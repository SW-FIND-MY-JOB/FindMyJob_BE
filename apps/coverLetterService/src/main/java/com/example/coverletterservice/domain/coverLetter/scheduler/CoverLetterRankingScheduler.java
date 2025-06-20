package com.example.coverletterservice.domain.coverLetter.scheduler;

import com.example.coverletterservice.domain.coverLetter.entity.CoverLetter;
import com.example.coverletterservice.domain.coverLetter.fallbackService.AuthFallbackService;
import com.example.coverletterservice.domain.coverLetter.repository.CoverLetterRepository;
import com.example.coverletterservice.global.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoverLetterRankingScheduler {
    private final CoverLetterRepository coverLetterRepository;
    private final AuthFallbackService authFallbackService;
    private final RedisUtil redisUtil;

    /* 등수별 지급 포인트 */
    private static final int[] BONUS = {
            10000, 5000, 3000, 1000, 1000,
            1000,  1000,  1000,  1000, 1000
    };

    //매주 월요일 00시 01분에 포인트 지급
    @Scheduled(cron = "0 1 0 ? * MON", zone = "Asia/Seoul")
    public void grantWeeklyBonus() {
        // 지난주 월요일 00:00 ~ 이번주 월요일 00:00
        LocalDateTime thisMon = LocalDate.now(ZoneId.of("Asia/Seoul"))
                .with(java.time.DayOfWeek.MONDAY)
                .atStartOfDay();
        LocalDateTime lastMon = thisMon.minusWeeks(1);

        // 해당 키가 있는지 확인
        if (redisUtil.existData("last_payment_day:")){
            // 포인트를 이미 지급했는지 검사
            if (redisUtil.getData("last_payment_day:").equals(lastMon.toLocalDate().toString())){
                return;
            }
        }

        // 주간 TOP10 조회
        List<CoverLetter> top = coverLetterRepository.findTopInWeek(lastMon, thisMon);

        int prevScore = -1;
        int bonusIdx = 0;    // BONUS 인덱스 (동점이면 그대로 유지)
        int rank = 0; // 순위 기준

        for (int i = 0; i < top.size(); i++) {
            CoverLetter coverLetter = top.get(i);

            // 점수가 다르면 랭킹, 보너스 인덱스 갱신
            if (coverLetter.getScore() != prevScore) {
                rank = i + 1;           // 새로운 점수 등장 → 새로운 순위
                bonusIdx = i;           // 해당 보너스 인덱스도 갱신
                prevScore = coverLetter.getScore();

                // 11등이면 끝
                if( rank > 10 ){
                    break;
                }
            }

            int bonus = BONUS[bonusIdx]; // 동점자도 같은 순위의 보너스

            String key = "weeklyBonus" + lastMon.toLocalDate() + ":rank:" + i + ":id:";

            // 중복 지급 체크
            if (redisUtil.existData(key) && redisUtil.getData(key).equals(coverLetter.getId().toString())) {
                continue;
            }

            // 포인트 지급
            authFallbackService.addUserPoint(coverLetter.getUserId(), bonus, "주간 TOP " + rank + " 리워드 적립");

            // 지급내역 기록 (10일 뒤 삭제)
            redisUtil.setData(key, coverLetter.getId().toString(), 60L * 60 * 24 * 10);
        }

        // 지급 완료 플래그
        redisUtil.setData("last_payment_day:", lastMon.toLocalDate().toString());
    }
}

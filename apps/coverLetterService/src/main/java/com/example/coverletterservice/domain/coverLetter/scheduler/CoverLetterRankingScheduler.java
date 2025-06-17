package com.example.coverletterservice.domain.coverLetter.scheduler;

import com.example.coverletterservice.domain.coverLetter.entity.CoverLetter;
import com.example.coverletterservice.domain.coverLetter.fallbackService.AuthFallbackService;
import com.example.coverletterservice.domain.coverLetter.repository.CoverLetterRepository;
import com.example.coverletterservice.global.redis.RedisUtil;
import jakarta.transaction.Transactional;
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

    //매주 월요일 00시 05분에 포인트 지급
    @Scheduled(cron = "0 5 0 ? * MON", zone = "Asia/Seoul")
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
        List<CoverLetter> top10 = coverLetterRepository.findTop10InWeek(lastMon, thisMon);

        // 순위별 포인트 차등 지급
        for (int i = 0; i < top10.size(); i++) {
            int bonus = BONUS[i];
            CoverLetter coverLetter = top10.get(i);
            String key = "weeklyBonus"+lastMon.toLocalDate().toString()+":rank:"+i+":id:";

            //중복 확인
            if(redisUtil.existData(key)){
                if (redisUtil.getData(key).equals(coverLetter.getId().toString())){
                    //중복된다면 넘어감
                    continue;
                }
            }

            //포인트 적립
            authFallbackService.addUserPoint(coverLetter.getUserId(), bonus);

            //Redis에 중복 지급 방지용 데이터 저장 (10일 뒤 파기)
            redisUtil.setData(key, coverLetter.getId().toString(), 60L * 60 * 24 * 10);
        }

        //이번주 포인트 지급 완료 플래그
        redisUtil.setData("last_payment_day:", lastMon.toLocalDate().toString());
    }
}

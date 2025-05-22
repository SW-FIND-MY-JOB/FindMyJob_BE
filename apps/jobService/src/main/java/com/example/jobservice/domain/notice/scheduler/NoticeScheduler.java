package com.example.jobservice.domain.notice.scheduler;

import com.example.jobservice.domain.agency.entity.Agency;
import com.example.jobservice.domain.agency.repository.AgencyRepository;
import com.example.jobservice.domain.notice.dto.notice.NoticeApiResponseDTO;
import com.example.jobservice.domain.notice.entity.Notice;
import com.example.jobservice.domain.notice.repository.NoticeRepository;
import com.example.jobservice.global.redis.RedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeScheduler {

    private final RedisUtil redisUtil;
    private final RestTemplate restTemplate = new RestTemplate();
    private final NoticeRepository noticeRepository;
    private final AgencyRepository agencyRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Value("${config.job-secret}")
    private String jobSecret;

    @PostConstruct
    public void init(){
        saveNoticeApi();
    }

    //1시간 마다 갱신
    @Scheduled(cron = "0 0 */1 * * *")
    public void noticeScheduler(){
        saveNoticeApi();
    }

    public void saveNoticeApi(){
        //한 페이지 결과 수
        int numOfRows = 100;

        //페이지 번호
        int pageNo = 1;

        //공고 채용 시작일 초기화
        String pbancBgngYmd;

        //redis에 저장된 기록 확인  key는 noticeRecrutStart
        if (redisUtil.existData("noticeRecrutStart")) {
            pbancBgngYmd = redisUtil.getData("noticeRecrutStart");
        } else {
            pbancBgngYmd = "20250101";
        }

        List<Notice> allNotices = new ArrayList<>();

        while (true) {
            int finalPageNo = pageNo;

            NoticeApiResponseDTO.NoticeApiResponse response = null;
            try {
                URI uri = new URI("https://apis.data.go.kr/1051000/recruitment/list" +
                        "?serviceKey=" + jobSecret +
                        "&numOfRows=" + numOfRows +
                        "&pageNo=" + finalPageNo +
                        "&pbancBgngYmd=" + pbancBgngYmd +
                        "&resultType=json");

                log.info("uri: {}", uri);

                response = restTemplate
                        .getForObject(uri, NoticeApiResponseDTO.NoticeApiResponse.class);
            } catch (URISyntaxException e) {
                log.error("api 호출 중 에러 {}", e.getMessage());
            }

            if (response == null || response.getResult() == null || response.getResult().isEmpty()) {
                break;
            }

            List<Notice> noticeList = response.getResult().stream()
                    .map(dto -> {
                        log.info("응답확인 {}", dto.getInstNm());
                        //기관명으로 기관 정보와 매핑 (없다면 null처리)
                        Agency agency = agencyRepository.findByInstNm(dto.getInstNm()).orElse(null);
                        return Notice.builder()
                                .id(dto.getRecrutPblntSn())
                                .instNm(dto.getInstNm())
                                .ncsCdNmLst(dto.getNcsCdNmLst())
                                .hireTypeNmLst(dto.getHireTypeNmLst())
                                .workRgnNmLst(dto.getWorkRgnNmLst())
                                .recruitSeNm(dto.getRecrutSeNm())
                                .prefCondCn(dto.getPrefCondCn())
                                .pbancBgngYmd(LocalDate.parse(dto.getPbancBgngYmd(), formatter))
                                .pbancEndYmd(LocalDate.parse(dto.getPbancEndYmd(), formatter))
                                .recrutPbancTtl(dto.getRecrutPbancTtl())
                                .srcUrl(dto.getSrcUrl())
                                .aplyQlfcCn(dto.getAplyQlfcCn())
                                .disqlfcRsn(dto.getDisqlfcRsn())
                                .scrnprcdrMthdExpln(dto.getScrnprcdrMthdExpln())
                                .acbgCondNmLst(dto.getAcbgCondNmLst())
                                .nonatchRsn(dto.getNonatchRsn())
                                .agency(agency)
                                .build();
                    })
                    .toList();

            allNotices.addAll(noticeList);
            pageNo++;
        }

        //현재 가져온 공고 Id를 토대로 존재하는 id만 파싱
        List<Long> existingIds = noticeRepository.findAllByIdIn(allNotices.stream()
                .map(Notice::getId)
                .toList())
            .stream()
            .map(Notice::getId)
            .toList();

        //새로운 공고와 null값 필터링
        List<Notice> newNotices = allNotices.stream()
            .filter(notice -> !existingIds.contains(notice.getId()))
            .filter(notice ->
                    notice.getInstNm() != null && notice.getNcsCdNmLst() != null
                    && notice.getHireTypeNmLst() != null && notice.getWorkRgnNmLst() != null
                    && notice.getRecruitSeNm() != null && notice.getPbancBgngYmd() != null
                    && notice.getPbancEndYmd() != null && notice.getRecrutPbancTtl() != null
                    && notice.getAcbgCondNmLst() != null && notice.getAgency() != null)
            .toList();

        log.info("새로운 공고 기사 개수 로그: {}", newNotices.size());

        noticeRepository.saveAll(newNotices);

        // 마지막 날짜를 Redis 저장
        newNotices.stream()
                .map(Notice::getPbancBgngYmd)
                .max(LocalDate::compareTo)
                .ifPresent(latestDate -> redisUtil.setData("noticeRecrutStart", latestDate.format(formatter)));
    }
}

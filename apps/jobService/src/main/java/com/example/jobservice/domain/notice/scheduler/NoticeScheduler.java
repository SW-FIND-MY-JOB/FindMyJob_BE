package com.example.jobservice.domain.notice.scheduler;

import com.example.jobservice.domain.agency.entity.Agency;
import com.example.jobservice.domain.agency.repository.AgencyRepository;
import com.example.jobservice.domain.notice.converter.NoticeConverter;
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
import java.util.stream.Collectors;

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
            pbancBgngYmd = "20250501";
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
                        return NoticeConverter.toNotice(dto, agency);
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

        for(Notice notice : allNotices){
            if (notice.getInstNm() == null) log.error("null - instNm: {}", notice.getId());
            if (notice.getNcsCdNmLst() == null) log.error("null - ncsCdNmLst: {}", notice.getId());
            if (notice.getNcsCdLst() == null) log.error("null - ncsCdLst: {}", notice.getId());
            if (notice.getHireTypeNmLst() == null) log.error("null - hireTypeNmLst: {}", notice.getId());
            if (notice.getHireTypeLst() == null) log.error("null - hireTypeLst: {}", notice.getId());
            if (notice.getWorkRgnNmLst() == null) log.error("null - workRgnNmLst: {}", notice.getId());
            if (notice.getWorkRgnLst() == null) log.error("null - workRgnLst: {}", notice.getId());
            if (notice.getRecrutSeNm() == null) log.error("null - recrutSeNm: {}", notice.getId());
            if (notice.getRecrutSe() == null) log.error("null - recrutSe: {}", notice.getId());
            if (notice.getPbancBgngYmd() == null) log.error("null - pbancBgngYmd: {}", notice.getId());
            if (notice.getPbancEndYmd() == null) log.error("null - pbancEndYmd: {}", notice.getId());
            if (notice.getRecrutPbancTtl() == null) log.error("null - recrutPbancTtl: {}", notice.getId());
            if (notice.getAcbgCondNmLst() == null) log.error("null - acbgCondNmLst: {}", notice.getId());
            if (notice.getAcbgCondLst() == null) log.error("null - acbgCondLst: {}", notice.getId());
            if (notice.getAgency() == null) log.error("null - agency: {}", notice.getId());
        }

        //새로운 공고와 null값 필터링
        List<Notice> newNotices = allNotices.stream()
            .filter(notice -> !existingIds.contains(notice.getId()))
            .filter(notice ->
                    notice.getInstNm() != null && notice.getNcsCdNmLst() != null
                    && notice.getNcsCdLst() != null && notice.getHireTypeNmLst() != null
                    && notice.getHireTypeLst() != null && notice.getWorkRgnNmLst() != null
                    && notice.getWorkRgnLst() != null && notice.getRecrutSeNm() != null
                    && notice.getRecrutSe() != null && notice.getPbancBgngYmd() != null
                    && notice.getPbancEndYmd() != null && notice.getRecrutPbancTtl() != null
                    && notice.getAcbgCondNmLst() != null && notice.getAcbgCondLst() != null
                    && notice.getAgency() != null)
//            .toList();
                .collect(Collectors.toList());
        log.info("새로운 공고 기사 개수 로그: {}", newNotices.size());

        noticeRepository.saveAll(newNotices);

        // 마지막 날짜를 Redis 저장
        newNotices.stream()
                .map(Notice::getPbancBgngYmd)
                .max(LocalDate::compareTo)
                .ifPresent(latestDate -> redisUtil.setData("noticeRecrutStart", latestDate.format(formatter)));
    }
}

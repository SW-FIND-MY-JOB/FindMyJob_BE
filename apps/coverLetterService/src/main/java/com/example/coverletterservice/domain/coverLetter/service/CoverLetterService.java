package com.example.coverletterservice.domain.coverLetter.service;

import com.example.coverletterservice.domain.coverLetter.converter.CoverLetterConverter;
import com.example.coverletterservice.domain.coverLetter.dto.CoverLetterReqDTO;
import com.example.coverletterservice.domain.coverLetter.dto.CoverLetterResDTO;
import com.example.coverletterservice.domain.coverLetter.entity.CoverLetter;
import com.example.coverletterservice.domain.coverLetter.exception.status.CoverLetterErrorStatus;
import com.example.coverletterservice.domain.coverLetter.fallbackService.AuthFallbackService;
import com.example.coverletterservice.domain.coverLetter.repository.CoverLetterRepository;
import com.example.coverletterservice.domain.coverLetter.repository.CoverLetterScrapRepository;
import com.example.coverletterservice.global.exception.GeneralException;
import com.example.coverletterservice.global.util.TokenUtil;
import com.example.jwtutillib.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoverLetterService {
    private final CoverLetterRepository coverLetterRepository;
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;
    private final WebClient openAiWebClient;
    private final CoverLetterScrapRepository coverLetterScrapRepository;
    private final AuthFallbackService authFallbackService;

    //자소서 저장
    @Transactional
    public CoverLetterResDTO.CoverLetterIdResDTO createCoverLetter(HttpServletRequest request, CoverLetterReqDTO.CoverLetterInformDTO coverLetterInfo){
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        //사용자 이름 추출
        String writer = jwtUtil.getName(token);

        //AI로 자소서 점수 판별
        Integer score = 0;
        try {
            score = coverLetterScoreFromAi(coverLetterInfo.getTitle(), coverLetterInfo.getContent());
        } catch (GeneralException e) {
            throw new GeneralException(CoverLetterErrorStatus._GPT_ERROR);
        }

        //만약 점수가 0이라면
        if (score.equals(0)){
            throw new GeneralException(CoverLetterErrorStatus._BAD_CONTENT);
        } else if (score.equals(1)){
            //만약 점수가 1이라면
            throw new GeneralException(CoverLetterErrorStatus._BAD_CONTENT2);
        } else if (score.equals(2)){
            //만약 점수가 2이라면
            throw new GeneralException(CoverLetterErrorStatus._NOT_COVER_LETTER_CONTENT);
        }

        
        //자소서 상위 몇 %인지 구하기
        //전체 자소서 개수
        long totalCount = 1;
        totalCount = coverLetterRepository.countAll();
        //내 자소서보다 낮은 자소서 개수
        long lowerCount = coverLetterRepository.countLowerThanScore(score);
        double percentile = ((double) lowerCount / totalCount) * 100;
        int percentileInt = (int) Math.floor(percentile);

        //자소서 구간별 갯수
        List<Integer> scores = coverLetterRepository.findAllScores();
        int[] counts = new int[20]; // 20구간 (0~1000 by 50)

        for (int s : scores) {
            int index = Math.min(s / 50, 19); // 1000점은 마지막 구간(19)에 포함
            counts[index]++;
        }

        // 구간 레이블 (ex: "0-49", "50-99", ..., "950-1000")
        List<String> bins = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int start = i * 50;
            int end = (i == 19) ? 1000 : (start + 49);
            bins.add(start + "-" + end);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bins", bins);
        result.put("counts", Arrays.stream(counts).boxed().toList());


        //자소서 저장
        CoverLetter coverLetter = CoverLetterConverter.toCoverLetter(coverLetterInfo, userId, writer, score);
        coverLetterRepository.save(coverLetter);

        //유저 포인트 점수에 따른 포인트 적립
        int point = 0;
        if(score < 300){
            point = 500;
        } else if (score < 500){
            point = 1000;
        } else if (score < 800){
            point = 1500;
        } else if (score < 900){
            point = 2000;
        } else if (score < 1000){
            point = 3000;
        } else if (score == 1000){
            point = 5000;
        }
        authFallbackService.addUserPoint(userId, point);

        // 저장된 자소서 정보 반환
        return CoverLetterResDTO.CoverLetterIdResDTO.builder()
                .id(coverLetter.getId())
                .score(score)
                .point(point)
                .percent(percentileInt)
                .scores(result)
                .build();
    }

    //단일 자소서 조회
    @Transactional
    public CoverLetterResDTO.CoverLetterDetailInformDTO SearchCoverLetter(HttpServletRequest request, Long coverLetterId){
        // 사용자 id 파싱
        Long userId = tokenUtil.getUserId(request);

        //자소서 꺼내오기
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new GeneralException(CoverLetterErrorStatus._NOT_EXIST_COVER_LETTER));

        //조회수 증가
        coverLetter.setViewCnt(coverLetter.getViewCnt() + 1);
        coverLetterRepository.save(coverLetter);

        //최신 자소서 리스트 가져오기
        List<CoverLetter> recentCoverLetterList = coverLetterRepository.findTop10ByOrderByCreatedAtDesc();

        // 현재 자소서 제외
        List<CoverLetter> filteredCoverLetterList = recentCoverLetterList.stream()
                .filter(c -> !c.getId().equals(coverLetterId))
                .toList();

        //dto로 변환
        List<CoverLetterResDTO.CoverLetterRecentInformDTO> recentCoverLetterDtoList = filteredCoverLetterList.stream()
                .map(CoverLetterConverter::toCoverLetterRecentInformDTO)
                .toList();

        //로그인 x
        if (userId == null){
            return CoverLetterConverter.toCoverLetterDetailInformDTO(coverLetter, false, false, recentCoverLetterDtoList);
        }
        //로그인 o
        else{
            //작성자 검증
            boolean isAuthor = userId.equals(coverLetter.getUserId());
            //스크랩 검증
            boolean isScrap = coverLetterScrapRepository.existsByCoverLetterAndUserId(coverLetter, userId);
            return CoverLetterConverter.toCoverLetterDetailInformDTO(coverLetter, isAuthor, isScrap, recentCoverLetterDtoList);
        }
    }

    //단일 자소서 내용 조회
    public String getCoverLetterContent(Long coverLetterId){
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new GeneralException(CoverLetterErrorStatus._NOT_EXIST_COVER_LETTER));

        return coverLetter.getContent();
    }

    //자소서 조건 검색 (기업명, 직무, 키워드)
    public Page<CoverLetterResDTO.CoverLetterInformDTO> searchCoverLetters(HttpServletRequest request, String instNm, String category, String keyword,
                                                                              int page, int size){
        // 사용자 id 파싱
        Long userId = tokenUtil.getUserId(request);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CoverLetter> result = coverLetterRepository.searchCoverLetter(instNm, category, keyword, pageable);

        //로그인 x
        if (userId == null){
            return result.map(coverLetter->CoverLetterConverter.toCoverLetterInformDTO(coverLetter, false));
        }
        //로그인 o
        return result.map(coverLetter->{
            //스크랩 검증
            boolean isScrap = coverLetterScrapRepository.existsByCoverLetterAndUserId(coverLetter, userId);
            return CoverLetterConverter.toCoverLetterInformDTO(coverLetter, isScrap);
        });
    }

    //내가 쓴 자소서 조회
    public Page<CoverLetterResDTO.CoverLetterInformDTO> seaarchMyCoverLetters(HttpServletRequest request, int page, int size){
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        //사용자가 작성한 자소서 가져오기
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CoverLetter> result = coverLetterRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);

        return result.map(coverLetter->CoverLetterConverter.toCoverLetterInformDTO(coverLetter, false));
    }

    //주간 랭킹 Top10 조회
    public List<CoverLetterResDTO.CoverLetterRankingResDTO> searchCoverLetterRanking(){
        // 이번주 월요일 00:00 ~ 다음주 월요일 00:00
        LocalDateTime thisMon = LocalDate.now(ZoneId.of("Asia/Seoul"))
                .with(java.time.DayOfWeek.MONDAY)
                .atStartOfDay();

        LocalDateTime nextMon = thisMon.plusWeeks(1);

        List<CoverLetter> coverLetterList = coverLetterRepository.findTopInWeek(thisMon, nextMon);

        int prevScore = -1;
        int prevRank = 0;
        int rank = 0;

        List<CoverLetterResDTO.CoverLetterRankingResDTO> rankingList = new ArrayList<>();
        for (int i = 0; i < coverLetterList.size(); i++) {
            CoverLetter coverLetter = coverLetterList.get(i);

            // 새 점수면 랭킹 업데이트
            if (coverLetter.getScore() != prevScore) {
                rank = i + 1;
                prevScore = coverLetter.getScore();
                prevRank = rank;

                //10등 초과면 멈추기
                if(prevRank > 10){
                    break;
                }
            }
            // 동점이면 이전 랭킹 그대로
            rankingList.add(CoverLetterConverter.toCoverLetterRankingResDTO(coverLetter, prevRank));
        }
        return rankingList;
    }


    //자소서 삭제
    @Transactional
    public void deleteCoverLetter(HttpServletRequest request, Long coverLetterId) {
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new GeneralException(CoverLetterErrorStatus._NOT_EXIST_COVER_LETTER));

        if (!userId.equals(coverLetter.getUserId())) {
            throw new GeneralException(CoverLetterErrorStatus._NOT_EQUAL_USER_COVER_LETTER);
        }

        coverLetterRepository.deleteById(coverLetterId);
    }

    // GPT 점수 산정 메서드
    public Integer coverLetterScoreFromAi(String title, String content) {
        return openAiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(createRequestBody(title, content))
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::extractContent)
                .block();
    }

    /** 🔹 GPT 프롬프트 */
    private Map<String, Object> createRequestBody(String title, String content) {
        return Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "system",
                                "content", "너는 자기소개서 평가 전문가야. 오직 숫자(0,1,2,100,101,...999,1000)만 반환해야 해. 해당 점수를 통해 사용자의 랭킹을 매기려고 해. 최대한 중복된 값이 나오지 않도록 깐깐하게 자소서를 읽고 점수를 매겨줘."),
                        Map.of("role", "user",
                                "content", String.format("""
                                   제목: %s
                                   본문: %s
                                   조건:
                                   1) 욕설·비방·비속어 포함 시 0
                                   2) 이상한 글자로 도배 시 1
                                   3) 자기소개서에 맞지 않은 글 작성 시 2
                                   4) 정상적인 글이면 100~1000 사이 점수를 1 단위로
                                   5) 다른 설명 없이 숫자만 출력
                                   """, title, content))
                ),
                "temperature", 0.0,
                "max_tokens", 10,
                "stream", false
        );
    }

    /** 🔹 응답 파싱 */
    @SuppressWarnings("unchecked")
    private Integer extractContent(Map<String, Object> response) {
        try {
            var choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new IllegalStateException("No choices in GPT response");
            }
            var message = (Map<String, Object>) choices.get(0).get("message");
            var content = (String) message.get("content");
            return Integer.valueOf(content.trim());  // " 100 " → 100
        } catch (NumberFormatException e) {
            log.error("GPT 응답이 숫자가 아님: {}", response);
            throw new GeneralException(CoverLetterErrorStatus._GPT_ERROR);
        } catch (Exception e) {
            log.error("GPT 파싱 오류:", e);
            throw new GeneralException(CoverLetterErrorStatus._GPT_ERROR);
        }
    }
}

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
        //내 자소서보다 높은 자소서 개수
        long upperCount = coverLetterRepository.countUpperThanScore(score);
        double percentile = ((double) upperCount / totalCount) * 100;
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
        authFallbackService.addUserPoint(userId, point, "자소서 작성 포인트 적립");

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
    public Page<CoverLetterResDTO.CoverLetterInformDTO> searchMyCoverLetters(HttpServletRequest request, int page, int size){
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
                        // ------ 예시 1 ------
                        Map.of("role", "user", "content", """
                            제목: 1. 공단이 지향하는 가치가 무엇이라고 생각하며, 그 가치를 창출하기 위해 본인은 어떠한 준비가 되어있는지, 본인의 역량을 개발하기 위하여 어떤 노력을 하였는지 구체적인 사례를 들어 기술해 주십시오.
                            본문: [소통과 협력으로 만드는 국민의 공무원연금공단이 지향하는 가치는 노후 생활]
                              ´고객 만족´이라고 생각합니다. 공단은 평소 고객에게 설문조사를 하여 고객이 진심으로 원하는 것이 무엇인지 파악하고 서비스를 제공하며 실현하는 것 같습니다. 시중금리 보다 낮은 금리로 자금을 대여해 주거나 공무원 연금을 지급하는 사업을 예로 들 수 있습니다. 공단의 가치에 맞는 사업을 함께 하고자 소통과 상생 협력 윤리를 배우는 여러 활동에 참여하며 역량을 개발해왔습니다. 교내에서 열린 스피치 대회와 학교 홍보 영상 제작 공모전에 참가하였습니다. 스피치 대회에서는 청중 앞에서 또렷한 목소리로 발표함으로써 유창한 전달 능력을 키웠고 이를 통해 말하기에 자신감을 얻었습니다. 제한된 시간 안에 발표하며 전달하고자 하는 내용을 효과적으로 전달하는 방법을 배울 수 있었습니다. 학교 홍보 영상을 제작하는 과정에서는 전달하고자 하는 내용을 구성하고 일정을 계획하는 등 제작 목적에 맞는 영상 제작 능력을 길렀습니다. 이러한 경험을 통해 고객의 요구에 맞는 적절한 의사 전달 능력을 키울 수 있었습니다.
                              공단에서 사회적 책임을 다하기 위해 학생회 임원으로서 책임감을 길렀습니다. 학생회는 전교생의 학교과의 협력을 통해 이해관계자 모두와 상생할 수 있도록 책임감을 느끼고 노력해야 한다고 생각했습니다. 이를 위해 학생들의 의견을 수렴하고 학교의 문제점을 파악하여 해결하는 방안을 모색했습니다. 또한 학생회에서 주최하는 행사를 기획하고 진행하는 과정에서 학생들의 안전과 편의를 최우선으로 고려하였습니다.
                              이러한 경험을 통해 의사소통 기술과 고객 관계 관리 방법, 사회적 책임을 다하기 위한 책임감을 갖춘 인재로 성장하였습니다. 공단에 입사하여 고객의 목소리에 귀 기울이고 고객의 요구에 맞는 서비스를 제공하여 고객 만족도를 높이는 데 기여하고 싶습니다. 이를 통해 공단의 비전인 ’국민의 안정된 노후 생활 보장‘을 달성할 수 있도록 노력하겠습니다.
                            조건:
                            1) 욕설·비방·비속어 포함 시 0
                            2) 이상한 글자로 도배 시 1
                            3) 자기소개서에 맞지 않은 글 작성 시 2
                            4) 정상적인 글이면 100~1000 사이 점수를 1 단위로
                            5) 다른 설명 없이 숫자만 출력
                        """),
                        Map.of("role", "assistant", "content", "911"),
                        // ------ 예시 2 ------
                        Map.of("role", "user", "content", """
                            제목: 조직이해능력 : 우리 회사에 지원한 동기 및 입사 후 회사 내에서 실천하고자 하는 목표를 본인의 역량과 결부시켜 기술하시오
                            본문: 한국전력공사에서 진행하는 사업 중 환경을 생각하는 전기 차 충전사업에 대해 관심이 있습니다. 뉴스를 보며 환경오염에 대한 심각함을 깨닫고 사소한 것부터라도 시작해 환경을 지켜야겠다는 생각이 들었습니다. 이때 조금이라도 환경을 위해 할 수 있는 방안을 생각해보던 중 한국전력공사가 환경을 지키는 다양한 에너지신사업을 진행한다는 것을 보고 인상 깊었습니다. 하지만, 이러한 사업에 대해 모르고 있는 사회적으로 소외된 사람들이 있는 것을 보았습니다. KEPCO는 전기 차 충전 사업을 시행하고 있지만 정작 필요한 사람들이 충전서비스를 제공받지 못하는 경우도 생길 것입니다. 이에 저는 지금까지 쌓아온 다양한 사무 행정 능력을 기반으로 사회적으로 소외되는 사람들 없이 대한민국 사람 모두가 혜택을 받을 수 있는 업무를 진행해 나가고 싶습니다. 또한, 대형 호텔 웨딩홀에서 아르바이트를 진행한 경험이 있어 고객의 민원이 들어오더라도 항상 고객의 입장에서 생각하여 업무처리를 원활하게 해결해 나갈 수 있습니다. 이렇듯, 소외되는 사람 한 명 없이, 대한민국에 사는 전 국민이 행복해지는 그날까지 고객을 지원하는 사원이 되고 싶습니다.
                            조건:
                            1) 욕설·비방·비속어 포함 시 0
                            2) 이상한 글자로 도배 시 1
                            3) 자기소개서에 맞지 않은 글 작성 시 2
                            4) 정상적인 글이면 100~1000 사이 점수를 1 단위로
                            5) 다른 설명 없이 숫자만 출력
                        """),
                        Map.of("role", "assistant", "content", "456"),
                        // ------ 예시 3 ------
                        Map.of("role", "user", "content", """
                            제목: ㅎㅎㅎㅎㅎㅎㅎㅎ
                            본문: ㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎ
                            조건:
                            1) 욕설·비방·비속어 포함 시 0
                            2) 이상한 글자로 도배 시 1
                            3) 자기소개서에 맞지 않은 글 작성 시 2
                            4) 정상적인 글이면 100~1000 사이 점수를 1 단위로
                            5) 다른 설명 없이 숫자만 출력
                        """),
                        Map.of("role", "assistant", "content", "1"),
                        // ------ 실제 사용자 입력 ------
                        Map.of("role", "user", "content", String.format("""
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

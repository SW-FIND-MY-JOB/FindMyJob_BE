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
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

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

        //AI로 자소서 포인트 적립금 판별
        Integer point = 0;
        try {
            point = coverLetterPointFromAi(coverLetterInfo.getTitle(), coverLetterInfo.getContent());
        } catch (GeneralException e) {
            throw new GeneralException(CoverLetterErrorStatus._GPT_ERROR);
        }

        //만약 포인트가 0이라면
        if (point.equals(0)){
            throw new GeneralException(CoverLetterErrorStatus._BAD_CONTENT);
        } else if (point.equals(1)){
            //만약 포인트가 1이라면
            throw new GeneralException(CoverLetterErrorStatus._BAD_CONTENT2);
        } else if (point.equals(2)){
            //만약 포인트가 2이라면
            throw new GeneralException(CoverLetterErrorStatus._NOT_COVER_LETTER_CONTENT);
        }

        //자소서 저장
        CoverLetter coverLetter = CoverLetterConverter.toCoverLetter(coverLetterInfo, userId);
        coverLetterRepository.save(coverLetter);

        //유저 포인트 적립
        authFallbackService.addUserPoint(userId, point);

        // 저장된 자소서 ID 반환
        return CoverLetterResDTO.CoverLetterIdResDTO.builder()
                .id(coverLetter.getId())
                .point(point)
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
    public Integer coverLetterPointFromAi(String title, String content) {
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
                                "content", "너는 자기소개서 평가 전문가야. 오직 숫자(0,1,2,500,510,...2990,3000)만 반환해야 해. 2000이상의 값 부터는 매우 엄격하게 점수를 매겨줘."),
                        Map.of("role", "user",
                                "content", String.format("""
                                   제목: %s
                                   본문: %s
                                   조건:
                                   1) 욕설·비방·비속어 포함 시 0
                                   2) 이상한 글자로 도배 시 1
                                   3) 자기소개서에 맞지 않은 글 작성 시 2
                                   4) 정상 글이면 500~3000 사이 점수를 10 단위로
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

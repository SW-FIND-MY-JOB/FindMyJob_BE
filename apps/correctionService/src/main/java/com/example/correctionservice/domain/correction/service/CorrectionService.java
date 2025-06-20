package com.example.correctionservice.domain.correction.service;

import com.example.correctionservice.domain.correction.client.CoverLetterServiceClient;
import com.example.correctionservice.domain.correction.dto.CorrectionReqDTO;
import com.example.correctionservice.domain.correction.dto.CorrectionResDTO;
import com.example.correctionservice.domain.correction.exception.status.CorrectionErrorStatus;
import com.example.correctionservice.domain.correction.fallbackService.AuthFallbackService;
import com.example.correctionservice.domain.correction.fallbackService.CoverLetterFallbackService;
import com.example.correctionservice.global.exception.GeneralException;
import com.example.correctionservice.global.util.TokenUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorrectionService {
    private final TokenUtil tokenUtil;
    private final WebClient openAiWebClient;
    private final ObjectMapper objectMapper;
    private final AuthFallbackService authFallbackService;
    private final CoverLetterFallbackService coverLetterFallbackService;

    //피드백 요청하는 서비스단
    public List<CorrectionResDTO.FeedbackResDTO> getCorrection(HttpServletRequest request, CorrectionReqDTO.CorrectionReqInform correctionReqInform){
        //토큰 검사
        String token = tokenUtil.checkToken(request);

        //사용자 추출
        Long userId = tokenUtil.getUserId(request);

        //포인트 사용 가능 여부 조회
        if( !authFallbackService.enoughUserPoint(userId, 300)){
            log.error("포인트가 부족합니다.");
            //포인트 부족 에러
            throw new GeneralException(CorrectionErrorStatus._NOT_ENOUGH_POINT);
        }
        log.info("포인트 사용 가능!");

        //다른 사람 자소서 내용 추출
        String otherContent = coverLetterFallbackService.getCoverLetterContent(correctionReqInform.getCoverLetterId());

        //내 자소서 내용 추출
        String myTitle = correctionReqInform.getTitle();
        String myContent = correctionReqInform.getContent();

        List<CorrectionResDTO.FeedbackResDTO> feedbackResDTOList = null;

        try {
            feedbackResDTOList = correctionFromAi(otherContent, myTitle, myContent);
        } catch ( FeignException e){
            log.error("GPT에러: {}", e.getMessage());
            throw new GeneralException(CorrectionErrorStatus._GPT_ERROR);
        }
        log.info("GPT 응답 성공!");

        //포인트 사용
        authFallbackService.useUserPoint(userId, 300, "AI 자소서 코칭 사용");
        return feedbackResDTOList;
    }

    public List<CorrectionResDTO.FeedbackResDTO> correctionFromAi(String otherContent, String myTitle, String myContent){
        List<CorrectionResDTO.FeedbackResDTO> correction = openAiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(createRequestBody(otherContent, myTitle, myContent))  // 요청 본문
                .retrieve()
                .bodyToMono(Map.class)  // 전체 응답을 한 번에 받음
                .map(response -> extractContent(response))  // 응답 본문에서 내용 추출
                .block();

        return correction;
    }

    // OpenAI 요청 본문 생성 (기사 요약)
    private Map<String, Object> createRequestBody(String otherContent, String myTitle, String myContent) {
        return Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "system", "content", "너는 자기소개서를 첨삭하는 전문가야."),
                        Map.of("role", "system", "content", "다음은 다른 사람이 작성한 자기소개서 내용이야." + otherContent),
                        Map.of("role", "system", "content", "이 내용을 참고해서 요청받은 자기소개서를 문장별로 나누고 응답을 반드시 JSON 형식으로 각 문장에 대해 빠짐없이 문법, 문장구조, 표현력, 추가적으로 개선해야할 부분에 대해 피드백을 제공해줘. 피드백 내용은 ~습니다 체로 끝맞춰줘." + otherContent),
                        Map.of("role", "user", "content", "다음은 자기소개서 제목이야. " + myTitle),
                        Map.of("role", "user", "content", "다음은 자기소개서 내용이야. " + myContent),
                        Map.of("role", "user", "content", "각 문장에 대해 비판적으로 검토하고, 각각에 아래 형식의 JSON 배열로 피드백 해줘. 만약 수정할 부분이 없다고 느껴진다면 editPoint에 null 이라고 적어줘" +
                                "응답 형식 예시: "+
                                "[ { \"content\": \"문장 내용\", \"goodPoint\": \"장점 설명\", \"editPoint\": \"수정할 점 설명\", \"editContent\": \"수정한 문장 내용\" } ...]")
                ),
                "temperature", 0.7,
                "stream", false  // 🔹 스트리밍 비활성화
        );
    }

    // OpenAI 응답에서 'content' 추출
    private List<CorrectionResDTO.FeedbackResDTO> extractContent(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                String contentJson = (String) message.get("content");

                //백틱 및 마크다운 제거
                String cleaned = contentJson
                        .replaceAll("(?s)```json", "")  // ```json 제거
                        .replaceAll("(?s)```", "")      // ``` 제거
                        .trim();

                // JSON 문자열 파싱
                return objectMapper.readValue(
                        cleaned,
                        new TypeReference<List<CorrectionResDTO.FeedbackResDTO>>() {
                        }
                );
            }
        } catch (Exception e){
            log.error("GPT 에러:", e);
            throw new GeneralException(CorrectionErrorStatus._GPT_ERROR);
        }
        throw new GeneralException(CorrectionErrorStatus._GPT_ERROR);
    }
}

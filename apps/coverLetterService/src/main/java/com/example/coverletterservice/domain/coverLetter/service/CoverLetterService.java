package com.example.coverletterservice.domain.coverLetter.service;

import com.example.coverletterservice.domain.coverLetter.client.AuthServiceClient;
import com.example.coverletterservice.domain.coverLetter.converter.CoverLetterConverter;
import com.example.coverletterservice.domain.coverLetter.dto.CoverLetterReqDTO;
import com.example.coverletterservice.domain.coverLetter.dto.CoverLetterResDTO;
import com.example.coverletterservice.domain.coverLetter.entity.CoverLetter;
import com.example.coverletterservice.domain.coverLetter.exception.status.CoverLetterErrorStatus;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoverLetterService {
    private final CoverLetterRepository coverLetterRepository;
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;
    private final CoverLetterScrapRepository coverLetterScrapRepository;
    private final AuthServiceClient authServiceClient;

    //자소서 저장
    @Transactional
    public CoverLetterResDTO.CoverLetterIdResDTO createCoverLetter(HttpServletRequest request, CoverLetterReqDTO.CoverLetterInformDTO coverLetterInfo){
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        CoverLetter coverLetter = CoverLetterConverter.toCoverLetter(coverLetterInfo, userId);
        coverLetterRepository.save(coverLetter);

        //유저 포인트 적립
        authServiceClient.addUserPoint(userId, 500);

        // 저장된 자소서 ID 반환
        return coverLetter.getId();
    }

    //단일 자조서 조회
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

    //자소서 삭제
    @Transactional
    public void deleteCoverLetter(HttpServletRequest request, Long coverLetterId){
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new GeneralException(CoverLetterErrorStatus._NOT_EXIST_COVER_LETTER));

        if(!userId.equals(coverLetter.getUserId())){
            throw new GeneralException(CoverLetterErrorStatus._NOT_EQUAL_USER_COVER_LETTER);
        }

        coverLetterRepository.deleteById(coverLetterId);
    }
}

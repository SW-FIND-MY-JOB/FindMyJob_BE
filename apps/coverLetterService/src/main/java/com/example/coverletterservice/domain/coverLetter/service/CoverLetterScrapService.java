package com.example.coverletterservice.domain.coverLetter.service;

import com.example.coverletterservice.domain.coverLetter.client.AuthServiceClient;
import com.example.coverletterservice.domain.coverLetter.converter.CoverLetterConverter;
import com.example.coverletterservice.domain.coverLetter.converter.CoverLetterScrapConverter;
import com.example.coverletterservice.domain.coverLetter.dto.CoverLetterResDTO;
import com.example.coverletterservice.domain.coverLetter.entity.CoverLetter;
import com.example.coverletterservice.domain.coverLetter.entity.CoverLetterScrap;
import com.example.coverletterservice.domain.coverLetter.exception.status.CoverLetterErrorStatus;
import com.example.coverletterservice.domain.coverLetter.repository.CoverLetterRepository;
import com.example.coverletterservice.domain.coverLetter.repository.CoverLetterScrapRepository;
import com.example.coverletterservice.global.exception.GeneralException;
import com.example.coverletterservice.global.util.TokenUtil;
import com.example.jwtutillib.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoverLetterScrapService {
    private final CoverLetterScrapRepository coverLetterScrapRepository;
    private final CoverLetterRepository coverLetterRepository;
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;

    // 스크랩한 자소서 보여주기
    public Page<CoverLetterResDTO.CoverLetterInformDTO> searchCoverLetterScrap(HttpServletRequest request, int page, int size){
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        //사용자 ID에 맞는 스크랩 공고 가져오기
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<CoverLetterScrap> result = coverLetterScrapRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);
        return result.map(coverLetterScrap -> CoverLetterConverter.toCoverLetterInformDTO(coverLetterScrap.getCoverLetter(), true));
    }

    //스크랩한 자소서 모두 보여주기
    public List<CoverLetterResDTO.CoverLetterInformDTO> searchAllCoverLetterScrap(HttpServletRequest request){
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        List<CoverLetterScrap> result = coverLetterScrapRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        return result.stream()
                .map(coverLetterScrap -> CoverLetterConverter.toCoverLetterInformDTO(coverLetterScrap.getCoverLetter(), true))
                .toList();
    }


    // 자소서 스크랩 저장
    @Transactional
    public void saveCoverLetterScrap(HttpServletRequest request, Long coverLetterId){
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        //자소서 정보 추출
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow( () -> new GeneralException(CoverLetterErrorStatus._NOT_EXIST_COVER_LETTER));

        //이미 스크랩 해둔 자소서인지 확인
        if (coverLetterScrapRepository.existsByCoverLetterAndUserId(coverLetter, userId)){
            throw new GeneralException(CoverLetterErrorStatus._ALREADY_EXIST_COVER_LETTER_SCRAP);
        }

        //entity 생성
        CoverLetterScrap coverLetterScrap = CoverLetterScrapConverter.toCoverLetterScrap(coverLetter, userId);

        coverLetterScrapRepository.save(coverLetterScrap);
    }

    //자소서 스크랩 해제
    @Transactional
    public void deleteCoverLetterScrap(HttpServletRequest request, Long coverLetterId){
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        //자소서 정보 추출
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow( () -> new GeneralException(CoverLetterErrorStatus._NOT_EXIST_COVER_LETTER));

        //해당 자소서 스크랩 정보 가져오기
        CoverLetterScrap coverLetterScrap = coverLetterScrapRepository.findByCoverLetterAndUserId(coverLetter, userId)
                .orElseThrow(()->new GeneralException(CoverLetterErrorStatus._NOT_EXIST_COVER_LETTER_SCRAP));

        coverLetterScrapRepository.delete(coverLetterScrap);
    }

    //사용자 ID에 관련된 정보 삭제
    @Transactional
    public void deleteUserIdCoverLetterScrap(Long userId){
        coverLetterScrapRepository.deleteAllByUserId(userId);
        log.info("해당 사용자의 자소서 스크랩이 삭제되었습니다.");
    }
}

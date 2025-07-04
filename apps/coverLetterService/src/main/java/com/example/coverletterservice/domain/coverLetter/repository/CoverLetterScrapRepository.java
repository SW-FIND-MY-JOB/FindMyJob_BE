package com.example.coverletterservice.domain.coverLetter.repository;

import com.example.coverletterservice.domain.coverLetter.entity.CoverLetter;
import com.example.coverletterservice.domain.coverLetter.entity.CoverLetterScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoverLetterScrapRepository extends JpaRepository<CoverLetterScrap, Long> {

    //존재 유무
    boolean existsByCoverLetterAndUserId(CoverLetter coverLetter, Long userId);

    //해당 자소서와 사용자ID에 맞는 스크랩 가져오기
    Optional<CoverLetterScrap> findByCoverLetterAndUserId(CoverLetter coverLetter, Long userId);

    //사용자 ID에 맞게 가져오기
    Page<CoverLetterScrap> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    //사용자 ID에 맞게 모두 가져오기
    List<CoverLetterScrap> findAllByUserIdOrderByCreatedAtDesc(Long userId);


    //사용자 ID에 관련된 정보 삭제
    void deleteAllByUserId(Long userId);
}

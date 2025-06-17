package com.example.coverletterservice.domain.coverLetter.repository;

import com.example.coverletterservice.domain.coverLetter.entity.CoverLetter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {

    //최신순으로 10개 조회
    List<CoverLetter> findTop10ByOrderByCreatedAtDesc();

    //조건 검색
    @Query("""
        SELECT n FROM CoverLetter n
        WHERE (:instNm = 'all' OR n.instNm LIKE %:instNm%)
          AND (:category = 'all' OR n.ncsCdNmLst LIKE %:category%)
          AND (:keyword = '' OR n.title LIKE %:keyword% OR n.content LIKE %:keyword%)
        ORDER BY n.createdAt DESC
    """)
    Page<CoverLetter> searchCoverLetter(
            @Param("instNm") String instNm,
            @Param("category") String category,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    //주간 Top 10 (점수 내림차순)
    @Query(value = """
        SELECT n FROM CoverLetter n
        WHERE n.createdAt >= :start
        AND n.createdAt <= :end
        ORDER BY n.point DESC
        LIMIT 10
    """)
    List<CoverLetter> findTop10InWeek(LocalDateTime start, LocalDateTime end);

    //사용자가 작성한 자소서 조회
    Page<CoverLetter> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}

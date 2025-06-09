package com.example.jobservice.domain.notice.repository;

import com.example.jobservice.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice> {
    List<Notice> findAllByIdIn(List<Long> ids);

    //조건 검색
    @Query("""
        SELECT n FROM Notice n
        WHERE (:region = 'all' OR n.workRgnLst LIKE %:region%)
          AND (:category = 'all' OR n.ncsCdLst LIKE %:category%)
          AND (:history = 'all' OR n.recrutSe LIKE %:history%)
          AND (:edu = 'all' OR n.acbgCondLst LIKE %:edu%)
          AND (:type = 'all' OR n.hireTypeLst LIKE %:type%)
          AND (:keyword = '' OR n.recrutPbancTtl LIKE %:keyword% OR n.instNm LIKE %:keyword%)
        ORDER BY n.pbancBgngYmd DESC
    """)
    Page<Notice> searchNotices(
            @Param("region") String region,
            @Param("category") String category,
            @Param("history") String history,
            @Param("edu") String edu,
            @Param("type") String type,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    //최근 공고 검색
    Page<Notice> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

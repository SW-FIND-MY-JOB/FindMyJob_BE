package com.example.jobservice.domain.notice.repository;

import com.example.jobservice.domain.notice.entity.Notice;
import com.example.jobservice.domain.notice.entity.NoticeScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NoticeScrapRepository extends JpaRepository<NoticeScrap, Long> {

    //존재 유무
    boolean existsByNoticeAndUserId(Notice notice, Long userId);

    //해당 공고와 사용자ID에 맞는 스크랩 가져오기
    Optional<NoticeScrap> findByNoticeAndUserId(Notice notice, Long userId);

    //사용자 ID에 맞게 가져오기
    Page<NoticeScrap> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    //사용자 ID와 날짜에 맞게 가져오기
    @Query("SELECT n FROM NoticeScrap n where n.userId = :userId AND " +
            "n.startDate >= :startOfMonth AND n.startDate <= :endOfMonth AND " +
            "n.endDate >= :startOfMonth AND n.endDate <= :endOfMonth")
    List<NoticeScrap> findNoticesWithinMonth(
            @Param("userId") Long userId,
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("endOfMonth") LocalDate endOfMonth);

    //사용자 ID에 따른 정보 삭제
    void deleteAllByUserId(Long userId);
}

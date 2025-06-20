package com.example.authservice.domain.point.repository;

import com.example.authservice.domain.point.entity.Point;
import com.example.authservice.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
    // 사용자 포인트 내역 정보 조회
    Page<Point> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}

package com.example.authservice.domain.point.service;

import com.example.authservice.domain.point.converter.PointConverter;
import com.example.authservice.domain.point.dto.PointResDTO;
import com.example.authservice.domain.point.entity.Point;
import com.example.authservice.domain.point.repository.PointRepository;
import com.example.authservice.domain.user.entity.User;
import com.example.authservice.domain.user.repository.UserRepository;
import com.example.authservice.global.exception.GeneralException;
import com.example.authservice.global.util.TokenUtil;
import com.example.jwtutillib.JwtUtil;
import com.example.responselib.apiPayload.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;

    //포인트 적립
    @Transactional
    public void updatePoint(boolean isAddPoint, int updatePoint, int balance, String description, User user) {
        Point point = Point.builder()
                .isAddPoint(isAddPoint)
                .updatePoint(updatePoint)
                .balance(balance)
                .description(description)
                .user(user)
                .build();
        pointRepository.save(point);
        log.info("포인트 내역이 저장되었습니다.");
    }

    //포인트 내역 조회
    public Page<PointResDTO.pointInformDTO> findAllByUser(HttpServletRequest request, int page, int size) {
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        //사용자 객체 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 사용자 포인트 내역 가져오기
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Point> result = pointRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        log.info("사용자 포인트 내역 가져오기 성공");
        return result.map(PointConverter::toPointResDTO);
    }
}

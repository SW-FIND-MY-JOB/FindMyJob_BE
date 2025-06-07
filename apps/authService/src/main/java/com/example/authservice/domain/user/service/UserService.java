package com.example.authservice.domain.user.service;

import com.example.authservice.domain.user.client.CoverLetterServiceClient;
import com.example.authservice.domain.user.client.JobServiceClient;
import com.example.authservice.domain.user.converter.UserConverter;
import com.example.authservice.domain.user.dto.UserReqDTO;
import com.example.authservice.domain.user.dto.UserResDTO;
import com.example.authservice.domain.user.entity.User;
import com.example.authservice.domain.user.exception.UserExceptionHandler;
import com.example.authservice.domain.user.exception.status.UserErrorStatus;
import com.example.authservice.domain.user.repository.UserRepository;
import com.example.authservice.global.exception.GeneralException;
import com.example.authservice.global.redis.RedisUtil;
import com.example.authservice.global.util.TokenUtil;
import com.example.jwtutillib.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenUtil tokenUtil;
    private final RedisUtil redisUtil;
    private final JobServiceClient jobServiceClient;
    private final CoverLetterServiceClient coverLetterServiceClient;

    //회원가입
    @Transactional
    public void join(UserReqDTO.JoinDTO joinDTO){
        //이메일 중복여부
        if (userRepository.existsByEmail(joinDTO.getEmail())){
            throw new UserExceptionHandler(UserErrorStatus._ALREADY_EXIST_EMAIL);
        }

        //인증된 메일 여부
        if (!redisUtil.existData("verify:" + joinDTO.getEmail()) || !redisUtil.getData("verify:" + joinDTO.getEmail()).equals("true")){
            throw new UserExceptionHandler(UserErrorStatus._NOT_VERIFY_EMAIL);
        }

        //사용자 생성
        User user = UserConverter.toUser(joinDTO, bCryptPasswordEncoder.encode(joinDTO.getPassword()));

        userRepository.save(user);
    }

    // 로그인
    public void login(UserReqDTO.LoginDTO loginDTO, HttpServletResponse response){
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        //이메일 존재여부
        if (!userRepository.existsByEmail(email)){
            throw new UserExceptionHandler(UserErrorStatus._NOT_EXIST_EMAIL);
        }

        User user = userRepository.findByEmail(email);

        //비밀번호 일치 여부
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new UserExceptionHandler(UserErrorStatus._NOT_EQUAL_PASSWORD);
        }

        Long accessExpiredMs = 1000*60*30L;
        Long refreshExpiredMs = 1000*60*60*24L;

        //토큰 생성
        String access = jwtUtil.createJwt("access", user.getId(), user.getEmail(), user.getName(), user.getRole(), accessExpiredMs);
        String refresh = jwtUtil.createJwt("refresh", user.getId(), user.getEmail(), user.getName(), user.getRole(), refreshExpiredMs);

        //redis에 refresh토큰이 있으면 제거
        if (redisUtil.existData("refresh:"+email)){
            redisUtil.deleteData("refresh:"+email);
        }
        //redis에 refresh토큰 저장
        redisUtil.setData("refresh:"+email, refresh, refreshExpiredMs);

        //쿠키 생성
        Cookie cookie = createCookie("refresh", refresh, 60 * 60 * 24 * 7);

        //응답 헤더에 토큰과 쿠키 삽입
        response.addHeader("Authorization", "Bearer " + access);
        response.addCookie(cookie);
    }

    //이메일 중복여부
    public Boolean isDuplicatedEmail(String mail){
        //이메일 중복여부
        return userRepository.existsByEmail(mail);
    }

    //로그아웃
    public void logout(HttpServletRequest request, HttpServletResponse response){
        //토큰 가져오기
        String refresh = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    log.info("refresh토큰 {}", refresh);
                    break;
                }
            }
        }

        //토큰이 없다면
        if(refresh == null || refresh.isEmpty()){
            return;
        }

        //refresh토큰이 redis에 존재한다면 삭제
        if(redisUtil.existData("refresh:" + jwtUtil.getEmail(refresh))){
            redisUtil.deleteData("refresh:" + jwtUtil.getEmail(refresh));
        }

        //빈 쿠키 생성
        Cookie cookie = createCookie("refresh", null, 0);
        response.addCookie(cookie);
    }

    //사용자 정보 반환
    public UserResDTO.userInformDTO getUserInform(HttpServletRequest request){
       String token = tokenUtil.checkToken(request);
       Long userId = jwtUtil.getUserId(token);
       User user = userRepository.findById(userId)
               .orElseThrow(() -> new UserExceptionHandler(UserErrorStatus._NOT_EXIST_USER));

       String name = user.getName();
       int point = user.getPoint();

       return UserResDTO.userInformDTO.builder()
               .name(name)
               .point(point)
               .build();
    }

    //비번 변경
    @Transactional
    public void setPw(UserReqDTO.SetPwDTO setPwDTO, String token){
        //token 파싱
        token = token.replace("Bearer ", "");

        User user = userRepository.findById(jwtUtil.getUserId(token))
                .orElseThrow(() -> new UserExceptionHandler(UserErrorStatus._NOT_EXIST_USER));

        //비밀번호 일치 여부
        if (!bCryptPasswordEncoder.matches(setPwDTO.getPassword(), user.getPassword())){
            throw new UserExceptionHandler(UserErrorStatus._NOT_EQUAL_PASSWORD);
        }

        //새 비밀번호로 업데이트
        user.setPassword(bCryptPasswordEncoder.encode(setPwDTO.getNewPassword()));
        userRepository.save(user);
    }

    //회원 탈퇴
    @Transactional
    public void deleteUser(UserReqDTO.DeleteUserDTO deleteUserDTO, String token){
        //token 파싱
        token = token.replace("Bearer ", "");

        User user = userRepository.findById(jwtUtil.getUserId(token))
                .orElseThrow(() -> new UserExceptionHandler(UserErrorStatus._NOT_EXIST_USER));

        //비밀번호 일치 여부
        if (!bCryptPasswordEncoder.matches(deleteUserDTO.getPassword(), user.getPassword())){
            throw new UserExceptionHandler(UserErrorStatus._NOT_EQUAL_PASSWORD);
        }

        //저장한 자소서 스크랩 삭제
        jobServiceClient.deleteUserNoticeScraps(user.getId());

        //저장한 공고 스크랩 삭제
        coverLetterServiceClient.deleteUserCoverLetterScraps(user.getId());

        //사용자 삭제
        userRepository.delete(user);
    }

    // 사용자 포인트 적립
    @Transactional
    public void addUserPoint(Long userId, Integer point){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserExceptionHandler(UserErrorStatus._NOT_EXIST_USER));

        //포인트 적립
        user.setPoint(user.getPoint() + point);
        userRepository.save(user);
        log.info("포인트 적립 성공");
    }

    // 사용자 포인트 사용
    @Transactional
    public void subUserPoint(Long userId, Integer point){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserExceptionHandler(UserErrorStatus._NOT_EXIST_USER));

        //포인트 사용 (포인트를 사용할 수 있는지 체크)
        if (user.getPoint() < point){
            log.warn("포인트 적립 실패");
            throw new GeneralException(UserErrorStatus._NOT_ENOUGH_POINT);
        }

        user.setPoint(user.getPoint() - point);
        userRepository.save(user);
        log.info("포인트 사용 성공");
    }

    // 사용자 포인트 사용 가능 조회
    public Boolean isEnoughPoint(Long userId, Integer point){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserExceptionHandler(UserErrorStatus._NOT_EXIST_USER));
        return user.getPoint() >= point;
    }

    private Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);       // 7일
        cookie.setPath("/");                  // 모든 경로에 대해 전송
        cookie.setHttpOnly(true);             // JS에서 접근 불가 (보안)
         cookie.setSecure(true);            // HTTPS만 허용 (배포 환경에서만 활성화)
        return cookie;
    }
}

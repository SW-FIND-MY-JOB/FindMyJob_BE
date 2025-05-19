package com.example.authservice.domain.mail.service;

import com.example.authservice.domain.mail.dto.MailReqDTO;
import com.example.authservice.domain.mail.exception.MailExceptionHandler;
import com.example.authservice.domain.mail.exception.status.MailErrorStatus;
import com.example.authservice.domain.user.service.UserService;
import com.example.authservice.global.redis.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final UserService userService;
    private final RedisUtil redisUtil;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${config.mail-username}")
    private String fromEmail;

    //메일 보내기
    public void sendMail(MailReqDTO.MailDTO mailDTO) throws MessagingException {
        //수신자 메일 가져옴
        String email = mailDTO.getEmail();

        //이미 가입된 메일인지 확인
        userService.isDuplicatedEmail(email);

        //인증번호 생성
        String authNum = createCode();

        //redis에 인증코드가 있으면 삭제
        if(redisUtil.existData("code: " + email)){
            redisUtil.deleteData("code: " + email);
        }

        //redis에 인증코드 저장 (30분)
        redisUtil.setData("code: " + email, authNum, 60 * 30L);

        //메일 양식 만들기
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            mimeMessage.addRecipients(MimeMessage.RecipientType.TO, email); //보낼 메일 주소
            mimeMessage.setSubject("안녕하세요. 내일찾기 회원가입 인증번호입니다."); //메일 제목
            mimeMessage.setFrom(fromEmail); //발신자
            mimeMessage.setText(setContext(authNum), "utf-8", "html");

            //메일 보내기
            javaMailSender.send(mimeMessage);
            log.info("send mail Success");
        } catch (MessagingException e) {
            log.error("send mail fail {}", String.valueOf(e));
            throw new MailExceptionHandler(MailErrorStatus._SEND_EMAIL_ERROR);
        }
    }

    // thymeleaf를 통한 html 적용
    public String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("mail", context);
    }

    // 인증번호 생성 메서드
    private String createCode(){
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); //6자리 숫자
        return String.valueOf(code);
    }

    // 코드 검증 메서드
    public void verifyEmailCode(MailReqDTO.VerifyCodeDTO verifyCodeDto){
        String email = verifyCodeDto.getEmail();
        String code = verifyCodeDto.getCode();

        String codeFoundByEmail = redisUtil.getData("code: " + email);

        //redis에 코드가 있는지 확인
        if (codeFoundByEmail == null) {
            throw new MailExceptionHandler(MailErrorStatus._NOT_EXIST_CODE);
        }

        // 코드가 일치하는지 확인
        if (!codeFoundByEmail.equals(code)){
            throw new MailExceptionHandler(MailErrorStatus._NOT_EQUAL_CODE);
        }

        // 서버에 인증된 메일 저장
        if (redisUtil.existData("verify: " + email)){
            redisUtil.deleteData("verify: " + email);
        }
        redisUtil.setData("verify: " + email, "true", 60 * 10);
    }
}

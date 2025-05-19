package com.example.authservice.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class JavaMailSenderConfig {
    @Value("${config.mail-host}")
    private String host;

    @Value("${config.mail-port}")
    private Integer port;

    @Value("${config.mail-username}")
    private String adminMail;

    @Value("${config.mail-password}")
    private String adminPassword;

    @Bean
    public JavaMailSender javaMailService() {
        final JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(adminMail);
        javaMailSender.setPassword(adminPassword);
        javaMailSender.setJavaMailProperties(getMailProperties());
        return javaMailSender;
    }

    private Properties getMailProperties(){
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true"); //포트 587을 사용하는 경우에 사용
//        properties.setProperty("mail.smtp.ssl.enable", "true"); 포트 465를 사용하는 경우에 사용
        return properties;
    }
}

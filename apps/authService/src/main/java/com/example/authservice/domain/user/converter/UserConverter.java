package com.example.authservice.domain.user.converter;

import com.example.authservice.domain.user.dto.UserReqDTO;
import com.example.authservice.domain.user.entity.User;

public class UserConverter {
    public static User toUser(UserReqDTO.JoinDTO joinDTO, String password){
        return User.builder()
                .name(joinDTO.getName())
                .email(joinDTO.getEmail())
                .password(password)
                .point(0)
                .role("USER")
                .build();

    }
}

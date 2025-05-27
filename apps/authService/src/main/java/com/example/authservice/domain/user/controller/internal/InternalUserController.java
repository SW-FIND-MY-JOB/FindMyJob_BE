package com.example.authservice.domain.user.controller.internal;

import com.example.authservice.domain.user.service.UserService;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/users")
public class InternalUserController {
    private final UserService userService;

    //포인트 적립
    @PutMapping("/{userId}/add-point")
    public ResponseEntity<Void> addUserPoint(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "1000") int point){
        userService.addUserPoint(userId, point);
        return ResponseEntity.ok().build();
    }

    //포인트 사용
    @PutMapping("/{userId}/use-point")
    public ResponseEntity<Void> useUserPoint(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "300") int point){
        userService.subUserPoint(userId, point);
        return ResponseEntity.ok().build();
    }
}

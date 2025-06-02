package com.example.authservice.domain.user.controller.internal;

import com.example.authservice.domain.user.service.UserService;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<Void> addUserPoint(@PathVariable @NotNull Long userId,
                                             @RequestParam @NotNull int point){
        userService.addUserPoint(userId, point);
        return ResponseEntity.ok().build();
    }

    //포인트 사용
    @PutMapping("/{userId}/use-point")
    public ResponseEntity<Void> useUserPoint(@PathVariable @NotNull Long userId,
                                             @RequestParam @NotNull int point){
        userService.subUserPoint(userId, point);
        return ResponseEntity.ok().build();
    }
    
    //포인트를 사용할 수 있는지 판별
    @GetMapping("/{userId}/enough-point")
    public ResponseEntity<Boolean> searchUserPoint(@PathVariable @NotNull Long userId,
                                                    @RequestParam @NotNull int point){
        Boolean isEnoughPoint = userService.isEnoughPoint(userId, point);
        return ResponseEntity.ok(isEnoughPoint);
    }
}

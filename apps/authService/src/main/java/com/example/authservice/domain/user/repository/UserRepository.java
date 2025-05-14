package com.example.authservice.domain.user.repository;


import com.example.authservice.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    //검증
    Boolean existsByEmail(String email);

    //찾기
    User findByEmail(String email);
}

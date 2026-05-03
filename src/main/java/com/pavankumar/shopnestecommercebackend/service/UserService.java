package com.pavankumar.shopnestecommercebackend.service;

import com.pavankumar.shopnestecommercebackend.dto.AuthResponse;
import com.pavankumar.shopnestecommercebackend.dto.RegisterRequest;
import com.pavankumar.shopnestecommercebackend.model.Role;
import com.pavankumar.shopnestecommercebackend.model.User;
import com.pavankumar.shopnestecommercebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("user already registered");
        }
        User user=User.builder()
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        return AuthResponse.builder()
                .role(user.getRole().name())
                .message("registration successfully")
                .build();
    }
}

package com.pavankumar.shopnestecommercebackend.util;

import com.pavankumar.shopnestecommercebackend.exception.ResourceNotFoundException;
import com.pavankumar.shopnestecommercebackend.model.User;
import com.pavankumar.shopnestecommercebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    private final UserRepository userRepository;


    public User getCurrentUser(){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));
    }
}

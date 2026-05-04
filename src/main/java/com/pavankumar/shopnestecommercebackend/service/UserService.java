package com.pavankumar.shopnestecommercebackend.service;

import com.pavankumar.shopnestecommercebackend.dto.AuthResponse;
import com.pavankumar.shopnestecommercebackend.dto.LoginRequest;
import com.pavankumar.shopnestecommercebackend.dto.RegisterRequest;
import com.pavankumar.shopnestecommercebackend.model.Role;
import com.pavankumar.shopnestecommercebackend.model.User;
import com.pavankumar.shopnestecommercebackend.repository.UserRepository;
import com.pavankumar.shopnestecommercebackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String email){
        User user=userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found"+email));
        return org.springframework.security.core.userdetails.
                User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }

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
    public AuthResponse login(LoginRequest request){
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        UserDetails userDetails=(UserDetails) authentication.getPrincipal();
        String token= jwtUtil.generateToken(userDetails);
        return AuthResponse.builder()
                .token(token)
                .role(userDetails.getAuthorities().toString())
                .message("login successful")
                .build();
    }
}

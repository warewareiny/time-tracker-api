package com.example.timetracker.auth.service;

import com.example.timetracker.auth.dto.JwtAuthenticationResponse;
import com.example.timetracker.auth.dto.SignInRequest;
import com.example.timetracker.auth.dto.SignUpRequest;
import com.example.timetracker.auth.entity.Role;
import com.example.timetracker.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signUp(SignUpRequest request) {

        log.info("Registering user {}", request.getUsername());

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userService.save(user);

        var jwt = jwtService.generateToken(savedUser);

        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {

        log.info("Authentication attempt {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());

        var jwt = jwtService.generateToken(userDetails);

        return new JwtAuthenticationResponse(jwt);
    }
}
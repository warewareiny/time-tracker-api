package com.example.timetracker.auth.service;

import com.example.timetracker.auth.dto.*;
import com.example.timetracker.auth.entity.RefreshToken;
import com.example.timetracker.auth.entity.Role;
import com.example.timetracker.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        User savedUser = userService.save(user);

        String accessToken = jwtService.generateToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.create(savedUser);

        return new JwtAuthenticationResponse(accessToken, refreshToken.getToken());
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userService.getByUsernameEntity(request.username());

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);

        return new JwtAuthenticationResponse(accessToken,refreshToken.getToken());
    }

    public JwtAuthenticationResponse refresh(RefreshTokenRequest request) {
        RefreshToken oldToken = refreshTokenService.verify(request.refreshToken());

        User user = oldToken.getUser();

        refreshTokenService.delete(oldToken);

        RefreshToken newToken = refreshTokenService.create(user);
        String accessToken = jwtService.generateToken(user);

        return new JwtAuthenticationResponse(accessToken, newToken.getToken());
    }
}
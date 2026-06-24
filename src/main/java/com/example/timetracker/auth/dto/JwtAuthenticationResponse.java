package com.example.timetracker.auth.dto;

public record JwtAuthenticationResponse(String accessToken,
                                        String refreshToken) {

}
package com.example.timetracker.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank
        String username,

        @NotBlank
        String email,

        @NotBlank
        String password
) {

}
package com.example.timetracker.auth.controller;

import com.example.timetracker.auth.dto.UpdateUserRequest;
import com.example.timetracker.auth.dto.UserResponse;
import com.example.timetracker.auth.mapper.UserMapper;
import com.example.timetracker.auth.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        return userMapper.toUserResponse(userService.getCurrentUser());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getById(@PathVariable Integer id) {
        return userMapper.toUserResponse(userService.findById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userService.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(toList());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/me")
    public void deleteCurrentUser() {
        userService.deleteById(userService.getCurrentUser().getId());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(@PathVariable Integer id) {
        userService.deleteById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/me")
    public UserResponse updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        return userMapper.toUserResponse(userService.updateCurrentUser(request));
    }
}
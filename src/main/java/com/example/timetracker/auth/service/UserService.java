package com.example.timetracker.auth.service;

import com.example.timetracker.auth.dto.UpdateUserRequest;
import com.example.timetracker.auth.dto.UserResponse;
import com.example.timetracker.auth.entity.User;
import com.example.timetracker.auth.exception.EmailAlreadyExistsException;
import com.example.timetracker.auth.exception.UserAlreadyExistsException;
import com.example.timetracker.auth.exception.UserNotFoundException;
import com.example.timetracker.auth.mapper.UserMapper;
import com.example.timetracker.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getByUsernameEntity(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public UserResponse create(User user) {
        log.info("Creating user {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException(user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public UserResponse getByUsername(String username) {
        return userMapper.toUserResponse(
                userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException(username))
        );
    }

    public UserResponse findById(Integer id) {
        return userMapper.toUserResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id))
        );
    }

    public List<UserResponse> findAll() {
        return userMapper.toUserResponses(userRepository.findAll());
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public UserResponse getCurrentUserResponse() {
        User user = getCurrentUser();
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateCurrentUser(UpdateUserRequest request) {
        User user = getCurrentUser();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteById(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
    }

    public void deleteCurrentUser() {
        User user = getCurrentUser();
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
package com.example.timetracker.auth.service;

import com.example.timetracker.auth.dto.UpdateUserRequest;
import com.example.timetracker.auth.entity.User;
import com.example.timetracker.auth.exception.EmailAlreadyExistsException;
import com.example.timetracker.auth.exception.UserAlreadyExistsException;
import com.example.timetracker.auth.exception.UserNotFoundException;
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

    public User save(User user) {
        log.debug("Saving user {}", user.getUsername());
        return userRepository.save(user);
    }

    public User create(User user) {
        log.info("Creating user {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("User with username {} already exists", user.getUsername());
            throw new UserAlreadyExistsException(user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("User with email {} already exists", user.getEmail());
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        log.info("User {} created successfully", user.getUsername());

        return save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User with username {} not found", username);
                    return new UsernameNotFoundException("User not found");
                });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getByUsername(username);
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();

        log.debug("Getting current user {}", username);

        return getByUsername(username);
    }

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with id {} not found", id);
                    return new UserNotFoundException(id);
                });
    }

    public void deleteById(Integer id) {
        log.info("Deleting user {}", id);

        userRepository.deleteById(id);

        log.info("User {} deleted", id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User updateCurrentUser(UpdateUserRequest request) {
        User currentUser = getCurrentUser();

        log.info("Updating user {}", currentUser.getId());

        currentUser.setUsername(request.getUsername());
        currentUser.setEmail(request.getEmail());

        log.info("User with id {} updated successfully", currentUser.getId());

        return save(currentUser);
    }
}
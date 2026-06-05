    package com.example.timetracker.service;

    import com.example.timetracker.dto.UpdateUserRequest;
    import com.example.timetracker.entity.User;
    import com.example.timetracker.repository.UserRepository;
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
    //            todo replace with custom exceptions
                throw new RuntimeException("User with this username already exists");
            }

            if (userRepository.existsByEmail(user.getEmail())) {
                log.warn("User with email {} already exists", user.getEmail());
                throw new RuntimeException("User with this email already exists");
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
                        return new UsernameNotFoundException("User not found");
                    });
        }

        public void deleteById(Integer id) {
            log.info("Deleting user {}", id);

            User user = findById(id);
            userRepository.delete(user);

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
    package com.example.timetracker.service;

    import com.example.timetracker.dto.UpdateUserRequest;
    import com.example.timetracker.entity.User;
    import com.example.timetracker.repository.UserRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class UserService implements UserDetailsService {

        private final UserRepository userRepository;

        public User save(User user) {
            return userRepository.save(user);
        }

        public User create(User user) {
            if (userRepository.existsByUsername(user.getUsername())) {
    //            todo replace with custom exceptions
                throw new RuntimeException("User with this username already exists");
            }

            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("User with this email already exists");
            }

            return save(user);
        }

        public User getByUsername(String username) {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
            return getByUsername(username);
        }

        public User findById(Integer id) {
            return userRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }

        public void deleteById(Integer id) {
            User user = findById(id);
            userRepository.delete(user);
        }

        public List<User> findAll() {
            return userRepository.findAll();
        }

        public User updateCurrentUser(UpdateUserRequest request) {
            User currentUser = getCurrentUser();

            currentUser.setUsername(request.getUsername());
            currentUser.setEmail(request.getEmail());

            return save(currentUser);
        }
    }
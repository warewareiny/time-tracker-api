package com.example.timetracker.unit;

import com.example.timetracker.entity.Role;
import com.example.timetracker.entity.User;
import com.example.timetracker.exception.EmailAlreadyExistsException;
import com.example.timetracker.exception.UserAlreadyExistsException;
import com.example.timetracker.exception.UserNotFoundException;
import com.example.timetracker.repository.UserRepository;
import com.example.timetracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private static final User TEST_USER_IVAN = new User(1,
            "Ivan",
            "email@mail.ru",
            "test-pass",
            Role.USER,
            Instant.now(),
            Instant.now(),
            null);

    @Test
    void shouldReturnUserById() {
        when(userRepository.findById(1))
                .thenReturn(Optional.of(TEST_USER_IVAN));
        var result = userService.findById(1);

        assertThat(result.getUsername()).isEqualTo("Ivan");
        assertThat(result.getEmail()).isEqualTo("email@mail.ru");
        assertThat(result.getPasswordHash()).isEqualTo("test-pass");
        assertThat(result.getRole()).isEqualTo(Role.USER);
        assertThat(result.getTasks()).isEqualTo(null);
    }

    @Test
    void shouldThrowWhenUserByIdNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(1))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(1);
    }

    @Test
    void shouldCallDeleteById() {
        userService.deleteById(1);
        verify(userRepository).deleteById(1);
    }

    @Test
    void shouldCallSave() {
        userService.save(TEST_USER_IVAN);
        verify(userRepository).save(TEST_USER_IVAN);
    }

    @Test
    void shouldCallCreate() {
        when(userRepository.existsByUsername("Ivan")).thenReturn(false);
        when(userRepository.existsByEmail("email@mail.ru")).thenReturn(false);
        when(userRepository.save(TEST_USER_IVAN)).thenReturn(TEST_USER_IVAN);

        var result = userService.create(TEST_USER_IVAN);

        assertThat(result).isEqualTo(TEST_USER_IVAN);
        verify(userRepository).save(TEST_USER_IVAN);
    }

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("Ivan")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(TEST_USER_IVAN))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).existsByUsername("Ivan");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("email@mail.ru")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(TEST_USER_IVAN))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository).existsByEmail("email@mail.ru");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldFindByUsername() {
        when(userRepository.findByUsername("Ivan"))
                .thenReturn(Optional.of(TEST_USER_IVAN));

        var result = userService.getByUsername("Ivan");

        assertThat(result).isEqualTo(TEST_USER_IVAN);
        verify(userRepository).findByUsername("Ivan");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("Ivan")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByUsername("Ivan"))
                .isInstanceOf(UsernameNotFoundException.class);

        verify(userRepository).findByUsername("Ivan");
    }

    @Test
    void shouldFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(TEST_USER_IVAN));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(TEST_USER_IVAN);

        verify(userRepository).findAll();
    }
}

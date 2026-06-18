package com.example.timetracker.integration.controller;

import com.example.timetracker.TestcontainersConfiguration;
import com.example.timetracker.auth.entity.Role;
import com.example.timetracker.auth.entity.User;
import com.example.timetracker.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class UserControllerTestIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRepository.save(
                User.builder()
                        .username("user1")
                        .email("user1@test.com")
                        .passwordHash(passwordEncoder.encode("password1"))
                        .role(Role.USER)
                        .build()
        );

        userRepository.save(
                User.builder()
                        .username("user2")
                        .email("user2@test.com")
                        .passwordHash(passwordEncoder.encode("password2"))
                        .role(Role.USER)
                        .build()
        );
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void shouldReturnCurrentUser() throws Exception {
        mvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@test.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturnAllUsers() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andExpect(jsonPath("$[0].email").value("user1@test.com"))
                .andExpect(jsonPath("$[1].email").value("user2@test.com"));
    }

    @WithMockUser(username = "user1", roles = "USER")
    @Test
    void shouldDeleteCurrentUser() throws Exception {
        mvc.perform(delete("/users/me"))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findByUsername("user1")).isEmpty();
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void shouldDeleteUserById() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(1)).isEmpty();
    }
}
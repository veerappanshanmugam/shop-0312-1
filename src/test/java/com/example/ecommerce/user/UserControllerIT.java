package com.example.ecommerce.user;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ecommerce.model.User;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_success() throws Exception {
        String requestBody = """
                {"email": "alice@example.com", "name": "Alice"}
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", is("alice@example.com")))
                .andExpect(jsonPath("$.name", is("Alice")))
                .andExpect(jsonPath("$.created_at", notNullValue()));
    }

    @Test
    void createUser_duplicateEmail_returns400() throws Exception {
        User existing = new User("alice@example.com", "Alice");
        userRepository.save(existing);

        String requestBody = """
                {"email": "alice@example.com", "name": "Alice Two"}
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Email already registered")));
    }

    @Test
    void getUser_existing_returns200() throws Exception {
        User user = new User("bob@example.com", "Bob");
        user = userRepository.save(user);

        mockMvc.perform(get("/users/{user_id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.email", is("bob@example.com")))
                .andExpect(jsonPath("$.name", is("Bob")))
                .andExpect(jsonPath("$.created_at", notNullValue()));
    }

    @Test
    void getUser_nonExisting_returns404() throws Exception {
        mockMvc.perform(get("/users/{user_id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", is("User not found")));
    }

    @Test
    void listUsers_empty() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listUsers_returnsAll() throws Exception {
        userRepository.save(new User("alice@example.com", "Alice"));
        userRepository.save(new User("bob@example.com", "Bob"));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}

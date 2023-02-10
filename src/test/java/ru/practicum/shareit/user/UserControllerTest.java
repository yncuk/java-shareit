package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserController userController;

    @Order(1)
    @Test
    @DisplayName("Create user test")
    void createUserTest() throws Exception {
        // when
        User user = User.builder()
                .name("user3")
                .email("user3@user.com")
                .build();
        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        Optional<UserDto> userOptional = Optional.ofNullable(userController.findById(3));
        // then
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 3)
                                .hasFieldOrPropertyWithValue("name", "user3")
                                .hasFieldOrPropertyWithValue("email", "user3@user.com")
                );
    }

    @Order(2)
    @Test
    @DisplayName("Create user with duplicate email")
    void createUserWithDuplicateEmail() throws Exception {
        // when
        User user = User.builder()
                .name("user2")
                .email("user@user.com")
                .build();
        // then
        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(3)
    @Test
    @DisplayName("Create user without email")
    void createUserWithoutEmail() throws Exception {
        // when
        User user = User.builder()
                .name("user2")
                .build();
        // then
        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(4)
    @ParameterizedTest(name = "{index}. Check create user with bad email: \"{arguments}\"")
    @ValueSource(strings = {" ", "  ", "nickname@", "mail ru"})
    @DisplayName("Create user with bad email")
    void createUserWithBadEmail(String mail) throws Exception {
        // when
        User user = User.builder()
                .name("user2")
                .email(mail)
                .build();
        // then
        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(5)
    @Test
    @DisplayName("Update user test")
    void updateUserTest() throws Exception {
        // when
        User user = User.builder()
                .name("updated")
                .email("updated@user.com")
                .build();
        mockMvc.perform(patch("/users/3")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        Optional<UserDto> userOptional = Optional.ofNullable(userController.findById(3));
        // then
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 3)
                                .hasFieldOrPropertyWithValue("name", "updated")
                                .hasFieldOrPropertyWithValue("email", "updated@user.com")
                );
    }

    @Order(6)
    @Test
    @DisplayName("Update user with exist email")
    void updateUserWithExistEmail() throws Exception {
        // when
        User user = User.builder()
                .name("user")
                .email("user123@user.com")
                .build();
        userController.create(user);
        user = user.withEmail("updated@user.com");
        // then
        mockMvc.perform(patch("/users/3")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        Optional<UserDto> userOptional = Optional.ofNullable(userController.findById(3));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 3)
                                .hasFieldOrPropertyWithValue("name", "updated")
                                .hasFieldOrPropertyWithValue("email", "updated@user.com")
                );
    }

    @Order(7)
    @Test
    @DisplayName("Update user with only name or only email")
    void updateUserWithOnlyNameOrOnlyEmail() throws Exception {
        // when
        User user = User.builder()
                .name("updatedName")
                .build();
        User user1 = User.builder()
                .email("updatedEmail@user.com")
                .build();
        // then
        mockMvc.perform(patch("/users/2")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(patch("/users/2")
                        .content(new ObjectMapper().writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        Optional<UserDto> userOptional = Optional.ofNullable(userController.findById(2));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user2 ->
                        assertThat(user2).hasFieldOrPropertyWithValue("id", 2)
                                .hasFieldOrPropertyWithValue("name", "updatedName")
                                .hasFieldOrPropertyWithValue("email", "updatedEmail@user.com")
                );
    }

    @Order(8)
    @Test
    @DisplayName("Get all user")
    void getAllUser() throws Exception {
        // then
        mockMvc.perform(get("/users"))
                .andExpect(status().is2xxSuccessful());

    }

    @Order(9)
    @Test
    @DisplayName("Get user before delete")
    void getUserBeforeDelete() throws Exception {
        // then
        mockMvc.perform(get("/users/2"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.email").value("updatedEmail@user.com"));
    }

    @Order(10)
    @Test
    @DisplayName("Delete user and get after")
    void deleteUserAndGetAfter() throws Exception {
        // then
        mockMvc.perform(delete("/users/2"))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(get("/users/2"))
                .andExpect(status().is4xxClientError());
    }
}
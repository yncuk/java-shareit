package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.JpaTest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql({"classpath:schema.sql"})
class UserControllerTest extends JpaTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Order(1)
    @Test
    @DisplayName("Create user test")
    void createUserTest() throws Exception {
        // when
        User user = new User();
        user.setName("user1");
        user.setEmail("user1@user.com");
        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        Optional<User> userOptional = userRepository.findById(1);
        // then
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "user1")
                                .hasFieldOrPropertyWithValue("email", "user1@user.com")
                );
    }

    @Order(2)
    @Test
    @DisplayName("Create user with duplicate email")
    void createUserWithDuplicateEmail() throws Exception {
        // when
        User user = new User();
        user.setName("user2");
        user.setEmail("user1@user.com");
        // then
        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(3)
    @Test
    @DisplayName("Create user without email")
    void createUserWithoutEmail() throws Exception {
        // when
        User user = new User();
        user.setName("user2");
        // then
        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(4)
    @ParameterizedTest(name = "{index}. Check create user with bad email: \"{arguments}\"")
    @ValueSource(strings = {" ", "  ", "nickname@", "mail ru"})
    @DisplayName("Create user with bad email")
    void createUserWithBadEmail(String mail) throws Exception {
        // when
        User user = new User();
        user.setName("user2");
        user.setEmail(mail);
        // then
        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(5)
    @Test
    @DisplayName("Update user test")
    void updateUserTest() throws Exception {
        // when
        User user = new User();
        user.setName("updated");
        user.setEmail("updated@user.com");
        mockMvc.perform(patch("/users/1")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        Optional<User> userOptional = userRepository.findById(1);
        // then
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "updated")
                                .hasFieldOrPropertyWithValue("email", "updated@user.com")
                );
    }

    @Order(6)
    @Test
    @DisplayName("Update user with exist email")
    void updateUserWithExistEmail() throws Exception {
        // when
        User user = new User();
        user.setName("user");
        user.setEmail("user123@user.com");
        userRepository.save(user);
        user.setEmail("updated@user.com");
        // then
        mockMvc.perform(patch("/users/2")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        Optional<User> userOptional = userRepository.findById(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "updated")
                                .hasFieldOrPropertyWithValue("email", "updated@user.com")
                );
    }

    @Order(7)
    @Test
    @DisplayName("Update user with only name or only email")
    void updateUserWithOnlyNameOrOnlyEmail() throws Exception {
        // when
        User user = new User();
        user.setName("updatedName");
        User user1 = new User();
        user.setEmail("updatedEmail@user.com");
        // then
        mockMvc.perform(patch("/users/3")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(patch("/users/3")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user1)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        Optional<User> userOptional = userRepository.findById(3);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user2 ->
                        assertThat(user2).hasFieldOrPropertyWithValue("id", 3)
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
        mockMvc.perform(get("/users/3"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.email").value("updatedEmail@user.com"));
    }

    @Order(10)
    @Test
    @DisplayName("Delete user and get after")
    void deleteUserAndGetAfter() throws Exception {
        // then
        mockMvc.perform(delete("/users/3"))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(get("/users/3"))
                .andExpect(status().is4xxClientError());
    }
}
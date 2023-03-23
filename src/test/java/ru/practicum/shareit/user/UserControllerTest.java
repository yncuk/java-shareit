package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.JpaTest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Sql({"classpath:schema.sql", "classpath:user/data.sql"})
class UserControllerTest extends JpaTest {
    @Autowired
    private MockMvc mockMvc;

    // given data.sql
    @Test
    @DisplayName("Create user test")
    void createUserTest() throws Exception {
        // when
        User user = new User();
        user.setName("admin");
        user.setEmail("admin@admin.com");
        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name", is("admin")))
                .andExpect(jsonPath("$.email", is("admin@admin.com")));
    }

    @Test
    @DisplayName("Create user with duplicate email")
    void createUserWithDuplicateEmail() throws Exception {
        // when
        User user = new User();
        user.setName("user_duplicate");
        user.setEmail("user_duplicate@mail.ru");
        // then
        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

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

    @Test
    @DisplayName("Update user test")
    void updateUserTest() throws Exception {
        // when
        User user = new User();
        user.setName("updated");
        user.setEmail("updated@user.com");
        // then
        mockMvc.perform(patch("/users/3")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("updated")))
                .andExpect(jsonPath("$.email", is("updated@user.com")));
    }

    @Test
    @DisplayName("Update user with exist email")
    void updateUserWithExistEmail() throws Exception {
        // when
        User user = new User();
        user.setName("exist");
        user.setEmail("exist_email@mail.ru");
        // then
        mockMvc.perform(patch("/users/3")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Update user with only name or only email")
    void updateUserWithOnlyNameOrOnlyEmail() throws Exception {
        // when
        User user = new User();
        user.setName("update_ex");
        User user1 = new User();
        user1.setEmail("for_update_ex@mail.ru");
        // then
        mockMvc.perform(patch("/users/6")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.name", is("update_ex")))
                .andExpect(jsonPath("$.email", is("for_update@mail.ru")));
        mockMvc.perform(patch("/users/6")
                        .content(new ObjectMapper().writeValueAsString(UserMapper.toUserDto(user1)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.name", is("update_ex")))
                .andExpect(jsonPath("$.email", is("for_update_ex@mail.ru")));
    }

    @Test
    @DisplayName("Get all user")
    void getAllUser() throws Exception {
        // then
        mockMvc.perform(get("/users"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Delete user and get after")
    void deleteUserAndGetAfter() throws Exception {
        // then
        mockMvc.perform(delete("/users/7"))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(get("/users/7"))
                .andExpect(status().is4xxClientError());
    }
}
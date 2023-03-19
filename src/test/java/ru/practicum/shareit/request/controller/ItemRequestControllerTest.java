package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.JpaTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql({"classpath:schema.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemRequestControllerTest extends JpaTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Order(1)
    @Test
    void createRequestTest() throws Exception {
        // given
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.com");
        userRepository.save(user);
        // when
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Хочу найти пилу");
        // then
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Хочу найти пилу")));
    }

    @Order(2)
    @Test
    void findAllByRequesterTest() throws Exception {
        // then
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is("Хочу найти пилу")));
    }

    @Order(3)
    @Test
    void findAllTest() throws Exception {
        // given
        User user = new User();
        user.setName("user2");
        user.setEmail("user2@user.com");
        userRepository.save(user);
        // then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is("Хочу найти пилу")));
    }

    @Order(4)
    @Test
    void findAllBadFromAndSize() throws Exception {
        // then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("from", "-1")
                        .queryParam("size", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Order(5)
    @Test
    void findAllEmptyList() throws Exception {
        // then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("from", "1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Order(6)
    @Test
    void findByIdTest() throws Exception {
        // then
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Хочу найти пилу")));
    }
}
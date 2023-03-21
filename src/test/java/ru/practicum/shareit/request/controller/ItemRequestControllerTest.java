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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({"classpath:schema.sql", "classpath:item_request/data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemRequestControllerTest extends JpaTest {
    @Autowired
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        // given data.sql
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void createRequestTest() throws Exception {
        // when
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Хочу найти молоток");
        // then
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Хочу найти молоток")));
    }

    @Test
    void findAllByRequesterTest() throws Exception {
        // then
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(2)))
                .andExpect(jsonPath("$.[0].description", is("Хочу найти пилу")));
    }

    @Test
    void findAllTest() throws Exception {
        // then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(2)))
                .andExpect(jsonPath("$.[0].description", is("Хочу найти пилу")));
    }

    @Test
    void findAllBadFromAndSize() throws Exception {
        // then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("from", "-1")
                        .queryParam("size", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllEmptyList() throws Exception {
        // then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("from", "2"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Test
    void findByIdTest() throws Exception {
        // then
        mockMvc.perform(get("/requests/2")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.description", is("Хочу найти пилу")));
    }
}
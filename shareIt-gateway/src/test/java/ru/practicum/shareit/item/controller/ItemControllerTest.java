package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ShareItGateway.class})
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Create item with empty description")
    void createItemWithEmptyDescription() throws Exception {
        // when
        ItemRequestDto itemDto = new ItemRequestDto();
        itemDto.setName("Отвертка2");
        itemDto.setDescription("");
        itemDto.setAvailable(true);
        // then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Create item with empty name")
    void createItemWithEmptyName() throws Exception {
        // when
        ItemRequestDto itemDto = new ItemRequestDto();
        itemDto.setName("");
        itemDto.setDescription("Обычная отвертка2");
        itemDto.setAvailable(true);
        // then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Create item without available")
    void createItemWithoutAvailable() throws Exception {
        // when
        ItemRequestDto itemDto = new ItemRequestDto();
        itemDto.setName("Отвертка2");
        itemDto.setDescription("Обычная отвертка2");
        // then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}
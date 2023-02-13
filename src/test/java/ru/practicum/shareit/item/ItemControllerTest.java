package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ItemController itemController;
    @Autowired
    private UserController userController;

    @Order(1)
    @Test
    @DisplayName("Create item test")
    void createItemTest() throws Exception {
        // when
        User user = User.builder()
                .name("user")
                .email("user@user.com")
                .build();
        userController.create(UserMapper.toUserDto(user));
        Item item = Item.builder()
                .name("Отвертка")
                .description("Обычная отвертка")
                .available(true)
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        Optional<ItemDto> itemOptional = Optional.ofNullable(itemController.findById(1));
        // then
        assertThat(itemOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Отвертка")
                                .hasFieldOrPropertyWithValue("description", "Обычная отвертка")
                                .hasFieldOrPropertyWithValue("available", true)
                );
    }

    @Order(2)
    @Test
    @DisplayName("Create item without header X-Sharer-User-Id")
    void createItemWithoutHeader() throws Exception {
        // when
        Item item = Item.builder()
                .name("Отвертка2")
                .description("Обычная отвертка2")
                .available(true)
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        // then
        mockMvc.perform(post("/items")
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(3)
    @Test
    @DisplayName("Create item with not found user")
    void createItemWithNotFoundUser() throws Exception {
        // when
        Item item = Item.builder()
                .name("Отвертка2")
                .description("Обычная отвертка2")
                .available(true)
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        // then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 10)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(4)
    @Test
    @DisplayName("Create item without available")
    void createItemWithoutAvailable() throws Exception {
        // when
        Item item = Item.builder()
                .name("Отвертка2")
                .description("Обычная отвертка2")
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        // then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(5)
    @Test
    @DisplayName("Create item with empty name")
    void createItemWithEmptyName() throws Exception {
        // when
        Item item = Item.builder()
                .name("")
                .description("Обычная отвертка2")
                .available(true)
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        // then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(6)
    @Test
    @DisplayName("Create item with empty description")
    void createItemWithEmptyDescription() throws Exception {
        // when
        Item item = Item.builder()
                .name("Отвертка2")
                .description("")
                .available(true)
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        // then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(7)
    @Test
    @DisplayName("Update item test")
    void updateItemTest() throws Exception {
        // when
        Item item = Item.builder()
                .name("Отвертка+")
                .description("Обычная отвертка+")
                .available(false)
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        // then
        Optional<ItemDto> itemOptional = Optional.ofNullable(itemController.findById(1));
        assertThat(itemOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Отвертка+")
                                .hasFieldOrPropertyWithValue("description", "Обычная отвертка+")
                                .hasFieldOrPropertyWithValue("available", false)
                );
    }

    @Order(8)
    @Test
    @DisplayName("Update item without header X-Sharer-User-Id")
    void updateItemWithoutHeader() throws Exception {
        // when
        Item item = Item.builder()
                .name("Отвертка2")
                .description("Обычная отвертка2")
                .available(false)
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        // then
        mockMvc.perform(patch("/items/1")
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(9)
    @Test
    @DisplayName("Update item with user 2")
    void updateItemWithUser2() throws Exception {
        // when
        User user = User.builder()
                .name("user2")
                .email("user2@user.com")
                .build();
        userController.create(UserMapper.toUserDto(user));
        Item item = Item.builder()
                .name("Отвертка2")
                .description("Обычная отвертка2")
                .available(false)
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        // then
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(10)
    @Test
    @DisplayName("Update not found item")
    void updateNotFoundItem() throws Exception {
        // when
        Item item = Item.builder()
                .name("Отвертка2")
                .description("Обычная отвертка2")
                .available(false)
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        // then
        mockMvc.perform(patch("/items/10")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(11)
    @Test
    @DisplayName("Update item available")
    void updateItemAvailable() throws Exception {
        // when
        Item item = Item.builder()
                .available(true)
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        // then
        Optional<ItemDto> itemOptional = Optional.ofNullable(itemController.findById(1));
        assertThat(itemOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Отвертка+")
                                .hasFieldOrPropertyWithValue("description", "Обычная отвертка+")
                                .hasFieldOrPropertyWithValue("available", true)
                );
    }

    @Order(12)
    @Test
    @DisplayName("Update item description")
    void updateItemDescription() throws Exception {
        // when
        Item item = Item.builder()
                .description("Обычная отвертка-")
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        // then
        Optional<ItemDto> itemOptional = Optional.ofNullable(itemController.findById(1));
        assertThat(itemOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Отвертка+")
                                .hasFieldOrPropertyWithValue("description", "Обычная отвертка-")
                                .hasFieldOrPropertyWithValue("available", true)
                );
    }

    @Order(13)
    @Test
    @DisplayName("Update item name")
    void updateItemName() throws Exception {
        // when
        Item item = Item.builder()
                .name("Отвертка-")
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        // then
        Optional<ItemDto> itemOptional = Optional.ofNullable(itemController.findById(1));
        assertThat(itemOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Отвертка-")
                                .hasFieldOrPropertyWithValue("description", "Обычная отвертка-")
                                .hasFieldOrPropertyWithValue("available", true)
                );
    }

    @Order(14)
    @Test
    @DisplayName("Get all items")
    void getAllItems() throws Exception {
        // then
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());
    }

    @Order(15)
    @Test
    @DisplayName("Search item ОтВертк")
    void searchItemTest() throws Exception {
        // then
        mockMvc.perform(get("/items/search?text=ОтВертк")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Отвертка-"))
                .andExpect(jsonPath("$[0].description").value("Обычная отвертка-"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Order(16)
    @Test
    @DisplayName("Search item обЫчная in description")
    void searchItemByDescription() throws Exception {
        // then
        mockMvc.perform(get("/items/search?text=обЫчная")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Отвертка-"))
                .andExpect(jsonPath("$[0].description").value("Обычная отвертка-"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Order(17)
    @Test
    @DisplayName("Search item empty request")
    void searchItemEmptyRequest() throws Exception {
        // then
        mockMvc.perform(get("/items/search?text=")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Order(18)
    @Test
    @DisplayName("Get not exist item")
    void getNotExistItem() throws Exception {
        // then
        mockMvc.perform(get("/items/10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }
}
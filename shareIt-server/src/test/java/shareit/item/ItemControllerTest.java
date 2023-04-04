package shareit.item;

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
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import shareit.JpaTest;

import java.time.LocalDateTime;

import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {ShareItServer.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Sql({"classpath:schema.sql", "classpath:item/data.sql"})
class ItemControllerTest extends JpaTest {
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
    @DisplayName("Create item test")
    void createItemTest() throws Exception {
        // when
        Item item = new Item();
        item.setName("Отвертка");
        item.setDescription("Обычная отвертка");
        item.setAvailable(true);
        // then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(ItemMapper.toItemDto(item)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Отвертка"))
                .andExpect(jsonPath("$.description").value("Обычная отвертка"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("Create item without header X-Sharer-User-Id")
    void createItemWithoutHeader() throws Exception {
        // when
        Item item = new Item();
        item.setName("Отвертка2");
        item.setDescription("Обычная отвертка2");
        item.setAvailable(true);
        // then
        mockMvc.perform(post("/items")
                        .content(new ObjectMapper().writeValueAsString(ItemMapper.toItemDto(item)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Create item with not found user")
    void createItemWithNotFoundUser() throws Exception {
        // when
        Item item = new Item();
        item.setName("Отвертка2");
        item.setDescription("Обычная отвертка2");
        item.setAvailable(true);
        // then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 99)
                        .content(new ObjectMapper().writeValueAsString(ItemMapper.toItemDto(item)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Update item test")
    void updateItemTest() throws Exception {
        // when
        Item item = new Item();
        item.setName("Отвертка+");
        item.setDescription("Обычная отвертка+");
        item.setAvailable(false);
        // then
        mockMvc.perform(patch("/items/3")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(ItemMapper.toItemDto(item)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Отвертка+"))
                .andExpect(jsonPath("$.description").value("Обычная отвертка+"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    @DisplayName("Update item without header X-Sharer-User-Id")
    void updateItemWithoutHeader() throws Exception {
        // when
        Item item = new Item();
        item.setName("Отвертка2");
        item.setDescription("Обычная отвертка2");
        item.setAvailable(false);
        // then
        mockMvc.perform(patch("/items/3")
                        .content(new ObjectMapper().writeValueAsString(ItemMapper.toItemDto(item)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Update item with user 2")
    void updateItemWithUser2() throws Exception {
        // when
        Item item = new Item();
        item.setName("Отвертка2");
        item.setDescription("Обычная отвертка2");
        item.setAvailable(false);
        // then
        mockMvc.perform(patch("/items/3")
                        .header("X-Sharer-User-Id", 2)
                        .content(new ObjectMapper().writeValueAsString(ItemMapper.toItemDto(item)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Update not found item")
    void updateNotFoundItem() throws Exception {
        // when
        Item item = new Item();
        item.setName("Отвертка2");
        item.setDescription("Обычная отвертка2");
        item.setAvailable(false);
        // then
        mockMvc.perform(patch("/items/99")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(ItemMapper.toItemDto(item)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Update item available")
    void updateItemAvailable() throws Exception {
        // when
        Item item = new Item();
        item.setAvailable(true);
        // then
        mockMvc.perform(patch("/items/4")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(ItemMapper.toItemDto(item)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Для обновления available"))
                .andExpect(jsonPath("$.description").value("Для обновления available"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("Update item description")
    void updateItemDescription() throws Exception {
        // when
        Item item = new Item();
        item.setDescription("Обычная отвертка-");
        // then
        mockMvc.perform(patch("/items/5")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(ItemMapper.toItemDto(item)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Для обновления description"))
                .andExpect(jsonPath("$.description").value("Обычная отвертка-"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("Update item name")
    void updateItemName() throws Exception {
        // when
        Item item = new Item();
        item.setName("Отвертка-");
        // then
        mockMvc.perform(patch("/items/6")
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(ItemMapper.toItemDto(item)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.name").value("Отвертка-"))
                .andExpect(jsonPath("$.description").value("Для обновления name"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("Get all items")
    void getAllItems() throws Exception {
        // then
        mockMvc.perform(get("/items?from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Search item ИскЛюЧ")
    void searchItemTest() throws Exception {
        // then
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("text", "ИскЛюЧ")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].name").value("Исключительное слово"))
                .andExpect(jsonPath("$[0].description").value("Исключительное слово"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @DisplayName("Search item опиСАниЮ in description")
    void searchItemByDescription() throws Exception {
        // then
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("text", "опиСАниЮ")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].id").value(8))
                .andExpect(jsonPath("$[0].name").value("Для поиска"))
                .andExpect(jsonPath("$[0].description").value("Поиск по описанию"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @DisplayName("Get not exist item")
    void getNotExistItem() throws Exception {
        // then
        mockMvc.perform(get("/items/99")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Get exist item")
    void getExistItem() throws Exception {
        // then
        mockMvc.perform(get("/items/8")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.name").value("Для поиска"))
                .andExpect(jsonPath("$.description").value("Поиск по описанию"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("Search bad from and size")
    void searchBadFromSize() throws Exception {
        // then
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("from", "-1")
                        .queryParam("size", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Find all bad from and size")
    void findAllBadFromSize() throws Exception {
        // then
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("from", "-1")
                        .queryParam("size", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Find by id next and last booking and comment")
    void findByIdWithNextAndLastBooking_AndComment() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.now().plusSeconds(2).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusSeconds(4).withNano(0);
        BookingDtoInput bookingDtoInput = new BookingDtoInput();
        bookingDtoInput.setItemId(9);
        bookingDtoInput.setStart(start);
        bookingDtoInput.setEnd(end);
        // when
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("approved", "true"))
                .andExpect(status().is2xxSuccessful());
        sleep(2050);

        Comment comment = new Comment();
        comment.setText("Новый коммент");

        // then
        mockMvc.perform(post("/items/9/comment")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Новый коммент")))
                .andExpect(jsonPath("$.authorName", is("user2")));

        sleep(3000);

        LocalDateTime start2 = LocalDateTime.now().plusSeconds(3).withNano(0);
        LocalDateTime end2 = LocalDateTime.now().plusSeconds(60).withNano(0);
        BookingDtoInput bookingDtoInput2 = new BookingDtoInput();
        bookingDtoInput2.setItemId(9);
        bookingDtoInput2.setStart(start2);
        bookingDtoInput2.setEnd(end2);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(bookingDtoInput2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(patch("/bookings/2")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("approved", "true"))
                .andExpect(status().is2xxSuccessful());
        // then
        mockMvc.perform(get("/items/9")
                        .header("X-Sharer-User-Id", 3))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(9)))
                .andExpect(jsonPath("$.name", is("Пила")))
                .andExpect(jsonPath("$.description", is("Пила необычная")))
                .andExpect(jsonPath("$.available", is(true)));
    }
}
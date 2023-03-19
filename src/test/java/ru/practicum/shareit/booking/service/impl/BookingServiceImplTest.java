package ru.practicum.shareit.booking.service.impl;

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
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Sql(scripts = {"classpath:schema.sql"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingServiceImplTest extends JpaTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private static final LocalDateTime start = LocalDateTime.now().plusSeconds(60).withNano(0);
    private static final LocalDateTime end = LocalDateTime.now().plusHours(1).withNano(0);

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Order(1)
    @Test
    void createBooking() throws Exception {
        // given
        User user = new User();
        user.setName("user");
        user.setEmail("user@mail.ru");
        userRepository.save(user);

        User user1 = new User();
        user1.setName("user2");
        user1.setEmail("user2@mail.ru");
        userRepository.save(user1);

        Item item = new Item();
        item.setName("Отвертка");
        item.setDescription("Обычная отвертка");
        item.setAvailable(true);
        item.setOwner(1);
        itemRepository.save(item);

        BookingDtoInput bookingDtoInput = new BookingDtoInput();
        bookingDtoInput.setItemId(1);
        bookingDtoInput.setStart(start);
        bookingDtoInput.setEnd(end);

        // then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())))
                .andExpect(jsonPath("$.booker.id", is(2)))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Order(2)
    @Test
    void createBookingBadRequests() throws Exception {
        // given
        BookingDtoInput bookingDtoInput = new BookingDtoInput();
        bookingDtoInput.setItemId(1);
        bookingDtoInput.setStart(start);
        bookingDtoInput.setEnd(end);

        // then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 99)
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        bookingDtoInput.setItemId(99);
        bookingDtoInput.setStart(start);
        bookingDtoInput.setEnd(end);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        bookingDtoInput.setItemId(1);
        bookingDtoInput.setStart(start);
        bookingDtoInput.setEnd(start);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        bookingDtoInput.setItemId(1);
        bookingDtoInput.setStart(end);
        bookingDtoInput.setEnd(start);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(3)
    @Test
    void createBookingItemNotAvailable() throws Exception {
        // given
        Item item = new Item();
        item.setName("Отвертка2");
        item.setDescription("Обычная отвертка2");
        item.setAvailable(false);
        item.setOwner(1);
        itemRepository.save(item);

        BookingDtoInput bookingDtoInput = new BookingDtoInput();
        bookingDtoInput.setItemId(2);
        bookingDtoInput.setStart(start);
        bookingDtoInput.setEnd(end);

        // then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(4)
    @Test
    void findByIdWhenBookingFound() throws Exception {
        // then
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())))
                .andExpect(jsonPath("$.booker.id", is(2)))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Order(5)
    @Test
    void findByIdWhenBookingNotFound() throws Exception {
        // then
        mockMvc.perform(get("/bookings/2")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Order(6)
    @Test
    void findByIdWhenUserNotFound() throws Exception {
        // then
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().is4xxClientError());
    }

    @Order(7)
    @Test
    void findAllByBookerNotFound() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().is4xxClientError());
    }

    @Order(8)
    @Test
    void findAllByBookerBadFrom() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("from", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Order(9)
    @Test
    void findAllByBookerBadSize() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("size", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Order(10)
    @Test
    void findAllByBookerUnknownState() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("state", "BEGINNING"))
                .andExpect(status().is4xxClientError());
    }

    @Order(11)
    @Test
    void findAllByBookerAndStateAll() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("state", "ALL"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].start", is(start.toString())))
                .andExpect(jsonPath("$.[0].end", is(end.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(2)))
                .andExpect(jsonPath("$.[0].item.id", is(1)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Order(12)
    @Test
    void findAllByBookerAndStateCurrent() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("state", "CURRENT"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Order(13)
    @Test
    void findAllByBookerAndStatePast() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("state", "PAST"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Order(14)
    @Test
    void findAllByBookerAndStateFuture() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("state", "FUTURE"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].start", is(start.toString())))
                .andExpect(jsonPath("$.[0].end", is(end.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(2)))
                .andExpect(jsonPath("$.[0].item.id", is(1)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Order(15)
    @Test
    void findAllByBookerAndStateWaiting() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("state", "WAITING"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].start", is(start.toString())))
                .andExpect(jsonPath("$.[0].end", is(end.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(2)))
                .andExpect(jsonPath("$.[0].item.id", is(1)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Order(16)
    @Test
    void findAllByOwnerNotFound() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().is4xxClientError());
    }

    @Order(17)
    @Test
    void findAllByOwnerBadFrom() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("from", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Order(18)
    @Test
    void findAllByOwnerBadSize() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("size", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Order(19)
    @Test
    void findAllByOwnerUnknownState() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("state", "BEGINNING"))
                .andExpect(status().is4xxClientError());
    }

    @Order(20)
    @Test
    void findAllByOwnerAndStateAll() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("state", "ALL"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].start", is(start.toString())))
                .andExpect(jsonPath("$.[0].end", is(end.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(2)))
                .andExpect(jsonPath("$.[0].item.id", is(1)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Order(21)
    @Test
    void findAllByOwnerAndStateCurrent() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("state", "CURRENT"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Order(22)
    @Test
    void findAllByOwnerAndStatePast() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("state", "PAST"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Order(23)
    @Test
    void findAllByOwnerAndStateFuture() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("state", "FUTURE"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].start", is(start.toString())))
                .andExpect(jsonPath("$.[0].end", is(end.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(2)))
                .andExpect(jsonPath("$.[0].item.id", is(1)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Order(24)
    @Test
    void findAllByOwnerAndStateWaiting() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("state", "WAITING"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].start", is(start.toString())))
                .andExpect(jsonPath("$.[0].end", is(end.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(2)))
                .andExpect(jsonPath("$.[0].item.id", is(1)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Order(25)
    @Test
    void updateBookingApproved() throws Exception {
        // then
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("approved", "true"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())))
                .andExpect(jsonPath("$.booker.id", is(2)))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Order(26)
    @Test
    void updateBookingUserNotFound() throws Exception {
        // then
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 99)
                        .queryParam("approved", "true"))
                .andExpect(status().is4xxClientError());
    }

    @Order(27)
    @Test
    void updateBookingOwnerNotFound() throws Exception {
        // then
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("approved", "true"))
                .andExpect(status().is4xxClientError());
    }

    @Order(28)
    @Test
    void updateBookingRejected() throws Exception {
        // given
        Item item = new Item();
        item.setName("Отвертка3");
        item.setDescription("Обычная отвертка3");
        item.setAvailable(true);
        item.setOwner(2);
        itemRepository.save(item);

        BookingDtoInput bookingDtoInput = new BookingDtoInput();
        bookingDtoInput.setItemId(3);
        bookingDtoInput.setStart(start);
        bookingDtoInput.setEnd(end);
        // when
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        // then
        mockMvc.perform(patch("/bookings/2")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("approved", "false"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())))
                .andExpect(jsonPath("$.booker.id", is(1)))
                .andExpect(jsonPath("$.item.id", is(3)))
                .andExpect(jsonPath("$.status", is(BookingStatus.REJECTED.toString())));
    }
}
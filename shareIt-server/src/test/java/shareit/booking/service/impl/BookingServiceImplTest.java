package shareit.booking.service.impl;

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
import shareit.JpaTest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoInput;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {ShareItServer.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Sql(scripts = {"classpath:schema.sql", "classpath:booking/data.sql"})
class BookingServiceImplTest extends JpaTest {

    @Autowired
    private MockMvc mockMvc;
    private static final LocalDateTime start = LocalDateTime.now().plusSeconds(60).withNano(0);
    private static final LocalDateTime end = LocalDateTime.now().plusHours(1).withNano(0);
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        // given data.sql
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void createBooking() throws Exception {
        // given
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

    @Test
    void createBookingItemNotAvailable() throws Exception {
        // when
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

    @Test
    void findByIdWhenBookingFound() throws Exception {
        // given
        LocalDateTime start2 = LocalDateTime.of(2023, 3, 10, 20, 1, 20);
        LocalDateTime end2 = LocalDateTime.of(2023, 3, 10, 21, 1, 20);
        // then
        mockMvc.perform(get("/bookings/10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.start", is(start2.toString())))
                .andExpect(jsonPath("$.end", is(end2.toString())))
                .andExpect(jsonPath("$.booker.id", is(2)))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void findByIdWhenBookingNotFound() throws Exception {
        // then
        mockMvc.perform(get("/bookings/99")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findByIdWhenUserNotFound() throws Exception {
        // then
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllByBookerNotFound() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllByBookerBadFrom() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("from", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllByBookerBadSize() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("size", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllByBookerUnknownState() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("state", "BEGINNING"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllByBookerAndStateAll() throws Exception {
        // given
        LocalDateTime start2 = LocalDateTime.of(2020, 4, 10, 20, 1, 20);
        LocalDateTime end2 = LocalDateTime.of(2020, 4, 10, 21, 1, 20);
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 4)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(18)))
                .andExpect(jsonPath("$.[0].start", is(start2.toString())))
                .andExpect(jsonPath("$.[0].end", is(end2.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(4)))
                .andExpect(jsonPath("$.[0].item.id", is(3)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void findAllByBookerAndStateCurrent() throws Exception {
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("state", "CURRENT")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Test
    void findAllByBookerAndStatePast() throws Exception {
        // given
        LocalDateTime start2 = LocalDateTime.of(2020, 3, 10, 20, 1, 20);
        LocalDateTime end2 = LocalDateTime.of(2020, 3, 10, 21, 1, 20);
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .queryParam("state", "PAST")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(16)))
                .andExpect(jsonPath("$.[0].start", is(start2.toString())))
                .andExpect(jsonPath("$.[0].end", is(end2.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(3)))
                .andExpect(jsonPath("$.[0].item.id", is(3)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void findAllByBookerAndStateFuture() throws Exception {
        // given
        LocalDateTime start2 = LocalDateTime.of(2024, 3, 10, 20, 1, 20);
        LocalDateTime end2 = LocalDateTime.of(2024, 3, 10, 21, 1, 20);
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .queryParam("state", "FUTURE")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(17)))
                .andExpect(jsonPath("$.[0].start", is(start2.toString())))
                .andExpect(jsonPath("$.[0].end", is(end2.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(3)))
                .andExpect(jsonPath("$.[0].item.id", is(3)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void findAllByBookerAndStateWaiting() throws Exception {
        // given
        LocalDateTime start2 = LocalDateTime.of(2020, 4, 10, 20, 1, 20);
        LocalDateTime end2 = LocalDateTime.of(2020, 4, 10, 21, 1, 20);
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 4)
                        .queryParam("state", "WAITING")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(18)))
                .andExpect(jsonPath("$.[0].start", is(start2.toString())))
                .andExpect(jsonPath("$.[0].end", is(end2.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(4)))
                .andExpect(jsonPath("$.[0].item.id", is(3)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void findAllByOwnerNotFound() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllByOwnerBadFrom() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("from", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllByOwnerBadSize() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("size", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllByOwnerUnknownState() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("state", "BEGINNING"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findAllByOwnerAndStateAll() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 3)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Test
    void findAllByOwnerAndStateCurrent() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 3)
                        .queryParam("state", "CURRENT")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Test
    void findAllByOwnerAndStatePast() throws Exception {
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 3)
                        .queryParam("state", "PAST")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Test
    void findAllByOwnerAndStateFuture() throws Exception {
        // given
        LocalDateTime start2 = LocalDateTime.of(2024, 4, 10, 20, 1, 20);
        LocalDateTime end2 = LocalDateTime.of(2024, 4, 10, 21, 1, 20);
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 5)
                        .queryParam("state", "FUTURE")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(19)))
                .andExpect(jsonPath("$.[0].start", is(start2.toString())))
                .andExpect(jsonPath("$.[0].end", is(end2.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(1)))
                .andExpect(jsonPath("$.[0].item.id", is(6)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void findAllByOwnerAndStateWaiting() throws Exception {
        // given
        LocalDateTime start2 = LocalDateTime.of(2024, 4, 10, 20, 1, 20);
        LocalDateTime end2 = LocalDateTime.of(2024, 4, 10, 21, 1, 20);
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 5)
                        .queryParam("state", "WAITING")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(19)))
                .andExpect(jsonPath("$.[0].start", is(start2.toString())))
                .andExpect(jsonPath("$.[0].end", is(end2.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(1)))
                .andExpect(jsonPath("$.[0].item.id", is(6)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void updateBookingApproved() throws Exception {
        // given
        LocalDateTime start2 = LocalDateTime.of(2025, 3, 10, 20, 1, 20);
        LocalDateTime end2 = LocalDateTime.of(2025, 3, 10, 21, 1, 20);
        // then
        mockMvc.perform(patch("/bookings/14")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("approved", "true"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(14)))
                .andExpect(jsonPath("$.start", is(start2.toString())))
                .andExpect(jsonPath("$.end", is(end2.toString())))
                .andExpect(jsonPath("$.booker.id", is(2)))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void updateBookingUserNotFound() throws Exception {
        // then
        mockMvc.perform(patch("/bookings/10")
                        .header("X-Sharer-User-Id", 99)
                        .queryParam("approved", "true"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateBookingOwnerNotFound() throws Exception {
        // then
        mockMvc.perform(patch("/bookings/12")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("approved", "true"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateBookingRejected() throws Exception {
        // given
        LocalDateTime start2 = LocalDateTime.of(2026, 3, 10, 20, 1, 20);
        LocalDateTime end2 = LocalDateTime.of(2026, 3, 10, 21, 1, 20);
        // then
        mockMvc.perform(patch("/bookings/15")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("approved", "false"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(15)))
                .andExpect(jsonPath("$.start", is(start2.toString())))
                .andExpect(jsonPath("$.end", is(end2.toString())))
                .andExpect(jsonPath("$.booker.id", is(1)))
                .andExpect(jsonPath("$.item.id", is(4)))
                .andExpect(jsonPath("$.status", is(BookingStatus.REJECTED.toString())));
    }
}
package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private final LocalDateTime start = LocalDateTime.now().plusSeconds(60).withNano(0);
    private final LocalDateTime end = LocalDateTime.now().plusHours(1).withNano(0);
    private final BookingDtoInput bookingDtoInput = new BookingDtoInput(1, start, end);
    private final UserDto userDto = new UserDto(2, "user", "user@mail.ru");
    private final ItemDto itemDto = new ItemDto(1, "Отвертка", "Обычная отвертка", true, null, null, null);
    private final BookingDto bookingDto = new BookingDto(1, start, end, userDto, itemDto, BookingStatus.WAITING);


    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("Create booking test")
    void createBookingTest() throws Exception {
        // when
        when(bookingService.create(anyInt(), any()))
                .thenReturn(bookingDto);
        // then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
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
    @DisplayName("Get booking test")
    void getBookingTest() throws Exception {
        // when
        when(bookingService.findById(1, 1))
                .thenReturn(bookingDto);
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

    @Test
    @DisplayName("Wrong get booking test")
    void wrongGetBookingTest() throws Exception {
        // when
        when(bookingService.findById(anyInt(), anyInt()))
                .thenThrow(EntityNotFoundException.class);
        // then
        mockMvc.perform(get("/bookings/2")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Find all by booker booking test")
    void findAllByBookerTest() throws Exception {
        // when
        when(bookingService.findAllByBooker(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        // then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].start", is(start.toString())))
                .andExpect(jsonPath("$.[0].end", is(end.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(2)))
                .andExpect(jsonPath("$.[0].item.id", is(1)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    @DisplayName("Find all by owner booking test")
    void findAllByOwnerTest() throws Exception {
        // when
        when(bookingService.findAllByOwner(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        // then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].start", is(start.toString())))
                .andExpect(jsonPath("$.[0].end", is(end.toString())))
                .andExpect(jsonPath("$.[0].booker.id", is(2)))
                .andExpect(jsonPath("$.[0].item.id", is(1)))
                .andExpect(jsonPath("$.[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    @DisplayName("update booking test")
    void updateBookingTest() throws Exception {
        // when
        BookingDto bookingDto1 = bookingDto;
        bookingDto1.setStatus(BookingStatus.APPROVED);
        // then
        when(bookingService.update(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingDto1);
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
}
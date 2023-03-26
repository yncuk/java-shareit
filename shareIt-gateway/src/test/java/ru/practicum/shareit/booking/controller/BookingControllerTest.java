package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ShareItGateway.class})
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private static final LocalDateTime start = LocalDateTime.now().plusSeconds(60).withNano(0);
    private static final LocalDateTime end = LocalDateTime.now().plusHours(1).withNano(0);
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void createBookingBadRequests() throws Exception {
        // when
        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto();
        bookItemRequestDto.setItemId(1);
        bookItemRequestDto.setStart(start);
        bookItemRequestDto.setEnd(end);
        // then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 99)
                        .content(mapper.writeValueAsString(bookItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        // when
        bookItemRequestDto.setItemId(99);
        // then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        // when
        bookItemRequestDto.setItemId(1);
        bookItemRequestDto.setStart(start);
        bookItemRequestDto.setEnd(start);
        // then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        // when
        bookItemRequestDto.setItemId(1);
        bookItemRequestDto.setStart(end);
        bookItemRequestDto.setEnd(start);
        // then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}
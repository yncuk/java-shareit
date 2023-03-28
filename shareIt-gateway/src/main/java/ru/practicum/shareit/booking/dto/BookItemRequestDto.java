package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.valid.StartBeforeEndDateValid;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@StartBeforeEndDateValid
public class BookItemRequestDto {
    long itemId;
    @FutureOrPresent
    LocalDateTime start;
    @Future
    LocalDateTime end;
}
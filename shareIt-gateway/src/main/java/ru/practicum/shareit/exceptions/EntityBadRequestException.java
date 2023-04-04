package ru.practicum.shareit.exceptions;

public class EntityBadRequestException extends RuntimeException {
    public EntityBadRequestException(final String message) {
        super(message);
    }

}

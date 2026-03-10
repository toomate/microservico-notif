package infrastructure.web.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant timestamp,
        String message,
        UUID id
) {
    public static ApiError of(String message) {
        return new ApiError(Instant.now(), message, UUID.randomUUID());
    }

    public static ApiError of(String message, UUID id) {
        return new ApiError(Instant.now(), message, id);
    }
}
package com.lightevents.events;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class EventDtos {
    public record CreateEventRequest(
        @NotBlank String title,
        String description,
        String coverImageUrl,
        String category,
        String city,
        String country,
        String venueName,
        boolean online,
        @NotBlank String organizerName,
        @Email String organizerEmail,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        @Min(1) int capacity,
        String brandColor
    ) {}
    public record CreateTicketRequest(@NotBlank String name, TicketKind kind, @DecimalMin("0.00") BigDecimal price, String currency, @Min(1) int quantity) {}
    public record RegisterAttendeeRequest(@NotBlank String fullName, @Email String email, String phone, String company, String roleTitle, @NotNull Long ticketTypeId) {}
    public record CheckInRequest(@NotBlank String qrCode) {}
}

package com.lightevents.events;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class EventDtos {
    public record CreateEventRequest(@NotBlank String title, String description, String coverImageUrl, String category, String customCategory, String city, String country, String venueName, String roomName, String addressLine, String postalCode, String state, String countryCode, boolean online, String onlineAccessUrl, @NotBlank String organizerName, @Email String organizerEmail, @NotNull LocalDateTime startsAt, @NotNull LocalDateTime endsAt, @Min(1) int capacity, String brandColor, Double latitude, Double longitude, List<String> mediaUrls, String videoUrl, String generatedImagePrompt, List<String> allowedPaymentMethods, LocalDateTime reservationFreeUntil, List<String> publishChannels) {}
    public record CreateTicketRequest(@NotBlank String name, TicketKind kind, @DecimalMin("0.00") BigDecimal price, String currency, @Min(1) int quantity) {}
    public record RegisterAttendeeRequest(@NotBlank String fullName, @Email String email, String phone, String company, String roleTitle, @NotNull Long ticketTypeId) {}
    public record TicketHolder(String fullName, @Email String email, String phone, String whatsapp, String countryOfResidence) {}
    public record ReserveTicketsRequest(@NotBlank String buyerName, @Email String buyerEmail, String buyerPhone, String buyerWhatsapp, boolean companyPurchase, String companyName, String deliveryPreference, @NotNull Long ticketTypeId, @Min(1) int quantity, List<TicketHolder> holders) {}
    public record ConfirmReservationPaymentRequest(@NotBlank String paymentReference) {}
    public record TicketLookupRequest(@Email @NotBlank String email) {}
    public record TicketLookupVerifyRequest(@Email @NotBlank String email, @NotBlank String code) {}
    public record CheckInRequest(@NotBlank String qrCode) {}
}

package com.lightevents.eventops;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class EventOpsDtos {
    private EventOpsDtos() {}
    public record OrganizerApplicationRequest(Long organizerAccountId, @NotBlank String businessName, @Email String contactEmail, String websiteUrl, String description) {}
    public record BoxOfficeLinkRequest(String deviceName, Integer expiresInHours) {}
    public record DoorSaleRequest(String buyerName, @Email String buyerEmail, String buyerPhone, @NotNull Long ticketTypeId, @Min(1) Integer quantity, String paymentMethod) {}
    public record BoxOfficeSaleRequest(@NotNull Long eventId, @NotNull Long ticketTypeId, Long cashierAccountId, String buyerName, @Email String buyerEmail, String buyerPhone, @Min(1) int quantity, BigDecimal unitPrice, String currency, String paymentMethod) {}
    public record SeatMapRequest(@NotNull Long eventId, @NotBlank String name, String venueName, String layoutJson, Boolean active) {}
    public record SeatRequest(@NotNull Long eventId, @NotNull Long seatMapId, String sectionName, String rowLabel, Integer seatNumber, @NotBlank String seatLabel) {}
    public record SeatReservationRequest(Long attendeeId, String reservationReference) {}
    public record PromoAccessCodeRequest(@NotNull Long eventId, @NotBlank String code, String type, BigDecimal discountAmount, Integer discountPercent, Integer maxRedemptions, Boolean active, LocalDateTime startsAt, LocalDateTime endsAt) {}
    public record RedeemCodeRequest(@NotNull Long eventId, @NotBlank String code) {}
    public record WaitlistEntryRequest(@NotNull Long eventId, Long ticketTypeId, String fullName, @Email String email, String phone, @Min(1) Integer quantity) {}
    public record RefundRequestCreate(@NotNull Long reservationId, Long eventId, @Email String buyerEmail, BigDecimal amount, String reason) {}
    public record RefundStatusRequest(@NotBlank String status) {}
    public record TeamMemberRequest(@NotNull Long eventId, Long accountId, @Email String email, String fullName, String role, List<String> permissions, Boolean active) {}
    public record AttendeeQuestionRequest(@NotNull Long eventId, @NotBlank String label, String type, Boolean required, List<String> options, Integer sortOrder) {}
    public record AttendeeAnswerRequest(@NotNull Long eventId, @NotNull Long attendeeId, @NotNull Long questionId, String answerText) {}
    public record MarketingCampaignRequest(@NotNull Long eventId, @NotBlank String name, String channel, BigDecimal budget, LocalDateTime startsAt, LocalDateTime endsAt, String status) {}
    public record TrackingLinkRequest(@NotNull Long eventId, Long campaignId, @NotBlank String slug, String url, String utmSource, String utmMedium, String utmCampaign) {}
    public record DeveloperWebhookRequest(Long eventId, @NotBlank String targetUrl, List<String> events, Boolean active) {}
    public record WebhookTestRequest(@NotBlank String eventType, String payload) {}
    public record SeatMapView(SeatMap seatMap, List<Seat> seats) {}
}

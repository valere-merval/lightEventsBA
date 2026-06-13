package com.lightevents.auth;
import jakarta.validation.constraints.*;

public final class AuthDtos {
    public record RegisterRequest(@NotBlank String fullName, @Email String email, String phone, String whatsappNumber, AccountRole role, PayoutMethod payoutMethod, String payoutCountry, String payoutAccountName, String payoutAccountRef) {}
    public record VerifyRequest(@NotBlank String channel, @NotBlank String destination, @NotBlank String code) {}
    public record LoginRequest(@Email @NotBlank String email) {}
    public record AccountResponse(Long id, String fullName, String email, String phone, AccountRole role, boolean verified, String apiToken, String demoEmailCode, String demoPhoneCode) {}
}

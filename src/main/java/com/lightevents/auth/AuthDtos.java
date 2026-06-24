package com.lightevents.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class AuthDtos {
    public record RegisterRequest(
            @NotBlank String fullName,
            @Email @NotBlank String email,
            String phone,
            String whatsappNumber,
            AccountRole role,
            PayoutMethod payoutMethod,
            String payoutCountry,
            String payoutAccountName,
            String payoutAccountRef
    ) {}

    public record VerifyRequest(@NotBlank String channel, @NotBlank String destination, @NotBlank String code) {}
    public record LoginRequest(@Email @NotBlank String email) {}
    public record LoginVerifyRequest(@Email @NotBlank String email, @NotBlank String code) {}
    public record LoginStartResponse(String email, String message) {}
    public record AccountResponse(Long id, String fullName, String email, String phone, AccountRole role, boolean verified, String apiToken) {}
}

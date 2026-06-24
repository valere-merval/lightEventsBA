package com.lightevents.auth;

import com.lightevents.notifications.NotificationService;
import com.lightevents.shared.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accounts;
    private final Optional<NotificationService> notifications;
    private final SecureRandom random = new SecureRandom();

    public AccountService(AccountRepository accounts, Optional<NotificationService> notifications) {
        this.accounts = accounts;
        this.notifications = notifications;
    }

    @Transactional
    public Account register(AuthDtos.RegisterRequest r) {
        if (blank(r.email())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is required");
        }

        String email = normalizeEmail(r.email());
        Account a = accounts.findByEmailIgnoreCase(email).orElseGet(Account::new);
        a.setFullName(r.fullName());
        a.setEmail(email);
        a.setPhone(blank(r.phone()) ? null : r.phone());
        a.setWhatsappNumber(r.whatsappNumber());
        a.setRole(r.role() == null || r.role() == AccountRole.ADMIN ? AccountRole.PARTICIPANT : r.role());
        a.setPayoutMethod(r.payoutMethod() == null ? PayoutMethod.BANK_TRANSFER : r.payoutMethod());
        a.setPayoutCountry(r.payoutCountry());
        a.setPayoutAccountName(r.payoutAccountName());
        a.setPayoutAccountRef(r.payoutAccountRef());
        a.setEmailVerified(false);
        a.setEmailVerificationCode(code());
        a.setPhoneVerificationCode(code());

        Account saved = accounts.save(a);
        sendEmailCode(saved.getEmail(), saved.getEmailVerificationCode(), "Code de vérification LightEvents", "Votre code de vérification LightEvents est : ");
        return saved;
    }

    @Transactional
    public Account verify(AuthDtos.VerifyRequest r) {
        Account a = r.channel().equalsIgnoreCase("phone") || r.channel().equalsIgnoreCase("whatsapp")
                ? accounts.findAll().stream()
                        .filter(x -> r.destination().equals(x.getPhone()) || r.destination().equals(x.getWhatsappNumber()))
                        .findFirst()
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"))
                : accounts.findByEmailIgnoreCase(normalizeEmail(r.destination()))
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"));

        if (r.channel().equalsIgnoreCase("email")) {
            if (!r.code().equals(a.getEmailVerificationCode())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid email code");
            }
            a.setEmailVerified(true);
        } else {
            if (!r.code().equals(a.getPhoneVerificationCode())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid phone code");
            }
            a.setPhoneVerified(true);
        }
        return accounts.save(a);
    }

    @Transactional
    public AuthDtos.LoginStartResponse startEmailLogin(AuthDtos.LoginRequest r) {
        Account a = accounts.findByEmailIgnoreCase(normalizeEmail(r.email()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"));
        a.setEmailVerificationCode(code());
        Account saved = accounts.save(a);
        sendEmailCode(saved.getEmail(), saved.getEmailVerificationCode(), "Code de connexion LightEvents", "Votre code de connexion LightEvents est : ");
        return new AuthDtos.LoginStartResponse(saved.getEmail(), "Code de connexion envoyé par email");
    }

    @Transactional
    public Account verifyEmailLogin(AuthDtos.LoginVerifyRequest r) {
        Account a = accounts.findByEmailIgnoreCase(normalizeEmail(r.email()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"));
        if (!r.code().equals(a.getEmailVerificationCode())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Code de connexion invalide");
        }
        a.setEmailVerified(true);
        return accounts.save(a);
    }

    public Account requireAdmin(String token) {
        Account a = accountFromToken(token);
        if (a.getRole() != AccountRole.ADMIN) throw new ApiException(HttpStatus.FORBIDDEN, "Admin only");
        return a;
    }

    private Account accountFromToken(String token) {
        if (token == null || token.isBlank()) throw new ApiException(HttpStatus.UNAUTHORIZED, "Login required");
        return accounts.findByApiToken(token.replace("Bearer ", ""))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token"));
    }

    public Account requireVerified(String token) {
        Account a = accountFromToken(token);
        if (!a.isVerified()) throw new ApiException(HttpStatus.FORBIDDEN, "Account must be verified by email, SMS or WhatsApp before publishing");
        return a;
    }

    public Account registerAdmin(AuthDtos.RegisterRequest r) {
        Account a = accounts.findByEmailIgnoreCase(normalizeEmail(r.email())).orElseGet(Account::new);
        a.setFullName(r.fullName());
        a.setEmail(normalizeEmail(r.email()));
        a.setPhone(blank(r.phone()) ? null : r.phone());
        a.setWhatsappNumber(r.whatsappNumber());
        a.setRole(AccountRole.ADMIN);
        a.setEmailVerified(true);
        a.setPhoneVerified(true);
        a.setEmailVerificationCode(code());
        a.setPhoneVerificationCode(code());
        return accounts.save(a);
    }

    public AuthDtos.AccountResponse response(Account a) {
        return new AuthDtos.AccountResponse(a.getId(), a.getFullName(), a.getEmail(), a.getPhone(), a.getRole(), a.isVerified(), a.getApiToken());
    }

    private void sendEmailCode(String email, String code, String subject, String prefix) {
        NotificationService notificationService = notifications.orElseThrow(() -> new ApiException(HttpStatus.BAD_GATEWAY, "Email service is not configured"));
        try {
            notificationService.sendEmail(email, subject, prefix + code + "\n\nSi vous n'êtes pas à l'origine de cette demande, ignorez cet email.");
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Impossible d'envoyer le code par email pour le moment");
        }
    }

    private String code() {
        return String.valueOf(100000 + random.nextInt(900000));
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}

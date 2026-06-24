package com.lightevents.payments;

import com.lightevents.events.EventService;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.math.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final TransactionRepository repo;
    private final EventService events;
    private final RestClient rest = RestClient.create();

    @Value("${stripe.secret-key:}") private String stripeKey;
    @Value("${paypal.client-id:}") private String paypalClientId;
    @Value("${paypal.client-secret:}") private String paypalClientSecret;
    @Value("${paypal.base-url:https://api-m.paypal.com}") private String paypalBaseUrl;
    @Value("${app.frontend-url:http://localhost:5173}") private String frontendUrl;

    @Value("${getmipay.base-url:https://sandbox.getmipay.com/api}") private String getMiPayBaseUrl;
    @Value("${getmipay.public-api-key:}") private String getMiPayPublicKey;
    @Value("${getmipay.private-secret-key:}") private String getMiPayPrivateKey;
    @Value("${getmipay.callback-url:}") private String getMiPayCallbackUrl;
    @Value("${getmipay.services.orange-money:3}") private String orangeMoneyService;
    @Value("${getmipay.services.mtn-money:1}") private String mtnMoneyService;
    @Value("${getmipay.services.wave:}") private String waveService;
    @Value("${getmipay.services.airtel-money:}") private String airtelMoneyService;
    @Value("${getmipay.services.moov-money:}") private String moovMoneyService;

    public PaymentController(TransactionRepository repo, EventService events) {
        this.repo = repo;
        this.events = events;
    }

    public record InitiatePaymentRequest(
            @NotNull Long eventId,
            Long attendeeId,
            String reservationReference,
            @NotNull PaymentProvider provider,
            @NotNull @DecimalMin("0.01") BigDecimal amount,
            String currency,
            String payerPhone,
            String payerName,
            String payerEmail,
            String otp
    ) {}

    public record CheckoutResponse(
            String reference,
            PaymentProvider provider,
            PaymentStatus status,
            BigDecimal gross,
            BigDecimal platformFee,
            BigDecimal organizerNet,
            String checkoutUrl,
            String providerReference,
            String note
    ) {}

    public record ConfirmPaymentRequest(String transactionReference, String reservationReference, String providerReference) {}

    @PostMapping({"/mobile-money/initiate", "/checkout"})
    public CheckoutResponse initiate(@RequestBody InitiatePaymentRequest r) {
        BigDecimal gross = r.amount();
        if (!blank(r.reservationReference())) gross = events.reservationByReference(r.reservationReference()).getGrossAmount();
        BigDecimal fee = gross.multiply(new BigDecimal("0.045")).setScale(2, RoundingMode.HALF_UP);
        Transaction t = new Transaction();
        t.setEventId(r.eventId());
        t.setAttendeeId(r.attendeeId());
        t.setReservationReference(r.reservationReference());
        t.setProvider(r.provider());
        t.setAmount(gross);
        t.setPlatformFee(fee);
        t.setOrganizerNet(gross.subtract(fee));
        t.setCurrency(r.currency() == null ? "XOF" : r.currency());
        t.setPayerPhone(r.payerPhone());
        try {
            var event = events.get(r.eventId());
            t.setOrganizerPayoutMethod(blank(event.getPayoutMethod()) ? "PAYPAL" : event.getPayoutMethod());
            t.setOrganizerPayoutAccountRef(event.getPayoutAccountRef());
            t.setPayoutStatus("PENDING");
        } catch (Exception ignored) {
            t.setPayoutStatus("PENDING");
        }
        t.setStatus(PaymentStatus.PENDING);
        t = repo.save(t);

        String url = checkoutUrl(t, r);
        t.setCheckoutUrl(url);
        t = repo.save(t);
        return new CheckoutResponse(t.getReference(), t.getProvider(), t.getStatus(), t.getAmount(), t.getPlatformFee(), t.getOrganizerNet(), t.getCheckoutUrl(), t.getProviderReference(), paymentNote(t));
    }

    @GetMapping("/{reference}/status")
    public Map<String, Object> status(@PathVariable String reference) {
        Transaction t = repo.findByReference(reference).orElseThrow();
        if (isGetMiPay(t.getProvider()) && !blank(t.getProviderReference()) && hasGetMiPayCredentials()) {
            Map<String, Object> providerStatus = getMiPayStatus(t.getProviderReference());
            String status = providerDataString(providerStatus, "status");
            if ("success".equalsIgnoreCase(status)) {
                markSucceeded(t);
            } else if ("failed".equalsIgnoreCase(status) || "timeout".equalsIgnoreCase(status)) {
                markFailed(t);
            }
            return Map.of("transaction", t, "provider", providerStatus);
        }
        return Map.of("transaction", t);
    }

    @PostMapping("/confirm")
    public Map<String, Object> confirm(@RequestBody ConfirmPaymentRequest r) {
        Transaction t = repo.findByReference(r.transactionReference()).orElseThrow();
        t.setProviderReference(r.providerReference());
        if (blank(t.getReservationReference()) && !blank(r.reservationReference())) t.setReservationReference(r.reservationReference());
        markSucceeded(t);
        return Map.of("paid", true, "transaction", t);
    }

    @PostMapping("/getmipay/webhook")
    public Map<String, Object> getMiPayWebhook(@RequestBody Map<String, Object> payload) {
        String reference = firstString(payload, "transaction_reference", "reference", "order_id");
        String status = firstString(payload, "status");
        repo.findAll().stream()
                .filter(t -> Objects.equals(t.getProviderReference(), reference) || Objects.equals(t.getReference(), reference))
                .findFirst()
                .ifPresent(t -> {
                    if ("success".equalsIgnoreCase(status)) {
                        markSucceeded(t);
                    } else if ("failed".equalsIgnoreCase(status) || "timeout".equalsIgnoreCase(status)) {
                        markFailed(t);
                    }
                });
        return Map.of("received", true);
    }

    @GetMapping("/mobile-money/services")
    public Map<String, Object> getMiPayServices() {
        if (!hasGetMiPayCredentials()) return Map.of("configured", false, "message", "GetMiPay credentials are not configured");
        return rest.get().uri(getMiPayBaseUrl + "/services")
                .headers(h -> h.setBearerAuth(getMiPayToken()))
                .retrieve().body(Map.class);
    }

    private String checkoutUrl(Transaction t, InitiatePaymentRequest r) {
        if (isGetMiPay(t.getProvider())) return createGetMiPayPayIn(t, r);
        if (t.getProvider() == PaymentProvider.STRIPE && !blank(stripeKey)) return createStripeCheckout(t);
        if (t.getProvider() == PaymentProvider.PAYPAL && !blank(paypalClientId) && !blank(paypalClientSecret)) return createPaypalOrder(t);
        return frontendUrl + "/tickets?paymentPreview=" + t.getProvider() + "&amount=" + t.getAmount();
    }

    private String createGetMiPayPayIn(Transaction t, InitiatePaymentRequest r) {
        if (!hasGetMiPayCredentials()) return frontendUrl + "/tickets?getmipayPreview=true&provider=" + t.getProvider() + "&amount=" + t.getAmount();
        String service = serviceFor(t.getProvider());
        if (blank(service)) return frontendUrl + "/tickets?getmipayMissingService=" + t.getProvider();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("amount", t.getAmount().setScale(0, RoundingMode.HALF_UP));
        body.put("currency", normalizeMobileCurrency(t.getCurrency()));
        body.put("wallet", r.payerPhone());
        body.put("description", "Ticket LightEvents " + safe(t.getReservationReference(), t.getReference()));
        body.put("customer_name", blank(r.payerName()) ? "Client LightEvents" : r.payerName());
        if (!blank(r.payerEmail())) body.put("customer_email", r.payerEmail());
        if (!blank(getMiPayCallbackUrl)) body.put("callback_url", getMiPayCallbackUrl);

        try {
            Map res = rest.post().uri(getMiPayBaseUrl + "/payments/payin")
                    .headers(h -> {
                        h.setBearerAuth(getMiPayToken());
                        h.set("operation", "2");
                        h.set("service", service);
                        if (!blank(r.otp())) h.set("otp", r.otp());
                    })
                    .body(body)
                    .retrieve().body(Map.class);
            String reference = providerDataString(res, "transaction_reference");
            if (blank(reference)) reference = providerDataString(res, "soleaspay_reference");
            if (!blank(reference)) t.setProviderReference(reference);
            String paymentUrl = providerDataString(res, "payment_url");
            return blank(paymentUrl) ? frontendUrl + "/tickets?getmipay=" + enc(safe(reference, t.getReference())) : paymentUrl;
        } catch (Exception e) {
            markFailed(t);
            return frontendUrl + "/tickets?getmipayError=" + enc(t.getProvider().name());
        }
    }

    private String getMiPayToken() {
        Map<String, Object> body = Map.of("public_apikey", getMiPayPublicKey, "private_secretkey", getMiPayPrivateKey);
        Map res = rest.post().uri(getMiPayBaseUrl + "/action/auth").body(body).retrieve().body(Map.class);
        String token = providerDataString(res, "token");
        if (blank(token)) throw new IllegalStateException("GetMiPay authentication failed");
        return token;
    }

    private Map<String, Object> getMiPayStatus(String reference) {
        return rest.get().uri(getMiPayBaseUrl + "/payments/" + enc(reference))
                .headers(h -> h.setBearerAuth(getMiPayToken()))
                .retrieve().body(Map.class);
    }

    private String createStripeCheckout(Transaction t) {
        try {
            String body = "mode=payment&success_url=" + enc(frontendUrl + "/tickets?paid=true") + "&cancel_url=" + enc(frontendUrl + "/events/" + t.getEventId()) + "&line_items[0][quantity]=1&line_items[0][price_data][currency]=" + enc(t.getCurrency().toLowerCase()) + "&line_items[0][price_data][unit_amount]=" + t.getAmount().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP) + "&line_items[0][price_data][product_data][name]=" + enc("Ticket LightEvents");
            Map res = rest.post().uri("https://api.stripe.com/v1/checkout/sessions").headers(h -> {
                h.setBearerAuth(stripeKey);
                h.set("Content-Type", "application/x-www-form-urlencoded");
            }).body(body).retrieve().body(Map.class);
            return String.valueOf(res.get("url"));
        } catch (Exception e) {
            return frontendUrl + "/tickets?stripePreview=true";
        }
    }

    private String createPaypalOrder(Transaction t) {
        try {
            Map token = rest.post().uri(paypalBaseUrl + "/v1/oauth2/token").headers(h -> {
                h.setBasicAuth(paypalClientId, paypalClientSecret);
                h.set("Content-Type", "application/x-www-form-urlencoded");
            }).body("grant_type=client_credentials").retrieve().body(Map.class);
            String access = String.valueOf(token.get("access_token"));
            Map<String, Object> amount = Map.of("currency_code", t.getCurrency(), "value", t.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString());
            Map<String, Object> body = Map.of("intent", "CAPTURE", "purchase_units", List.of(Map.of("reference_id", t.getReference(), "description", "Ticket LightEvents", "custom_id", t.getReference(), "amount", amount)), "application_context", Map.of("return_url", frontendUrl + "/tickets?paypalPaid=true", "cancel_url", frontendUrl + "/events/" + t.getEventId()));
            Map res = rest.post().uri(paypalBaseUrl + "/v2/checkout/orders").headers(h -> {
                h.setBearerAuth(access);
                h.set("Content-Type", "application/json");
                h.set("Prefer", "return=representation");
            }).body(body).retrieve().body(Map.class);
            t.setProviderReference(String.valueOf(res.get("id")));
            for (Object link : (List) res.getOrDefault("links", List.of())) {
                Map l = (Map) link;
                if ("approve".equals(l.get("rel"))) return String.valueOf(l.get("href"));
            }
            return frontendUrl + "/tickets?paypalOrder=" + res.get("id");
        } catch (Exception e) {
            return frontendUrl + "/tickets?paypalPreview=true";
        }
    }

    private void markSucceeded(Transaction t) { t.setStatus(PaymentStatus.SUCCEEDED); t.setPayoutStatus("READY_FOR_AUTOMATIC_PAYOUT"); repo.save(t); if (!blank(t.getReservationReference())) events.confirmPayment(t.getReservationReference(), t.getReference()); }
    private void markFailed(Transaction t) { t.setStatus(PaymentStatus.FAILED); t.setPayoutStatus("CANCELLED"); repo.save(t); if (!blank(t.getReservationReference())) events.failPayment(t.getReservationReference(), t.getReference()); }

    private boolean hasGetMiPayCredentials() { return !blank(getMiPayPublicKey) && !blank(getMiPayPrivateKey); }
    private static boolean isGetMiPay(PaymentProvider provider) { return provider == PaymentProvider.ORANGE_MONEY || provider == PaymentProvider.MTN_MONEY || provider == PaymentProvider.WAVE || provider == PaymentProvider.AIRTEL_MONEY || provider == PaymentProvider.MOOV_MONEY; }
    private String serviceFor(PaymentProvider provider) { return switch (provider) { case ORANGE_MONEY -> orangeMoneyService; case MTN_MONEY -> mtnMoneyService; case WAVE -> waveService; case AIRTEL_MONEY -> airtelMoneyService; case MOOV_MONEY -> moovMoneyService; default -> ""; }; }
    private static String normalizeMobileCurrency(String currency) { return "XAF".equalsIgnoreCase(currency) ? "XAF" : "XOF"; }
    private static String paymentNote(Transaction t) { return isGetMiPay(t.getProvider()) ? "Paiement Mobile Money initialisé via GetMiPay. Confirmez le paiement sur le téléphone du client, puis vérifiez le statut." : "LightEvents encaisse, garde 4.5%, puis prépare le reversement automatique vers le moyen choisi par l'organisateur."; }
    private static String providerDataString(Map map, String key) { if (map == null) return null; Object data = map.get("data"); if (data instanceof Map<?, ?> d && d.get(key) != null) return String.valueOf(d.get(key)); Object value = map.get(key); return value == null ? null : String.valueOf(value); }
    private static String firstString(Map<String, Object> map, String... keys) { for (String key : keys) { Object value = map.get(key); if (value != null) return String.valueOf(value); } return null; }
    private static boolean blank(String v) { return v == null || v.isBlank(); }
    private static String safe(String value, String fallback) { return blank(value) ? fallback : value; }
    private static String enc(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
}

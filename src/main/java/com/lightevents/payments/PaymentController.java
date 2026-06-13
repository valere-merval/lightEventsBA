package com.lightevents.payments;
import jakarta.validation.constraints.*; import org.springframework.web.bind.annotation.*; import java.math.BigDecimal;
@RestController @RequestMapping("/api/payments")
public class PaymentController { private final TransactionRepository repo; public PaymentController(TransactionRepository repo){this.repo=repo;} public record InitiatePaymentRequest(@NotNull Long eventId, Long attendeeId, @NotNull PaymentProvider provider, @NotNull @DecimalMin("0.01") BigDecimal amount, String currency, @NotBlank String payerPhone) {}
 @PostMapping("/mobile-money/initiate") public Transaction initiate(@RequestBody InitiatePaymentRequest r){ Transaction t=new Transaction(); t.setEventId(r.eventId()); t.setAttendeeId(r.attendeeId()); t.setProvider(r.provider()); t.setAmount(r.amount()); t.setCurrency(r.currency()==null?"XOF":r.currency()); t.setPayerPhone(r.payerPhone()); t.setStatus(PaymentStatus.PENDING); return repo.save(t);} }

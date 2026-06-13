package com.lightevents.events;

import com.lightevents.auth.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/events")
public class EventController {
    private final EventService service; private final AccountService accounts;
    public EventController(EventService service, AccountService accounts) { this.service = service; this.accounts = accounts; }
    @GetMapping public List<Event> list(@RequestParam(defaultValue="true") boolean publishedOnly, @RequestParam(required=false) String country, @RequestParam(required=false) String city, @RequestParam(required=false) String category) { return publishedOnly ? service.published(country,city,category) : service.all(); }
    @GetMapping("/categories") public List<String> categories(){ return List.of("music","business","conference","dating","humour & comedy","cinema","webinar","sport","social","tourism","formation","religion","tech","food","art"); }
    @GetMapping("/destinations") public List<Map<String,Object>> destinations(){ return List.of(Map.of("name","Santorini Sunset Experience","country","Greece","category","tourism","priceFrom",89,"image","https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?auto=format&fit=crop&w=1200&q=80"), Map.of("name","Safari premium Serengeti","country","Tanzania","category","tourism","priceFrom",260,"image","https://images.unsplash.com/photo-1516426122078-c23e76319801?auto=format&fit=crop&w=1200&q=80"), Map.of("name","Dubai Marina Yacht Night","country","UAE","category","tourism","priceFrom",140,"image","https://images.unsplash.com/photo-1512453979798-5ea266f8880c?auto=format&fit=crop&w=1200&q=80"), Map.of("name","Bali Creator Retreat","country","Indonesia","category","tourism","priceFrom",120,"image","https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=1200&q=80")); }
    @GetMapping("/{id}") public Event get(@PathVariable Long id) { return service.get(id); }
    @PostMapping public Event create(@RequestHeader(value="X-LightEvents-Token", required=false) String token, @Valid @RequestBody EventDtos.CreateEventRequest request) { return service.create(request, accounts.requireVerified(token)); }
    @PostMapping("/{id}/tickets") public TicketType addTicket(@PathVariable Long id, @Valid @RequestBody EventDtos.CreateTicketRequest request) { return service.addTicket(id, request); }
    @PostMapping("/{id}/attendees") public Attendee register(@PathVariable Long id, @Valid @RequestBody EventDtos.RegisterAttendeeRequest request) { return service.register(id, request); }
    @PostMapping("/{id}/reservations") public EventService.ReservationResult reserve(@PathVariable Long id, @Valid @RequestBody EventDtos.ReserveTicketsRequest request){ return service.reserve(id, request); }
    @PostMapping("/reservations/{reference}/confirm-payment") public Reservation confirmPayment(@PathVariable String reference, @RequestBody EventDtos.ConfirmReservationPaymentRequest request){ return service.confirmPayment(reference, request.paymentReference()); }
    @GetMapping("/{id}/attendees") public List<Attendee> attendees(@PathVariable Long id) { return service.attendees(id); }
    @PostMapping("/check-in") public Attendee checkIn(@Valid @RequestBody EventDtos.CheckInRequest request) { return service.checkIn(request.qrCode()); }
    @PostMapping("/tickets/lookup/request-code") public TicketLookupCode requestTickets(@Valid @RequestBody EventDtos.TicketLookupRequest r){ return service.requestLookupCode(r.email()); }
    @PostMapping("/tickets/lookup/verify") public List<Attendee> verifyTickets(@Valid @RequestBody EventDtos.TicketLookupVerifyRequest r){ return service.verifyLookup(r.email(), r.code()); }
}

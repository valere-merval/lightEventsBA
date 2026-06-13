package com.lightevents.events;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService service;
    public EventController(EventService service) { this.service = service; }

    @GetMapping public List<Event> list(@RequestParam(defaultValue = "true") boolean publishedOnly) { return publishedOnly ? service.published() : service.all(); }
    @GetMapping("/{id}") public Event get(@PathVariable Long id) { return service.get(id); }
    @PostMapping public Event create(@Valid @RequestBody EventDtos.CreateEventRequest request) { return service.create(request); }
    @PostMapping("/{id}/tickets") public TicketType addTicket(@PathVariable Long id, @Valid @RequestBody EventDtos.CreateTicketRequest request) { return service.addTicket(id, request); }
    @PostMapping("/{id}/attendees") public Attendee register(@PathVariable Long id, @Valid @RequestBody EventDtos.RegisterAttendeeRequest request) { return service.register(id, request); }
    @GetMapping("/{id}/attendees") public List<Attendee> attendees(@PathVariable Long id) { return service.attendees(id); }
    @PostMapping("/check-in") public Attendee checkIn(@Valid @RequestBody EventDtos.CheckInRequest request) { return service.checkIn(request.qrCode()); }
}

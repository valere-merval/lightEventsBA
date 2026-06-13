package com.lightevents.events;

import com.lightevents.shared.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {
    private final EventRepository events;
    private final TicketTypeRepository tickets;
    private final AttendeeRepository attendees;

    public EventService(EventRepository events, TicketTypeRepository tickets, AttendeeRepository attendees) {
        this.events = events; this.tickets = tickets; this.attendees = attendees;
    }

    public List<Event> published() { return events.findByStatusOrderByStartsAtAsc(EventStatus.PUBLISHED); }
    public List<Event> all() { return events.findAll(); }
    public Event get(Long id) { return events.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found")); }

    @Transactional
    public Event create(EventDtos.CreateEventRequest req) {
        Event e = new Event();
        e.setTitle(req.title()); e.setDescription(req.description()); e.setCoverImageUrl(req.coverImageUrl()); e.setCategory(req.category());
        e.setCity(req.city()); e.setCountry(req.country()); e.setVenueName(req.venueName()); e.setOnline(req.online());
        e.setOrganizerName(req.organizerName()); e.setOrganizerEmail(req.organizerEmail()); e.setStartsAt(req.startsAt()); e.setEndsAt(req.endsAt());
        e.setCapacity(req.capacity()); e.setStatus(EventStatus.PUBLISHED);
        if (req.brandColor() != null && !req.brandColor().isBlank()) e.setBrandColor(req.brandColor());
        return events.save(e);
    }

    @Transactional
    public TicketType addTicket(Long eventId, EventDtos.CreateTicketRequest req) {
        Event event = get(eventId);
        TicketType t = new TicketType();
        t.setEvent(event); t.setName(req.name()); t.setKind(req.kind() == null ? TicketKind.FREE : req.kind());
        t.setPrice(req.price() == null ? java.math.BigDecimal.ZERO : req.price()); t.setCurrency(req.currency() == null ? "XOF" : req.currency()); t.setQuantity(req.quantity());
        return tickets.save(t);
    }

    @Transactional
    public Attendee register(Long eventId, EventDtos.RegisterAttendeeRequest req) {
        Event event = get(eventId);
        TicketType ticket = tickets.findById(req.ticketTypeId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ticket type not found"));
        if (!ticket.getEvent().getId().equals(event.getId())) throw new ApiException(HttpStatus.BAD_REQUEST, "Ticket does not belong to this event");
        if (ticket.getSold() >= ticket.getQuantity()) throw new ApiException(HttpStatus.CONFLICT, "Ticket type sold out");
        Attendee a = new Attendee();
        a.setEvent(event); a.setTicketType(ticket); a.setFullName(req.fullName()); a.setEmail(req.email()); a.setPhone(req.phone()); a.setCompany(req.company()); a.setRoleTitle(req.roleTitle());
        a.setStatus(ticket.getPrice().signum() > 0 ? CheckInStatus.RESERVED : CheckInStatus.PAID);
        ticket.setSold(ticket.getSold() + 1);
        tickets.save(ticket);
        return attendees.save(a);
    }

    @Transactional
    public Attendee checkIn(String qrCode) {
        Attendee a = attendees.findByQrCode(qrCode).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "QR code invalid"));
        if (a.getStatus() == CheckInStatus.CHECKED_IN) throw new ApiException(HttpStatus.CONFLICT, "Ticket already checked in");
        a.checkIn();
        return attendees.save(a);
    }

    public List<Attendee> attendees(Long eventId) { return attendees.findByEventId(eventId); }
}

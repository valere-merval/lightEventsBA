package com.lightevents.dashboard;

import com.lightevents.auth.Account;
import com.lightevents.auth.AccountService;
import com.lightevents.events.*;
import com.lightevents.payments.TransactionRepository;
import com.lightevents.profiles.UserProfileRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final EventRepository events;
    private final AttendeeRepository attendees;
    private final ReservationRepository reservations;
    private final UserProfileRepository profiles;
    private final TransactionRepository tx;
    private final AccountService accounts;

    public DashboardController(EventRepository events, AttendeeRepository attendees, ReservationRepository reservations, UserProfileRepository profiles, TransactionRepository tx, AccountService accounts) {
        this.events = events;
        this.attendees = attendees;
        this.reservations = reservations;
        this.profiles = profiles;
        this.tx = tx;
        this.accounts = accounts;
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        return Map.of(
                "events", events.count(),
                "attendees", attendees.count(),
                "checkedIn", attendees.countByStatus(CheckInStatus.CHECKED_IN),
                "profiles", profiles.count(),
                "transactions", tx.count(),
                "markets", List.of("Côte d’Ivoire", "Sénégal", "Cameroun", "RDC", "Bénin", "Togo")
        );
    }

    @GetMapping("/organizer")
    @Transactional(readOnly = true)
    public Map<String, Object> organizer(
            @RequestHeader(value = "X-LightEvents-Token", required = false) String token,
            @RequestParam(required = false) String email
    ) {
        Account account = null;
        if (!blank(token)) {
            account = accounts.requireAccount(token);
        }
        Account connected = account;
        List<Event> own = events.findAll().stream()
                .filter(e -> belongsToOrganizer(e, connected, email))
                .toList();
        List<Map<String, Object>> list = own.stream().map(this::eventRow).toList();
        return Map.of("events", list, "platformFeePercent", 4.5);
    }

    private boolean belongsToOrganizer(Event event, Account account, String email) {
        if (account != null) {
            return (event.getOrganizerAccount() != null && Objects.equals(event.getOrganizerAccount().getId(), account.getId()))
                    || same(account.getEmail(), event.getOrganizerEmail());
        }
        return blank(email) || same(email, event.getOrganizerEmail());
    }

    private Map<String, Object> eventRow(Event e) {
        List<Attendee> people = attendees.findByEventId(e.getId());
        Map<String, Long> byCountry = people.stream().collect(Collectors.groupingBy(
                a -> blank(a.getCountryOfResidence()) ? "Non précisé" : a.getCountryOfResidence(),
                Collectors.counting()
        ));
        long seconds = Math.max(0, Duration.between(LocalDateTime.now(), e.getStartsAt()).toSeconds());
        return Map.of(
                "event", e,
                "participants", people,
                "reservations", reservations.findAll().stream().filter(r -> Objects.equals(r.getEventId(), e.getId())).toList(),
                "participantCountries", byCountry,
                "countdownSeconds", seconds
        );
    }

    private static boolean same(String a, String b) { return a != null && b != null && a.equalsIgnoreCase(b); }
    private static boolean blank(String v) { return v == null || v.isBlank(); }
}

package com.lightevents.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "attendees")
public class Attendee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "event_id") @JsonIgnore private Event event;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "ticket_type_id") @JsonIgnore private TicketType ticketType;
    @NotBlank private String fullName;
    @Email private String email;
    private String phone;
    private String company;
    private String roleTitle;
    @Enumerated(EnumType.STRING) private CheckInStatus status = CheckInStatus.RESERVED;
    @Column(unique = true, nullable = false) private String qrCode = UUID.randomUUID().toString();
    private Instant registeredAt = Instant.now();
    private Instant checkedInAt;

    public Long getId() { return id; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public TicketType getTicketType() { return ticketType; }
    public void setTicketType(TicketType ticketType) { this.ticketType = ticketType; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getRoleTitle() { return roleTitle; }
    public void setRoleTitle(String roleTitle) { this.roleTitle = roleTitle; }
    public CheckInStatus getStatus() { return status; }
    public void setStatus(CheckInStatus status) { this.status = status; }
    public String getQrCode() { return qrCode; }
    public Instant getRegisteredAt() { return registeredAt; }
    public Instant getCheckedInAt() { return checkedInAt; }
    public void checkIn() { this.status = CheckInStatus.CHECKED_IN; this.checkedInAt = Instant.now(); }
}

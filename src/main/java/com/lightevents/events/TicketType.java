package com.lightevents.events;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ticket_types")
public class TicketType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "event_id")
    private Event event;
    @NotBlank private String name;
    @Enumerated(EnumType.STRING) private TicketKind kind = TicketKind.FREE;
    private BigDecimal price = BigDecimal.ZERO;
    private String currency = "XOF";
    private int quantity;
    private int sold;
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public TicketKind getKind() { return kind; }
    public void setKind(TicketKind kind) { this.kind = kind; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getSold() { return sold; }
    public void setSold(int sold) { this.sold = sold; }
    public Instant getCreatedAt() { return createdAt; }
}

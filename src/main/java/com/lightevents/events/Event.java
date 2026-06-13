package com.lightevents.events;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank private String title;
    @Column(length = 4000) private String description;
    private String coverImageUrl;
    private String category;
    private String city;
    private String country;
    private String venueName;
    private boolean online;
    private String organizerName;
    private String organizerEmail;
    @NotNull private LocalDateTime startsAt;
    @NotNull private LocalDateTime endsAt;
    @Enumerated(EnumType.STRING) private EventStatus status = EventStatus.DRAFT;
    private int capacity;
    private String brandColor = "#7c3aed";
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketType> tickets = new ArrayList<>();

    @PreUpdate void touch() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }
    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }
    public String getOrganizerEmail() { return organizerEmail; }
    public void setOrganizerEmail(String organizerEmail) { this.organizerEmail = organizerEmail; }
    public LocalDateTime getStartsAt() { return startsAt; }
    public void setStartsAt(LocalDateTime startsAt) { this.startsAt = startsAt; }
    public LocalDateTime getEndsAt() { return endsAt; }
    public void setEndsAt(LocalDateTime endsAt) { this.endsAt = endsAt; }
    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getBrandColor() { return brandColor; }
    public void setBrandColor(String brandColor) { this.brandColor = brandColor; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<TicketType> getTickets() { return tickets; }
}

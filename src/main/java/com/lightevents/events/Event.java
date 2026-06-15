package com.lightevents.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lightevents.auth.Account;
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
    private String roomName;
    @Column(length = 1200) private String addressLine;
    private String postalCode;
    private String state;
    private String countryCode;
    private boolean online;
    @Column(length = 1200) private String onlineAccessUrl;
    private String organizerName;
    private String organizerEmail;
    @NotNull private LocalDateTime startsAt;
    @NotNull private LocalDateTime endsAt;
    @Enumerated(EnumType.STRING) private EventStatus status = EventStatus.DRAFT;
    private int capacity;
    private String brandColor = "#7c3aed";
    private String customCategory;
    private Double latitude;
    private Double longitude;
    @Column(length = 3000) private String mediaUrls;
    private String videoUrl;
    private String generatedImageUrl;
    @Column(length = 1000) private String allowedPaymentMethods;
    private java.time.LocalDateTime reservationFreeUntil;
    private Integer reservationHoldDays = 2;
    @Column(length = 1000) private String publishChannels;
    @ManyToOne(fetch = FetchType.LAZY) @JsonIgnore private Account organizerAccount;
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
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }
    public String getOnlineAccessUrl() { return onlineAccessUrl; }
    public void setOnlineAccessUrl(String onlineAccessUrl) { this.onlineAccessUrl = onlineAccessUrl; }
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
    public String getCustomCategory(){return customCategory;} public void setCustomCategory(String v){customCategory=v;}
    public Double getLatitude(){return latitude;} public void setLatitude(Double v){latitude=v;}
    public Double getLongitude(){return longitude;} public void setLongitude(Double v){longitude=v;}
    public String getMediaUrls(){return mediaUrls;} public void setMediaUrls(String v){mediaUrls=v;}
    public String getVideoUrl(){return videoUrl;} public void setVideoUrl(String v){videoUrl=v;}
    public String getGeneratedImageUrl(){return generatedImageUrl;} public void setGeneratedImageUrl(String v){generatedImageUrl=v;}
    public String getAllowedPaymentMethods(){return allowedPaymentMethods;} public void setAllowedPaymentMethods(String v){allowedPaymentMethods=v;}
    public java.time.LocalDateTime getReservationFreeUntil(){return reservationFreeUntil;} public void setReservationFreeUntil(java.time.LocalDateTime v){reservationFreeUntil=v;}
    public Integer getReservationHoldDays(){return reservationHoldDays;} public void setReservationHoldDays(Integer v){reservationHoldDays=v;}
    public String getPublishChannels(){return publishChannels;} public void setPublishChannels(String v){publishChannels=v;}
    public Account getOrganizerAccount(){return organizerAccount;} public void setOrganizerAccount(Account v){organizerAccount=v;}
}

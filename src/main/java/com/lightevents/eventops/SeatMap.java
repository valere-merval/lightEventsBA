package com.lightevents.eventops;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

@Entity @Table(name="seat_maps")
public class SeatMap {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long eventId; @NotBlank private String name; private String venueName;
    @Column(length=8000) private String layoutJson;
    private boolean active=true; private Instant createdAt=Instant.now();
    public Long getId(){return id;} public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getVenueName(){return venueName;} public void setVenueName(String v){venueName=v;} public String getLayoutJson(){return layoutJson;} public void setLayoutJson(String v){layoutJson=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public Instant getCreatedAt(){return createdAt;}
}

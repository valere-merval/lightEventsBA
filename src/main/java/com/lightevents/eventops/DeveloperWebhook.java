package com.lightevents.eventops;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="developer_webhooks")
public class DeveloperWebhook {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long eventId; @Column(length=1200) private String targetUrl; @Column(length=1000) private String eventsCsv; private String secret=UUID.randomUUID().toString(); private boolean active=true; private Instant createdAt=Instant.now();
    public Long getId(){return id;} public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public String getTargetUrl(){return targetUrl;} public void setTargetUrl(String v){targetUrl=v;} public String getEventsCsv(){return eventsCsv;} public void setEventsCsv(String v){eventsCsv=v;} public String getSecret(){return secret;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public Instant getCreatedAt(){return createdAt;}
}

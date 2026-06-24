package com.lightevents.eventops;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="marketing_campaigns")
public class MarketingCampaign {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long eventId; private String name; private String channel="EMAIL"; private BigDecimal budget=BigDecimal.ZERO; private LocalDateTime startsAt; private LocalDateTime endsAt; private String status="DRAFT";
    public Long getId(){return id;} public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getChannel(){return channel;} public void setChannel(String v){channel=v;} public BigDecimal getBudget(){return budget;} public void setBudget(BigDecimal v){budget=v;} public LocalDateTime getStartsAt(){return startsAt;} public void setStartsAt(LocalDateTime v){startsAt=v;} public LocalDateTime getEndsAt(){return endsAt;} public void setEndsAt(LocalDateTime v){endsAt=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
}

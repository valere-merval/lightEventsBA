package com.lightevents.eventops;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="webhook_deliveries")
public class WebhookDelivery {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long webhookId; private String eventType; @Column(length=8000) private String payload; private Integer statusCode; private boolean success; private Instant deliveredAt=Instant.now();
    public Long getId(){return id;} public Long getWebhookId(){return webhookId;} public void setWebhookId(Long v){webhookId=v;} public String getEventType(){return eventType;} public void setEventType(String v){eventType=v;} public String getPayload(){return payload;} public void setPayload(String v){payload=v;} public Integer getStatusCode(){return statusCode;} public void setStatusCode(Integer v){statusCode=v;} public boolean isSuccess(){return success;} public void setSuccess(boolean v){success=v;} public Instant getDeliveredAt(){return deliveredAt;}
}

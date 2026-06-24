package com.lightevents.eventops;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="refund_requests")
public class RefundRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long reservationId; private Long eventId; @Email private String buyerEmail; private BigDecimal amount=BigDecimal.ZERO; @Column(length=2000) private String reason; private String status="REQUESTED"; private Instant createdAt=Instant.now(); private Instant resolvedAt;
    public Long getId(){return id;} public Long getReservationId(){return reservationId;} public void setReservationId(Long v){reservationId=v;} public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public String getBuyerEmail(){return buyerEmail;} public void setBuyerEmail(String v){buyerEmail=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;} public String getReason(){return reason;} public void setReason(String v){reason=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public Instant getCreatedAt(){return createdAt;} public Instant getResolvedAt(){return resolvedAt;} public void setResolvedAt(Instant v){resolvedAt=v;}
}

package com.lightevents.eventops;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.time.Instant;

@Entity @Table(name="waitlist_entries")
public class WaitlistEntry {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long eventId; private Long ticketTypeId; private String fullName; @Email private String email; private String phone; private int quantity=1; private String status="WAITING"; private Instant createdAt=Instant.now(); private Instant notifiedAt;
    public Long getId(){return id;} public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public Long getTicketTypeId(){return ticketTypeId;} public void setTicketTypeId(Long v){ticketTypeId=v;} public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public int getQuantity(){return quantity;} public void setQuantity(int v){quantity=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public Instant getCreatedAt(){return createdAt;} public Instant getNotifiedAt(){return notifiedAt;} public void setNotifiedAt(Instant v){notifiedAt=v;}
}

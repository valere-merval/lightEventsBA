package com.lightevents.eventops;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.time.Instant;

@Entity @Table(name="event_team_members")
public class TeamMember {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long eventId; private Long accountId; @Email private String email; private String fullName; private String role="STAFF"; @Column(length=1000) private String permissions; private boolean active=true; private Instant invitedAt=Instant.now();
    public Long getId(){return id;} public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public Long getAccountId(){return accountId;} public void setAccountId(Long v){accountId=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;} public String getRole(){return role;} public void setRole(String v){role=v;} public String getPermissions(){return permissions;} public void setPermissions(String v){permissions=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public Instant getInvitedAt(){return invitedAt;}
}

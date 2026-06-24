package com.lightevents.eventops;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="organizer_applications")
public class OrganizerApplication {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long organizerAccountId;
    @NotBlank private String businessName;
    @Email private String contactEmail;
    private String websiteUrl;
    @Column(length=2000) private String description;
    @Column(unique=true, nullable=false) private String apiKey = UUID.randomUUID().toString();
    private boolean enabled = true;
    private Instant createdAt = Instant.now();
    public Long getId(){return id;} public Long getOrganizerAccountId(){return organizerAccountId;} public void setOrganizerAccountId(Long v){organizerAccountId=v;} public String getBusinessName(){return businessName;} public void setBusinessName(String v){businessName=v;} public String getContactEmail(){return contactEmail;} public void setContactEmail(String v){contactEmail=v;} public String getWebsiteUrl(){return websiteUrl;} public void setWebsiteUrl(String v){websiteUrl=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public String getApiKey(){return apiKey;} public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){enabled=v;} public Instant getCreatedAt(){return createdAt;}
}

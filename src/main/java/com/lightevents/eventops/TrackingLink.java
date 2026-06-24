package com.lightevents.eventops;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="tracking_links", uniqueConstraints=@UniqueConstraint(columnNames="slug"))
public class TrackingLink {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long eventId; private Long campaignId; private String slug; @Column(length=1200) private String url; private String utmSource; private String utmMedium; private String utmCampaign; private long clicks; private Instant createdAt=Instant.now();
    public Long getId(){return id;} public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public Long getCampaignId(){return campaignId;} public void setCampaignId(Long v){campaignId=v;} public String getSlug(){return slug;} public void setSlug(String v){slug=v;} public String getUrl(){return url;} public void setUrl(String v){url=v;} public String getUtmSource(){return utmSource;} public void setUtmSource(String v){utmSource=v;} public String getUtmMedium(){return utmMedium;} public void setUtmMedium(String v){utmMedium=v;} public String getUtmCampaign(){return utmCampaign;} public void setUtmCampaign(String v){utmCampaign=v;} public long getClicks(){return clicks;} public void setClicks(long v){clicks=v;} public Instant getCreatedAt(){return createdAt;}
}

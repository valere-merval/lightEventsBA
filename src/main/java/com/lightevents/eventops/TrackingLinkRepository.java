package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface TrackingLinkRepository extends JpaRepository<TrackingLink,Long>{ List<TrackingLink> findByEventId(Long eventId); List<TrackingLink> findByCampaignId(Long campaignId); Optional<TrackingLink> findBySlug(String slug); }

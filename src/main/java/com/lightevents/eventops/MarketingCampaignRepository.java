package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface MarketingCampaignRepository extends JpaRepository<MarketingCampaign,Long>{ List<MarketingCampaign> findByEventId(Long eventId); }

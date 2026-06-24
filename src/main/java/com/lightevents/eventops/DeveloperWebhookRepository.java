package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface DeveloperWebhookRepository extends JpaRepository<DeveloperWebhook,Long>{ List<DeveloperWebhook> findByEventId(Long eventId); }

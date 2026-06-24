package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface WaitlistEntryRepository extends JpaRepository<WaitlistEntry,Long>{ List<WaitlistEntry> findByEventId(Long eventId); List<WaitlistEntry> findByEventIdAndStatusOrderByCreatedAtAsc(Long eventId, String status); }

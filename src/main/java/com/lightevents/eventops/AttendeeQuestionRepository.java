package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface AttendeeQuestionRepository extends JpaRepository<AttendeeQuestion,Long>{ List<AttendeeQuestion> findByEventIdOrderBySortOrderAsc(Long eventId); }

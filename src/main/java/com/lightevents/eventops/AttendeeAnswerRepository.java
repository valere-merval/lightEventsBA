package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface AttendeeAnswerRepository extends JpaRepository<AttendeeAnswer,Long>{ List<AttendeeAnswer> findByAttendeeId(Long attendeeId); List<AttendeeAnswer> findByEventId(Long eventId); }

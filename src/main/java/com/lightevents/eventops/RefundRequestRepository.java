package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface RefundRequestRepository extends JpaRepository<RefundRequest,Long>{ List<RefundRequest> findByEventId(Long eventId); List<RefundRequest> findByReservationId(Long reservationId); }

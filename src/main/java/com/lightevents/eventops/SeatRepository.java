package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SeatRepository extends JpaRepository<Seat,Long>{ List<Seat> findBySeatMapId(Long seatMapId); List<Seat> findByEventId(Long eventId); Optional<Seat> findBySeatMapIdAndSeatLabelIgnoreCase(Long seatMapId, String seatLabel); }

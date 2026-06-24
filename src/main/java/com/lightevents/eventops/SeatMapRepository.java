package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SeatMapRepository extends JpaRepository<SeatMap,Long>{ List<SeatMap> findByEventId(Long eventId); }

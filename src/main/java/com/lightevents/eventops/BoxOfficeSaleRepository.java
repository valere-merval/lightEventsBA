package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface BoxOfficeSaleRepository extends JpaRepository<BoxOfficeSale,Long>{ List<BoxOfficeSale> findByEventId(Long eventId); }

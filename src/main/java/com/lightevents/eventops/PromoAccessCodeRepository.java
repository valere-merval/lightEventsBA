package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface PromoAccessCodeRepository extends JpaRepository<PromoAccessCode,Long>{ List<PromoAccessCode> findByEventId(Long eventId); Optional<PromoAccessCode> findByEventIdAndCodeIgnoreCase(Long eventId, String code); }

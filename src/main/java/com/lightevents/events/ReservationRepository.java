package com.lightevents.events;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.List; import java.util.Optional; import java.time.Instant;
public interface ReservationRepository extends JpaRepository<Reservation, Long> { Optional<Reservation> findByReference(String reference); List<Reservation> findByBuyerEmailIgnoreCaseOrderByCreatedAtDesc(String buyerEmail); List<Reservation> findByStatusAndExpiresAtBefore(ReservationStatus status, Instant now); }

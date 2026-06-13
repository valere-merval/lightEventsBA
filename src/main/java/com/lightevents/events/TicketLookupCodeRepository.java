package com.lightevents.events;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface TicketLookupCodeRepository extends JpaRepository<TicketLookupCode, Long> { Optional<TicketLookupCode> findTopByEmailIgnoreCaseOrderByIdDesc(String email); }

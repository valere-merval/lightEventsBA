package com.lightevents.events;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
    Optional<Attendee> findByQrCode(String qrCode);
    List<Attendee> findByEventId(Long eventId);
    long countByStatus(CheckInStatus status);
}

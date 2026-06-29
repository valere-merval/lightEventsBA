package com.lightevents.events;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface EventRepository extends JpaRepository<Event, Long> {
    @EntityGraph(attributePaths = "tickets")
    Optional<Event> findById(Long id);
    @EntityGraph(attributePaths = "tickets")
    List<Event> findByStatusOrderByStartsAtAsc(EventStatus status);
    List<Event> findByStatusAndCategoryIgnoreCaseOrderByStartsAtAsc(EventStatus status, String category);
    List<Event> findByStatusAndCountryIgnoreCaseOrderByStartsAtAsc(EventStatus status, String country);
    List<Event> findByStatusAndCityIgnoreCaseOrderByStartsAtAsc(EventStatus status, String city);
}

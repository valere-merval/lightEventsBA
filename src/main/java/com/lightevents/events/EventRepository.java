package com.lightevents.events;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatusOrderByStartsAtAsc(EventStatus status);
    List<Event> findByStatusAndCategoryIgnoreCaseOrderByStartsAtAsc(EventStatus status, String category);
    List<Event> findByStatusAndCountryIgnoreCaseOrderByStartsAtAsc(EventStatus status, String country);
    List<Event> findByStatusAndCityIgnoreCaseOrderByStartsAtAsc(EventStatus status, String city);
}

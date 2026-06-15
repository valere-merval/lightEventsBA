package com.lightevents.destinations;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface DestinationSpotRepository extends JpaRepository<DestinationSpot,Long>{ List<DestinationSpot> findAllByOrderBySortOrderAscTitleAsc(); }

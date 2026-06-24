package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface OrganizerApplicationRepository extends JpaRepository<OrganizerApplication,Long>{ List<OrganizerApplication> findByOrganizerAccountId(Long organizerAccountId); Optional<OrganizerApplication> findByApiKey(String apiKey); }

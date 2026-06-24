package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface TeamMemberRepository extends JpaRepository<TeamMember,Long>{ List<TeamMember> findByEventId(Long eventId); }

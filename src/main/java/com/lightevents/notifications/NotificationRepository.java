package com.lightevents.notifications;
import org.springframework.data.jpa.repository.JpaRepository;
public interface NotificationRepository extends JpaRepository<NotificationLog, Long> {}

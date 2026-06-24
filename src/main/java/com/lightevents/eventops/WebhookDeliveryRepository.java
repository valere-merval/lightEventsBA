package com.lightevents.eventops;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery,Long>{ List<WebhookDelivery> findByWebhookId(Long webhookId); }

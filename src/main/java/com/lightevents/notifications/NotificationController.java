package com.lightevents.notifications;
import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/notifications")
public class NotificationController { private final NotificationRepository repo; public NotificationController(NotificationRepository repo){this.repo=repo;} @GetMapping public List<NotificationLog> list(){return repo.findAll();} @PostMapping public NotificationLog queue(@RequestBody NotificationLog n){n.setStatus("QUEUED"); return repo.save(n);} }

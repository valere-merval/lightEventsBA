package com.lightevents.docs;
import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/docs")
public class IntegrationController { @GetMapping("/integrations") public Map<String,Object> integrations(){ return Map.of("apiBase","/api","html","Add the JS widget and use data attributes for event lists or ticket checkout.","react","Use fetch/axios against /api/events and /api/events/{id}/reservations.","reactNative","Use the same REST APIs; scanner app calls /api/events/check-in.","flutter","Use http package and QR scanner plugin.","wordpress","Install LightEvents plugin, configure API key, use [lightevents-calendar] and [lightevents-ticket event=123].","joomla","Install module or embed JS widget."); }}

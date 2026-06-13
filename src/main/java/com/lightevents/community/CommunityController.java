package com.lightevents.community;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/communities")
public class CommunityController { private final CommunityRepository repo; public CommunityController(CommunityRepository repo){this.repo=repo;} @GetMapping public List<Community> list(){return repo.findAll();} @PostMapping public Community create(@Valid @RequestBody Community c){ if(c.getSlug()==null || c.getSlug().isBlank()) c.setSlug(c.getName().toLowerCase().replaceAll("[^a-z0-9]+","-").replaceAll("(^-|-$)","")); return repo.save(c);} }

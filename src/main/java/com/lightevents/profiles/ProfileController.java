package com.lightevents.profiles;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/profiles")
public class ProfileController {
 private final UserProfileRepository profiles; public ProfileController(UserProfileRepository profiles){this.profiles=profiles;}
 @GetMapping public List<UserProfile> list(){return profiles.findAll();}
 @GetMapping("/{id}") public UserProfile get(@PathVariable Long id){return profiles.findById(id).orElseThrow();}
 @PostMapping public UserProfile create(@Valid @RequestBody UserProfile p){return profiles.save(p);}
}

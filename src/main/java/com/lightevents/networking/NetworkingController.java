package com.lightevents.networking;
import com.lightevents.profiles.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/networking")
public class NetworkingController {
 private final UserProfileRepository profiles; private final ConnectionRepository connections;
 public NetworkingController(UserProfileRepository profiles, ConnectionRepository connections){this.profiles=profiles;this.connections=connections;}
 public record MatchResult(Long profileId, String fullName, String headline, String company, int score, String reason, String whatsappNumber) {}
 public record ConnectRequest(Long requesterProfileId, Long targetProfileId, String note) {}
 @GetMapping("/business-match") public List<MatchResult> match(@RequestParam Long profileId){
   UserProfile me=profiles.findById(profileId).orElseThrow(); String needs=norm(me.getLookingFor()+" "+me.getSkills());
   return profiles.findAll().stream().filter(p->!p.getId().equals(profileId)).map(p->{int s=score(needs, norm(p.getOffering()+" "+p.getSkills()+" "+p.getHeadline())); return new MatchResult(p.getId(),p.getFullName(),p.getHeadline(),p.getCompany(),s, s>35?"Très pertinent pour votre objectif":"Contact intéressant à explorer", p.getWhatsappNumber());}).sorted(Comparator.comparing(MatchResult::score).reversed()).limit(12).toList();
 }
 @PostMapping("/connections") public Connection connect(@RequestBody ConnectRequest r){ Connection c=new Connection(); c.setRequesterProfileId(r.requesterProfileId()); c.setTargetProfileId(r.targetProfileId()); c.setNote(r.note()); return connections.save(c); }
 private static String norm(String v){return v==null?"":v.toLowerCase();} private static int score(String a,String b){int score=10; for(String t:a.split("[^a-z0-9à-ÿ]+")){ if(t.length()>3 && b.contains(t)) score+=18; } return Math.min(score,99);}
}

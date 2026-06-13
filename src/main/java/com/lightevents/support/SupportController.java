package com.lightevents.support;
import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/support")
public class SupportController { public record ChatRequest(String message, Long organizerId, String whatsappNumber) {}
 @GetMapping("/faq") public List<Map<String,String>> faq(){ return List.of(Map.of("q","Comment recevoir mes tickets ?","a","Par email, WhatsApp ou SMS selon votre choix."),Map.of("q","Combien LightEvents prélève ?","a","4,5% sur les paiements encaissés, puis reversement automatique à l'organisateur."),Map.of("q","Puis-je intégrer LightEvents sur WordPress ?","a","Oui via shortcode, widget JS ou API REST.")); }
 @PostMapping("/chatbot") public Map<String,Object> chatbot(@RequestBody ChatRequest r){ String msg=r.message()==null?"":r.message().toLowerCase(); String answer=msg.contains("organisateur")?"Je peux vous rediriger vers le chatbot de l'organisateur ou ouvrir une conversation WhatsApp." : msg.contains("ticket")?"Entrez votre email dans la page Mes tickets pour recevoir un code et retrouver vos billets." : "Bonjour, je suis l'assistant LightEvents. Je peux aider pour billets, paiements, organisateurs, événements et remboursements."; return Map.of("answer",answer,"whatsappReady",true,"handoff", r.organizerId()!=null?"ORGANIZER_BOT":"LIGHTEVENTS_SUPPORT"); }
}

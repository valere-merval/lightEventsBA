package com.lightevents.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {
    private final RestClient rest = RestClient.create("https://api.openai.com/v1");
    @Value("${openai.api-key:}") private String apiKey;
    @Value("${openai.model:gpt-4.1-mini}") private String model;
    public String chat(String prompt) {
        if (apiKey == null || apiKey.isBlank()) return "Mode preview IA: ajoute OPENAI_API_KEY dans .env pour activer les réponses OpenAI.";
        Map<String,Object> body = Map.of("model", model, "messages", List.of(Map.of("role","system","content","Tu es l'assistant LightEvents, utile et concis."), Map.of("role","user","content", prompt == null ? "" : prompt)));
        Map res = rest.post().uri("/chat/completions").headers(h -> h.setBearerAuth(apiKey)).body(body).retrieve().body(Map.class);
        try {
            List choices = (List) res.get("choices"); Map choice = (Map) choices.get(0); Map message = (Map) choice.get("message"); return String.valueOf(message.get("content"));
        } catch (Exception e) { return "Réponse IA indisponible pour le moment."; }
    }
}

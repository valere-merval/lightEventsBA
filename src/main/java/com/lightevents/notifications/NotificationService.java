package com.lightevents.notifications;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@ConditionalOnClass(JavaMailSender.class)
public class NotificationService {
    private final JavaMailSender mailSender;
    private final RestClient rest = RestClient.create();
    @Value("${app.mail.from:no-reply@lightevents.local}") private String from;
    @Value("${twilio.account-sid:}") private String twilioSid;
    @Value("${twilio.auth-token:}") private String twilioToken;
    @Value("${twilio.whatsapp-from:}") private String whatsappFrom;
    @Value("${twilio.sms-from:}") private String smsFrom;

    public NotificationService(JavaMailSender mailSender) { this.mailSender = mailSender; }

    public Map<String, Object> sendEmail(String to, String subject, String body) {
        if (blank(to)) return Map.of("sent", false, "reason", "missing email");
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from); msg.setTo(to); msg.setSubject(subject); msg.setText(body);
        mailSender.send(msg);
        return Map.of("sent", true, "channel", "email", "to", to);
    }

    public Map<String, Object> sendWhatsApp(String to, String body) { return sendTwilio(to, body, true); }
    public Map<String, Object> sendSms(String to, String body) { return sendTwilio(to, body, false); }

    private Map<String, Object> sendTwilio(String to, String body, boolean whatsapp) {
        if (blank(twilioSid) || blank(twilioToken) || blank(to)) return Map.of("sent", false, "preview", true, "channel", whatsapp ? "whatsapp" : "sms", "to", to, "body", body);
        String fromNumber = whatsapp ? whatsappFrom : smsFrom;
        String destination = whatsapp && !to.startsWith("whatsapp:") ? "whatsapp:" + to : to;
        String sender = whatsapp && !fromNumber.startsWith("whatsapp:") ? "whatsapp:" + fromNumber : fromNumber;
        Map<String, String> form = new LinkedHashMap<>(); form.put("From", sender); form.put("To", destination); form.put("Body", body);
        return rest.post().uri("https://api.twilio.com/2010-04-01/Accounts/{sid}/Messages.json", twilioSid)
                .headers(h -> h.setBasicAuth(twilioSid, twilioToken)).body(form).retrieve().body(Map.class);
    }
    private static boolean blank(String v){ return v==null || v.isBlank(); }
}

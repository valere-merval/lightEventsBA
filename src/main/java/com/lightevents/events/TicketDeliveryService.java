package com.lightevents.events;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lightevents.notifications.NotificationService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

@Service
public class TicketDeliveryService {
    private final Optional<NotificationService> notifications;

    public TicketDeliveryService(Optional<NotificationService> notifications) {
        this.notifications = notifications;
    }

    public void sendTickets(Event event, Reservation reservation, List<Attendee> attendees) {
        notifications.ifPresent(notification -> attendees.forEach(attendee -> sendTicket(notification, event, reservation, attendee)));
    }

    private void sendTicket(NotificationService notification, Event event, Reservation reservation, Attendee attendee) {
        if (blank(attendee.getEmail())) return;
        try {
            String body = "Bonjour " + safe(attendee.getFullName()) + ",\n\n"
                    + "Voici votre ticket LightEvents. Le QR Code en pièce jointe sera scanné à l'entrée avec l'app LightEvents Organizer.\n\n"
                    + "Événement: " + safe(event.getTitle()) + "\n"
                    + "Référence réservation: " + safe(reservation.getReference()) + "\n"
                    + "Statut ticket: " + attendee.getStatus() + "\n"
                    + "Code QR brut: " + attendee.getQrCode() + "\n\n"
                    + "Gardez cet email et présentez le QR Code au check-in.";
            notification.sendEmailWithAttachment(
                    attendee.getEmail(),
                    "Votre ticket LightEvents - " + safe(event.getTitle()),
                    body,
                    "ticket-lightevents-" + attendee.getId() + ".png",
                    qrPng(attendee.getQrCode())
            );
        } catch (Exception ignored) {
            // Ticket delivery must not block reservation/payment creation. Delivery can be retried later.
        }
    }

    private byte[] qrPng(String value) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(value, BarcodeFormat.QR_CODE, 512, 512);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return out.toByteArray();
    }

    private static boolean blank(String value) { return value == null || value.isBlank(); }
    private static String safe(String value) { return value == null ? "" : value; }
}

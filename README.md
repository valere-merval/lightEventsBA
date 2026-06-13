
# LightEvents Backend

Spring Boot / Java 25 backend for **LightEvents**: Eventbrite + LinkedIn + WhatsApp + Mobile Money for African-first events.

## Modules

- Events, tickets, QR check-in
- Professional profiles and business matching
- Communities
- Mobile Money transaction initiation placeholders
- WhatsApp/SMS/email notification queue placeholders
- Dashboard metrics

## Run locally

```bash
./mvnw spring-boot:run
```

Default DB is in-memory H2 for development. Copy `.env.example` and use PostgreSQL for production.

## Key endpoints

- `GET /api/events`
- `POST /api/events`
- `POST /api/events/{id}/tickets`
- `POST /api/events/{id}/attendees`
- `POST /api/events/check-in`
- `GET /api/profiles`
- `GET /api/networking/business-match?profileId=1`
- `POST /api/payments/mobile-money/initiate`
- `GET /api/dashboard/summary`

## Production roadmap

1. Add JWT/OAuth2 auth and organizer roles.
2. Replace payment placeholders with Orange Money, MTN MoMo, Wave, Airtel/Flutterwave/CinetPay adapters.
3. Add offline QR sync for mobile check-in.
4. Add WordPress plugin consuming these REST APIs.
5. Add event recommendation and AI content generation services.

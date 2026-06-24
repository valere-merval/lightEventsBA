# LightEvents Backend

Spring Boot / Java 25 backend for **LightEvents**.

## Documentation

La documentation fonctionnelle complète est disponible ici : [`docs/LIGHTEVENTS_GUIDE_FR.md`](docs/LIGHTEVENTS_GUIDE_FR.md).

## V2 capabilities

- Verified account registration by email/SMS/WhatsApp code before event publishing
- Organizer payout preferences and 4.5% platform fee calculation
- Events with categories, location, payment methods, preview-ready media fields
- Multi-ticket reservations with per-ticket holder details
- Ticket QR code delivery by email with PNG attachment
- QR check-in endpoint, rejects a second scan
- Ticket lookup by email + verification code
- S3-compatible presigned upload placeholder
- AI image generation placeholder endpoint
- Support FAQ/chatbot endpoint with WhatsApp handoff metadata
- API integration docs endpoint
- EventOps modules: box-office, seating, promos, waitlist, refunds, teams, custom forms, marketing and webhooks
- GetMiPay Mobile Money PayIn integration for Orange Money, MTN Money, Wave, Airtel Money and Moov Money

## Run

```bash
./mvnw spring-boot:run
```

## Auth flow for organizers

1. `POST /api/auth/register` with role `ORGANIZER`.
2. Verify using `POST /api/auth/verify`.
3. Publish with header `X-LightEvents-Token: <apiToken>`.

## Important endpoints

- `GET /api/events?country=CI&city=Abidjan&category=business`
- `GET /api/events/categories`
- `GET /api/events/destinations`
- `POST /api/events`
- `POST /api/events/{id}/reservations`
- `POST /api/events/check-in`
- `POST /api/events/tickets/lookup/request-code`
- `POST /api/payments/checkout`
- `GET /api/payments/{reference}/status`
- `GET /api/payments/mobile-money/services`
- `POST /api/payments/getmipay/webhook`
- `POST /api/media/presign`
- `POST /api/media/ai-image`
- `POST /api/support/chatbot`

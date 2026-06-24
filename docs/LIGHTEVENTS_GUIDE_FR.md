# Documentation fonctionnelle LightEvents

Dernière mise à jour : 2026-06-24

## 1. Vision générale

LightEvents est une plateforme de billetterie et d’organisation d’événements inspirée d’Eventbrite, mais pensée pour l’Afrique, la diaspora, WordPress, WhatsApp, Mobile Money et les PME.

La plateforme est composée de quatre projets :

| Projet | Rôle |
| --- | --- |
| `lightEventsBA` | Backend Spring Boot. Il contient l’API, la base de données, les réservations, tickets, QR codes, check-in, modules EventOps, factures et paiements. |
| `lightEventsFE` | Frontend React web. Il sert aux participants et organisateurs : découvrir, créer, réserver, payer, gérer les événements. |
| `lightEventsWP` | Plugin WordPress. Il permet à un site WordPress d’afficher les événements LightEvents et de vendre/réserver des billets. |
| `lightEventsMobile` | App Expo / React Native pour organisateurs. Elle sert au check-in QR, au box-office et aux opérations terrain. |

## 2. Parcours principaux

### 2.1 Création et connexion compte

1. L’utilisateur crée un compte avec son email.
2. Le backend génère un code à 6 chiffres.
3. Le code est envoyé par email.
4. L’utilisateur entre le code.
5. Si le code est correct : le compte est vérifié et connecté.
6. Si le code est faux : l’utilisateur reste sur la page avec un message d’échec.

Endpoints principaux :

```txt
POST /api/auth/register
POST /api/auth/verify
POST /api/auth/login/request-code
POST /api/auth/login/verify
```

Le frontend stocke ensuite l’`apiToken` dans le `localStorage`. L’app mobile stocke la session avec `AsyncStorage`.

### 2.2 Création d’événement

Un organisateur connecté et vérifié peut créer un événement depuis `/create`.

Un événement contient notamment :

- titre, description, catégorie ;
- lieu physique ou lien online ;
- date de début/fin ;
- capacité ;
- image principale, galerie, vidéo ;
- options de billets ;
- période de réservation sans paiement immédiat ;
- mode de reversement organisateur.

Endpoint :

```txt
POST /api/events
Header: X-LightEvents-Token: <apiToken>
```

### 2.3 Réservation de tickets

Un participant peut réserver ou payer depuis :

- le frontend React `/events/:id` ;
- le plugin WordPress `[lightevents_checkout event="123"]` ;
- l’app mobile organisateur en mode box-office.

Endpoint principal :

```txt
POST /api/events/{eventId}/reservations
```

Le backend crée :

- une réservation ;
- un ou plusieurs `Attendee` ;
- un QR code unique par attendee ;
- un statut de ticket : `RESERVED`, `PAID`, etc.

### 2.4 Envoi des tickets QR par email

Dès qu’une réservation crée des tickets, LightEvents envoie un email à chaque détenteur de ticket quand son email est connu.

Chaque email contient :

- nom du détenteur ;
- nom de l’événement ;
- référence de réservation ;
- statut du ticket ;
- code QR brut ;
- pièce jointe PNG contenant le QR Code.

Le QR Code encode directement la valeur `attendee.qrCode`. Cette valeur est celle scannée par l’app mobile au check-in.

Service backend :

```txt
com.lightevents.events.TicketDeliveryService
```

Important : l’envoi email ne bloque pas la réservation. Si le SMTP échoue, la réservation existe quand même. En production, il faudra ajouter une file de retry.

### 2.5 Check-in avec l’app mobile

L’organisateur ouvre `lightEventsMobile`, se connecte par email + code, puis va dans l’onglet `Scanner`.

Fonctionnement :

1. Le participant présente son QR Code reçu par email.
2. L’app mobile lit le QR avec la caméra.
3. L’app envoie le code au backend.
4. Le backend vérifie que le QR existe.
5. Si le ticket n’a jamais été scanné : statut `CHECKED_IN`.
6. Si le ticket a déjà été scanné : erreur, second scan refusé.

Endpoint :

```txt
POST /api/events/check-in
Body: { "qrCode": "..." }
```

Il existe aussi un champ manuel dans l’app mobile pour coller le code QR brut si la caméra ne fonctionne pas.

## 3. Paiements : état actuel

Les paiements ne sont pas encore à considérer comme une intégration production complète.

Actuellement, le backend prépare :

- transaction ;
- frais plateforme 4,5 % ;
- montant net organisateur ;
- checkout Stripe si clé configurée ;
- checkout PayPal si clés configurées ;
- fallback preview si les clés ne sont pas configurées.

Endpoints :

```txt
POST /api/payments/checkout
POST /api/payments/confirm
POST /api/events/reservations/{reference}/confirm-payment
```

Avant de renforcer les paiements, LightEvents garantit maintenant que les tickets possèdent un QR Code et que l’app mobile peut scanner ce QR.

À faire pour production :

- webhooks Stripe/PayPal réels ;
- Mobile Money réel Orange/MTN/Wave/Airtel/Moov ;
- statut paiement fiable ;
- retry email ;
- reçu/facture systématique ;
- règles de remboursement automatisées.

## 4. Modules EventOps ajoutés

Les modules EventOps couvrent les fonctionnalités avancées type Eventbrite.

Package backend :

```txt
com.lightevents.eventops
```

### 4.1 App organisateur / lien box-office

Permet de créer un lien ou une clé pour un appareil de caisse/check-in.

```txt
POST /api/organizer/events/{eventId}/box-office-link
POST /api/organizer/apps
GET  /api/organizer/apps
```

### 4.2 Vente à l’entrée / box-office

Permet de vendre un ticket sur place.

```txt
POST /api/organizer/events/{eventId}/door-sales
POST /api/box-office/sales
GET  /api/box-office/sales?eventId=...
```

### 4.3 Plan de salle / seating

```txt
POST /api/seat-maps
GET  /api/events/{eventId}/seat-maps
GET  /api/seat-maps/{seatMapId}
POST /api/seat-maps/seats
POST /api/seats/{seatId}/reserve
POST /api/seats/{seatId}/release
```

### 4.4 Codes promo / access codes

```txt
POST /api/promo-codes
GET  /api/events/{eventId}/promo-codes
POST /api/promo-codes/redeem
```

### 4.5 Waitlist

```txt
POST /api/waitlist
GET  /api/events/{eventId}/waitlist
POST /api/events/{eventId}/waitlist/notify-next
```

### 4.6 Refunds / annulations

```txt
POST /api/refunds
GET  /api/refunds?eventId=...
POST /api/refunds/{id}/status
```

### 4.7 Équipe / permissions

```txt
POST /api/team-members
GET  /api/events/{eventId}/team
```

### 4.8 Formulaires personnalisés participants

```txt
POST /api/attendee-questions
GET  /api/events/{eventId}/attendee-questions
POST /api/attendee-answers
GET  /api/attendees/{attendeeId}/answers
```

### 4.9 Marketing / tracking

```txt
POST /api/marketing-campaigns
GET  /api/events/{eventId}/marketing-campaigns
POST /api/tracking-links
POST /api/tracking-links/{slug}/click
```

### 4.10 Webhooks développeur

```txt
POST /api/developer/webhooks
GET  /api/developer/webhooks?eventId=...
POST /api/developer/webhooks/{webhookId}/test
GET  /api/developer/webhooks/{webhookId}/deliveries
```

## 5. Frontend React

Le frontend contient les pages principales :

| Route | Fonction |
| --- | --- |
| `/` | Accueil, découverte événements |
| `/events` | Liste et filtres |
| `/events/:id` | Détail, réservation, paiement |
| `/create` | Création événement organisateur |
| `/auth` | Création compte, vérification, connexion par code email |
| `/tickets` | Consultation tickets/factures par code email |
| `/organizer` | Dashboard organisateur + modules EventOps |
| `/admin` | Supervision admin |
| `/help` | FAQ/chatbot |

Le hub EventOps du dashboard organisateur expose les 10 modules avancés et appelle les endpoints backend correspondants.

## 6. App mobile LightEvents Organizer

Repo : `lightEventsMobile`.

Technologie : Expo / React Native.

Fonctions actuelles :

- login email + code ;
- stockage session ;
- liste événements ;
- dashboard ventes/check-ins ;
- scanner QR via `expo-camera` ;
- fallback manuel ;
- box-office ;
- écrans MVP waitlist, refunds, promos, seating, team, marketing, settings.

Configuration :

```bash
cd lightEventsMobile
cp .env.example .env
npm install --include=dev
npm run typecheck
npm start
```

Variable importante :

```txt
EXPO_PUBLIC_LIGHTEVENTS_API_BASE=http://<backend-host>:8080/api
```

Sur Android emulator :

```txt
EXPO_PUBLIC_LIGHTEVENTS_API_BASE=http://10.0.2.2:8080/api
```

## 7. Plugin WordPress

Repo : `lightEventsWP`.

Le plugin sert à intégrer LightEvents dans un site WordPress existant.

### 7.1 Configuration

Dans WordPress :

1. Installer le plugin dans `wp-content/plugins/lightevents`.
2. Activer `LightEvents for WordPress`.
3. Aller dans `Réglages → LightEvents`.
4. Configurer :
   - URL API : `https://api.votre-domaine.com/api` ;
   - URL plateforme : `https://app.votre-domaine.com` ;
   - token API optionnel ;
   - page détail WordPress optionnelle.

### 7.2 Shortcodes

```txt
[lightevents_events]
[lightevents_events view="calendar" country="Côte d'Ivoire" category="business"]
[lightevents_events view="list" organizer="MWEMBA"]
[lightevents_event id="123"]
[lightevents_checkout event="123"]
```

### 7.3 Fonctionnement checkout WordPress

1. Le visiteur choisit un billet.
2. Il entre nom, email, téléphone.
3. Le plugin appelle `POST /api/events/{eventId}/reservations`.
4. Le backend crée les tickets et QR Codes.
5. Le backend envoie les tickets QR par email.
6. Si le visiteur choisit paiement immédiat, le plugin appelle ensuite `POST /api/payments/checkout`.

Le plugin ne génère pas lui-même les QR Codes. C’est volontaire : le backend reste la source de vérité.

## 8. États importants

### Attendee / ticket

- `RESERVED` : ticket réservé, pas encore forcément payé.
- `PAID` : ticket payé ou gratuit confirmé.
- `CHECKED_IN` : ticket déjà scanné à l’entrée.
- `CANCELLED` : ticket annulé.

### Reservation

- `HELD` : réservation temporaire.
- `PAYMENT_PENDING` : paiement en cours.
- `PAID` : paiement confirmé.
- `EXPIRED` : réservation expirée.

## 9. Ce qui reste à durcir avant production

LightEvents a maintenant les briques fonctionnelles MVP, mais il faut encore durcir :

1. Authentification/autorisation fine sur tous les endpoints EventOps.
2. Webhooks paiement réels.
3. Providers Mobile Money réels.
4. Retry fiable des emails.
5. Templates email HTML plus propres.
6. Tickets PDF/Wallet en plus du QR PNG.
7. Offline mode mobile pour scanner sans réseau.
8. Synchronisation quand le réseau revient.
9. Tests métiers complets : réservation, paiement, remboursement, check-in.
10. Audit sécurité avant exposition publique.

## 10. Résumé simple

- Le backend est la source de vérité.
- Le frontend web sert aux participants et organisateurs.
- Le plugin WordPress vend/réserve via le backend.
- L’app mobile scanne les QR Codes générés par le backend.
- Chaque ticket a un QR unique.
- Chaque QR peut être scanné une seule fois.
- Les paiements sont préparés, mais doivent encore être finalisés proprement avant production.

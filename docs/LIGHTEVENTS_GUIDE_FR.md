# Documentation fonctionnelle LightEvents

Dernière mise à jour : 2026-06-24

## 1. Vision générale

LightEvents est une plateforme de billetterie et d’organisation d’événements pensée pour l’Afrique, la diaspora, WordPress, WhatsApp, Mobile Money et les PME.

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

- titre, description, une ou plusieurs catégories ;
- lieu physique ou lien online ;
- date de début/fin ;
- capacité ;
- image principale, galerie, vidéo ;
- options de billets multiples avec nom, description, prix, devise, quantité et disponibilité ;
- période de réservation sans paiement immédiat ;
- mode de reversement organisateur.

Endpoint :

```txt
POST /api/events
Header: X-LightEvents-Token: <apiToken>
```

Catégories :

- le champ historique `category` garde la catégorie principale ;
- le champ `categories` accepte une liste de catégories ;
- l’événement est trouvé dans `/api/events?category=...` si la catégorie cherchée correspond à l’une des catégories sélectionnées ;
- le frontend affiche toutes les catégories sélectionnées pour éviter qu’un événement multi-thème soit présenté comme mono-catégorie.

Prix et options :

- un événement peut avoir plusieurs options : Standard, VIP, Business, Early Bird, Gratuit, etc. ;
- les listes et highlights affichent un résumé de prix basé sur toutes les options ;
- la page détail affiche toutes les options avec prix, description et places restantes ;
- le formulaire d’achat/réservation laisse le participant choisir l’option précise.

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


## 2.6 Ce qu’un organisateur peut faire

Un organisateur peut :

- créer un compte organisateur et le vérifier par code email ;
- publier un événement physique ou en ligne ;
- choisir plusieurs catégories pour améliorer la découverte ;
- renseigner lieu, salle, adresse exacte, dates, capacité, médias, galerie et vidéo ;
- générer ou uploader une image principale ;
- créer plusieurs options de billets avec prix, devise, quota et description ;
- définir la durée de validité d’une réservation sans paiement immédiat ;
- définir jusqu’à quand les réservations sans paiement sont acceptées ;
- choisir comment recevoir l’argent : PayPal ou références bancaires ;
- consulter son dashboard organisateur ;
- voir participants, réservations, statuts et pays des participants ;
- scanner les QR Codes avec l’app LightEvents Organizer ;
- saisir manuellement un QR Code si la caméra ne fonctionne pas ;
- vendre à l’entrée via le box-office ;
- utiliser les modules avancés : box-office mobile, vente à l’entrée, plan de salle, codes promo, liste d’attente, remboursements, équipe/rôles, formulaires personnalisés, campagnes marketing/tracking et webhooks développeur ;
- intégrer ses événements sur WordPress via shortcodes ;
- suivre les paiements et confirmations liés aux réservations.

## 2.7 Ce qu’un participant peut faire

Un participant peut :

- découvrir les événements depuis l’accueil, la liste, les pays, villes et catégories ;
- trouver un événement via n’importe quelle catégorie sélectionnée par l’organisateur ;
- ouvrir la page détail pour voir description, lieu, date, médias, paiements acceptés et toutes les options de prix ;
- choisir une option de billet spécifique ;
- réserver sans payer immédiatement si l’organisateur l’autorise encore ;
- payer directement par Orange Money, MTN MoMo, Wave, Airtel Money, Moov Money, Stripe ou PayPal ;
- renseigner les informations de chaque détenteur de ticket ;
- acheter pour une entreprise et renseigner un nom de société ;
- recevoir ses tickets QR par email, avec statut et référence ;
- consulter ses tickets, achats et factures via un code envoyé à son email/téléphone/WhatsApp ;
- présenter son QR Code à l’entrée ;
- contacter l’organisateur depuis la page détail ;
- utiliser le chatbot pour poser des questions sur événements, lieux, prix, paiements et tickets.

## 3. Paiements : état actuel

LightEvents intègre maintenant un premier connecteur Mobile Money via GetMiPay, en plus des connecteurs Stripe/PayPal déjà préparés.

Le backend prépare toujours :

- transaction ;
- frais plateforme 4,5 % ;
- montant net organisateur ;
- référence interne LightEvents ;
- référence fournisseur quand GetMiPay/Stripe/PayPal en retourne une.

### 3.1 Moyens de paiement supportés

```txt
ORANGE_MONEY
MTN_MONEY
WAVE
AIRTEL_MONEY
MOOV_MONEY
STRIPE
PAYPAL
```

### 3.2 Paiement GetMiPay / Mobile Money

Flux technique :

1. Le participant réserve avec `payNow=true`.
2. LightEvents crée la réservation et les tickets QR.
3. Le frontend ou WordPress appelle `POST /api/payments/checkout`.
4. Le backend authentifie GetMiPay via `POST /action/auth`.
5. Le backend appelle GetMiPay `POST /payments/payin` avec :
   - header `Authorization: Bearer <jwt>` ;
   - header `operation: 2` ;
   - header `service: <serviceId>` ;
   - header `otp` si Orange Money le demande ;
   - body `amount`, `currency`, `wallet`, `description`, `customer_name`, `customer_email`, `callback_url`.
6. Le participant valide sur son téléphone / wallet.
7. LightEvents vérifie le statut via `GET /api/payments/{reference}/status` ou reçoit le webhook `/api/payments/getmipay/webhook`.
8. Si GetMiPay répond `success`, LightEvents marque la transaction `SUCCEEDED`, confirme la réservation et remet les tickets en statut payé.

Endpoints LightEvents :

```txt
POST /api/payments/checkout
POST /api/payments/mobile-money/initiate
GET  /api/payments/{reference}/status
GET  /api/payments/mobile-money/services
POST /api/payments/getmipay/webhook
POST /api/payments/confirm
POST /api/events/reservations/{reference}/confirm-payment
```

Configuration backend :

```properties
GETMIPAY_BASE_URL=https://sandbox.getmipay.com/api
GETMIPAY_PUBLIC_API_KEY=...
GETMIPAY_PRIVATE_SECRET_KEY=...
GETMIPAY_CALLBACK_URL=https://api.votre-domaine.com/api/payments/getmipay/webhook
GETMIPAY_SERVICE_ORANGE_MONEY=3
GETMIPAY_SERVICE_MTN_MONEY=1
GETMIPAY_SERVICE_WAVE=...
GETMIPAY_SERVICE_AIRTEL_MONEY=...
GETMIPAY_SERVICE_MOOV_MONEY=...
```

Important : les IDs de services GetMiPay peuvent dépendre du compte marchand et du pays. Après configuration des clés, utilisez `GET /api/payments/mobile-money/services` pour récupérer les services réellement activés sur le compte marchand.

### 3.3 Ce qui reste à durcir avant production paiement

- valider les vrais IDs de services GetMiPay par pays ;
- sécuriser/signaturer les webhooks si GetMiPay fournit une signature ;
- ajouter un écran de suivi paiement après redirection ;
- ajouter retry/polling automatique côté frontend ;
- gérer remboursements Mobile Money ;
- ajouter logs/audit paiement complets.

## 4. Modules EventOps ajoutés

Les modules EventOps couvrent les fonctionnalités avancées nécessaires aux organisateurs professionnels.

Utilisation dans le dashboard organisateur :

1. Se connecter avec un compte organisateur.
2. Ouvrir `/organizer`.
3. Sélectionner l’événement cible dans le menu `Événement cible`.
4. Remplir le formulaire du module souhaité.
5. Cliquer sur `Créer / enregistrer`.
6. Lire la réponse JSON affichée sous la carte pour récupérer l’ID, la clé, le statut ou l’objet créé.

Les champs de type liste acceptent soit une seule valeur (`scan`), soit plusieurs valeurs séparées par virgules (`scan,sell,refund`). Le frontend envoie toujours une vraie liste JSON au backend.

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

Un code de type `PROMO` peut être saisi par le participant dans le formulaire d’achat de ticket. Le backend valide le code, vérifie qu’il est actif/non expiré et que sa limite d’utilisation n’est pas atteinte, puis applique `discountPercent` ou `discountAmount` avant de créer le paiement. Les codes de type `ACCESS` restent des codes d’accès privés et ne sont pas traités comme des remises.

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

Le hub EventOps du dashboard organisateur expose les 10 modules avancés et appelle les endpoints backend correspondants. Le dashboard utilise maintenant le token du compte connecté (`X-LightEvents-Token`) pour retrouver les événements liés au compte organisateur, même si l’email saisi dans le formulaire événement diffère de l’email de connexion.

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

## Paiement, tickets et reversements — durcissement production

- Lors d'un paiement direct, LightEvents crée une réservation `PAYMENT_PENDING`, mais le ticket QR n'est envoyé qu'après confirmation du paiement par le provider.
- Si le paiement échoue ou expire, la réservation est annulée, les places sont libérées et les participants temporaires passent en `CANCELLED`.
- Si le paiement réussit, la réservation passe en `PAID`, les participants passent en `PAID`, la facture est créée et les tickets QR sont envoyés.
- Chaque transaction calcule automatiquement 4,5 % pour LightEvents et 95,5 % net organisateur, en conservant le moyen de reversement choisi par l'organisateur lors de la création de l'événement.
- Le dashboard organisateur permet d'exporter la liste CSV complète des participants avec coordonnées, statut, QR code, référence de réservation et horodatages de check-in.

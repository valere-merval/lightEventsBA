# Déploiement LightEvents Backend

Ce backend Spring Boot est prêt pour Render via `render.yaml`.

## Étapes Render

1. Aller sur Render.
2. Créer un **Blueprint** depuis le repo GitHub `valere-merval/lightEventsBA`.
3. Render détecte `render.yaml` et crée :
   - le service web `lightevents-api`
   - la base PostgreSQL `lightevents-db`
4. Une fois déployé, l'API publique sera :

```txt
https://<votre-service-render>.onrender.com/api
```

## À mettre dans WordPress

Dans Réglages → LightEvents :

```txt
API LightEvents = https://<votre-service-render>.onrender.com/api
URL plateforme = https://valere-merval.github.io/lightEventsFE
```

## Variables utiles plus tard

- `STRIPE_SECRET_KEY`
- `PAYPAL_CLIENT_ID`
- `PAYPAL_CLIENT_SECRET`
- `OPENAI_API_KEY`
- `TWILIO_ACCOUNT_SID`
- `TWILIO_AUTH_TOKEN`
- `TWILIO_WHATSAPP_FROM`
- `S3_BUCKET`, `S3_REGION`, `S3_ACCESS_KEY`, `S3_SECRET_KEY`

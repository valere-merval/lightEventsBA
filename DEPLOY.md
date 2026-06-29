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

## Variables Render obligatoires si le service est créé manuellement

Ne mettez jamais les identifiants PostgreSQL dans `application.properties` ou dans GitHub. Ajoutez-les uniquement dans **Render → Web Service → Environment**.

Pour une base Render PostgreSQL, utilisez l'URL **Internal Database URL** :

```txt
DATABASE_URL=<Internal Database URL Render>
DATABASE_DRIVER=org.postgresql.Driver
JPA_DDL_AUTO=update
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
CORS_ALLOWED_ORIGINS=https://valere-merval.github.io,https://valere-merval.github.io/lightEventsFE
FRONTEND_URL=https://valere-merval.github.io/lightEventsFE
```

`DATABASE_USERNAME` et `DATABASE_PASSWORD` sont optionnels si `DATABASE_URL` contient déjà `postgresql://user:password@host/db`. Si vous les renseignez séparément, `DATABASE_USERNAME` doit être l'utilisateur PostgreSQL, pas le mot de passe.

## Variables utiles plus tard

- `STRIPE_SECRET_KEY`
- `PAYPAL_CLIENT_ID`
- `PAYPAL_CLIENT_SECRET`
- `OPENAI_API_KEY`
- `TWILIO_ACCOUNT_SID`
- `TWILIO_AUTH_TOKEN`
- `TWILIO_WHATSAPP_FROM`
- `S3_BUCKET`, `S3_REGION`, `S3_ACCESS_KEY`, `S3_SECRET_KEY`

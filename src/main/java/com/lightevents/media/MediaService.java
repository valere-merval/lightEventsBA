package com.lightevents.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
public class MediaService {
    @Value("${s3.bucket:}") private String bucket;
    @Value("${s3.region:eu-central-1}") private String region;
    @Value("${s3.endpoint:}") private String endpoint;
    @Value("${s3.public-base-url:}") private String publicBaseUrl;
    @Value("${s3.access-key:}") private String accessKey;
    @Value("${s3.secret-key:}") private String secretKey;
    @Value("${openai.api-key:}") private String openAiKey;
    @Value("${openai.image-model:gpt-image-1.5}") private String imageModel;
    private final RestClient rest = RestClient.create("https://api.openai.com/v1");
    private final SecureRandom random = new SecureRandom();

    public record MediaUpload(String key, String publicUrl, String contentType, long size, String status) {}

    public MediaUpload upload(MultipartFile file, String folder) throws Exception {
        String safeFolder = blank(folder) ? "events" : folder.replaceAll("[^a-zA-Z0-9/_-]", "");
        String name = Optional.ofNullable(file.getOriginalFilename()).orElse("media.bin").replaceAll("[^a-zA-Z0-9._-]", "-");
        return uploadBytes(file.getBytes(), file.getContentType(), safeFolder + "/" + Instant.now().toEpochMilli() + "-" + Math.abs(random.nextInt()) + "-" + name);
    }

    public MediaUpload generateEventCover(String title, String description) {
        if (blank(openAiKey)) return null;
        try {
            String prompt = "Affiche premium moderne pour un événement LightEvents. Titre: " + safe(title) + ". Description: " + safe(description) + ". Style: élégant, professionnel, lumineux, sans texte illisible.";
            Map<String,Object> req = Map.of("model", imageModel, "prompt", prompt, "size", "1024x1024");
            Map res = rest.post().uri("/images/generations").headers(h -> h.setBearerAuth(openAiKey)).body(req).retrieve().body(Map.class);
            List data = (List) res.get("data"); Map first = (Map) data.get(0); String b64 = String.valueOf(first.get("b64_json"));
            byte[] bytes = Base64.getDecoder().decode(b64);
            return uploadBytes(bytes, "image/png", "events/generated/" + Instant.now().toEpochMilli() + "-cover.png");
        } catch (Exception e) { throw new IllegalStateException("OpenAI image generation failed: " + e.getMessage(), e); }
    }

    private MediaUpload uploadBytes(byte[] bytes, String contentType, String key) throws Exception {
        if (blank(bucket) || blank(accessKey) || blank(secretKey)) throw new IllegalStateException("S3 is not configured. Set S3_BUCKET, S3_ACCESS_KEY, S3_SECRET_KEY, S3_REGION and optionally S3_ENDPOINT/S3_PUBLIC_BASE_URL.");
        S3Client s3 = s3();
        String ct = blank(contentType) ? "application/octet-stream" : contentType;
        PutObjectRequest put = PutObjectRequest.builder().bucket(bucket).key(key).contentType(ct).build();
        s3.putObject(put, RequestBody.fromBytes(bytes));
        return new MediaUpload(key, publicUrl(key), ct, bytes.length, "UPLOADED");
    }

    private S3Client s3() {
        S3ClientBuilder b = S3Client.builder().region(Region.of(region)).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));
        if (!blank(endpoint)) b.endpointOverride(URI.create(endpoint)).forcePathStyle(true);
        return b.build();
    }
    private String publicUrl(String key) { if (!blank(publicBaseUrl)) return publicBaseUrl.replaceAll("/$", "") + "/" + key; return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key; }
    private static boolean blank(String v){ return v==null || v.isBlank(); }
    private static String safe(String v){ return v==null ? "" : v; }
}

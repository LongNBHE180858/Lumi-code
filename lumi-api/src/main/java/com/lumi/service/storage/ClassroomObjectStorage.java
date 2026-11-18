package com.lumi.service.storage;

import com.lumi.config.ClassroomStorageProperties;
import jakarta.annotation.PreDestroy;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

/** Private classroom files, isolated under classrooms/{classId}/ in R2. */
@Component
public class ClassroomObjectStorage {

    public record Uploaded(String url, String fileName, String mimeType, long size) {}

    public record Access(String url, Instant expiresAt) {}

    private final ClassroomStorageProperties properties;
    private S3Client client;
    private S3Presigner presigner;

    public ClassroomObjectStorage(ClassroomStorageProperties properties) {
        this.properties = properties;
    }

    private synchronized void ready() {
        if (client != null) return;
        if (
            !properties.isEnabled() ||
            blank(properties.getEndpoint()) ||
            blank(properties.getBucket()) ||
            blank(properties.getAccessKey()) ||
            blank(properties.getSecretKey())
        ) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Classroom cloud storage is not configured");
        }
        URI endpoint = URI.create(properties.getEndpoint());
        if (
            !"https".equals(endpoint.getScheme()) ||
            endpoint.getHost() == null ||
            endpoint.getUserInfo() != null ||
            endpoint.getRawQuery() != null ||
            (endpoint.getPath() != null && !endpoint.getPath().isEmpty() && !endpoint.getPath().equals("/"))
        ) {
            throw new IllegalStateException("Classroom storage endpoint must be an HTTPS origin");
        }
        if (
            !properties.getBucket().matches("[a-z0-9][a-z0-9-]*") ||
            properties.getSignedUrlSeconds() < 1 ||
            properties.getSignedUrlSeconds() > 604800
        ) {
            throw new IllegalStateException("Invalid classroom storage configuration");
        }
        var credentials = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())
        );
        var config = S3Configuration.builder().pathStyleAccessEnabled(true).chunkedEncodingEnabled(false).build();
        client = S3Client.builder()
            .endpointOverride(endpoint)
            .region(Region.of("auto"))
            .credentialsProvider(credentials)
            .serviceConfiguration(config)
            .build();
        presigner = S3Presigner.builder()
            .endpointOverride(endpoint)
            .region(Region.of("auto"))
            .credentialsProvider(credentials)
            .serviceConfiguration(config)
            .build();
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private String baseUrl() {
        return properties.getEndpoint().replaceAll("/+$", "") + "/" + properties.getBucket() + "/";
    }

    private String key(Long classId, String url) {
        String prefix = "classrooms/" + classId + "/";
        if (url == null || !url.startsWith(baseUrl() + prefix)) throw new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Classroom file not found"
        );
        String key = url.substring(baseUrl().length());
        if (!key.matches("classrooms/[0-9]+/(?:document|video|image)/[a-f0-9-]+\\.(?:pdf|docx|mp4|png|jpg)")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Classroom file not found");
        }
        return key;
    }

    public Uploaded upload(Long classId, CourseFileValidator.Kind kind, Path file, String name, String mimeType) {
        if (
            kind != CourseFileValidator.Kind.DOCUMENT &&
            kind != CourseFileValidator.Kind.VIDEO &&
            kind != CourseFileValidator.Kind.THUMBNAIL
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported classroom file type");
        }
        ready();
        String ext = switch (mimeType) {
            case "application/pdf" -> "pdf";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx";
            case "video/mp4" -> "mp4";
            case "image/png" -> "png";
            case "image/jpeg" -> "jpg";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported classroom file type");
        };
        String folder = kind == CourseFileValidator.Kind.THUMBNAIL ? "image" : kind.name().toLowerCase(Locale.ROOT);
        String key = "classrooms/" + classId + "/" + folder + "/" + UUID.randomUUID() + "." + ext;
        boolean uploaded = false;
        try {
            long size = Files.size(file);
            client.putObject(
                PutObjectRequest.builder().bucket(properties.getBucket()).key(key).contentType(mimeType).build(),
                RequestBody.fromFile(file)
            );
            uploaded = true;
            var metadata = client.headObject(HeadObjectRequest.builder().bucket(properties.getBucket()).key(key).build());
            if (metadata.contentLength() != size || !mimeType.equals(metadata.contentType())) throw new IllegalStateException(
                "Cloud metadata mismatch"
            );
            return new Uploaded(baseUrl() + key, name, mimeType, size);
        } catch (Exception failure) {
            if (uploaded) try {
                client.deleteObject(DeleteObjectRequest.builder().bucket(properties.getBucket()).key(key).build());
            } catch (RuntimeException ignored) {}
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Classroom upload failed");
        }
    }

    public Access access(Long classId, String url, String fileName) {
        ready();
        String key = key(classId, url);
        try {
            var request = GetObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .responseContentDisposition(
                    ContentDisposition.inline()
                        .filename(fileName == null ? "material" : fileName, java.nio.charset.StandardCharsets.UTF_8)
                        .build()
                        .toString()
                )
                .build();
            var signed = presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(properties.getSignedUrlSeconds()))
                    .getObjectRequest(request)
                    .build()
            );
            return new Access(signed.url().toString(), signed.expiration());
        } catch (RuntimeException failure) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot issue classroom file URL");
        }
    }

    public void delete(Long classId, String url) {
        ready();
        client.deleteObject(DeleteObjectRequest.builder().bucket(properties.getBucket()).key(key(classId, url)).build());
    }

    @PreDestroy
    public synchronized void close() {
        if (client != null) client.close();
        if (presigner != null) presigner.close();
    }
}

package com.lumi.service.storage;

import com.lumi.config.CourseStorageProperties;
import com.lumi.service.dto.TicketMessageContent;
import jakarta.annotation.PreDestroy;
import java.net.URI;
import java.nio.file.*;
import java.time.*;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

/** Private R2 bucket through S3 API; URLs stored in DB never contain credentials or signatures. */
@Component
public class R2CourseObjectStorage implements CourseObjectStorage {

    private static final Logger LOG = LoggerFactory.getLogger(R2CourseObjectStorage.class);

    private final CourseStorageProperties properties;
    private S3Client client;
    private S3Presigner presigner;

    public R2CourseObjectStorage(CourseStorageProperties properties) {
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
            LOG.warn(
                "R2 configuration incomplete: enabled={}, endpointPresent={}, bucketPresent={}, accessKeyPresent={}, secretKeyPresent={}, workingDirectory={}",
                properties.isEnabled(),
                !blank(properties.getEndpoint()),
                !blank(properties.getBucket()),
                !blank(properties.getAccessKey()),
                !blank(properties.getSecretKey()),
                System.getProperty("user.dir")
            );
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Course cloud storage is not configured");
        }
        URI endpoint = URI.create(properties.getEndpoint());
        if (
            !"https".equals(endpoint.getScheme()) ||
            endpoint.getHost() == null ||
            endpoint.getRawQuery() != null ||
            endpoint.getUserInfo() != null ||
            (endpoint.getPath() != null && !endpoint.getPath().isEmpty() && !endpoint.getPath().equals("/"))
        ) throw new IllegalStateException("Storage endpoint must be an HTTPS origin");
        if (
            !properties.getBucket().matches("[a-z0-9][a-z0-9-]*") ||
            properties.getSignedUrlSeconds() < 1 ||
            properties.getSignedUrlSeconds() > 604800
        ) throw new IllegalStateException("Invalid course storage configuration");
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

    private String key(String url) {
        if (url == null || !url.startsWith(baseUrl())) throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "File is not managed by the configured cloud storage"
        );
        String key = url.substring(baseUrl().length());
        if (!key.matches("courses/[0-9]+/[a-z]+/[a-f0-9-]+\\.[a-z0-9]+") && !key.matches("tickets/[0-9]+/[a-f0-9-]+\\.(png|jpg|jpeg)")) throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Invalid stored file URL"
        );
        return key;
    }

    @Override
    public Uploaded upload(Long courseId, CourseFileValidator.Kind kind, Path file, String name, String mimeType) {
        ready();
        String extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase(java.util.Locale.ROOT);
        String key =
            "courses/" + courseId + "/" + kind.name().toLowerCase(java.util.Locale.ROOT) + "/" + UUID.randomUUID() + "." + extension;
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
            if (uploaded) {
                try {
                    client.deleteObject(DeleteObjectRequest.builder().bucket(properties.getBucket()).key(key).build());
                } catch (RuntimeException ignored) {
                    /* Private orphan; retain for operational cleanup. */
                }
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cloud upload failed; existing file was retained");
        }
    }

    public TicketMessageContent.Image uploadTicketImage(Long ticketId, Path file, String name, String mimeType) {
        ready();
        String extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase(java.util.Locale.ROOT);
        String key = "tickets/" + ticketId + "/" + UUID.randomUUID() + "." + extension;
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
            return new TicketMessageContent.Image(key, name);
        } catch (Exception failure) {
            if (uploaded) {
                try {
                    client.deleteObject(DeleteObjectRequest.builder().bucket(properties.getBucket()).key(key).build());
                } catch (RuntimeException ignored) {}
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Ticket image upload failed");
        }
    }

    public Access ticketImageAccess(Long ticketId, String key, String name) {
        if (!key.matches("tickets/" + ticketId + "/[a-f0-9-]+\\.(png|jpg|jpeg)")) throw new ResponseStatusException(
            HttpStatus.NOT_FOUND
        );
        return access(baseUrl() + key, name, false);
    }

    public void deleteTicketImage(String key) {
        if (!key.matches("tickets/[0-9]+/[a-f0-9-]+\\.(png|jpg|jpeg)")) return;
        delete(baseUrl() + key);
    }

    @Override
    public Access access(String url, String fileName, boolean download) {
        ready();
        String key = key(url);
        String disposition = (download ? ContentDisposition.attachment() : ContentDisposition.inline())
            .filename(fileName == null ? "media" : fileName, java.nio.charset.StandardCharsets.UTF_8)
            .build()
            .toString();
        try {
            var request = GetObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .responseContentDisposition(disposition)
                .build();
            var signed = presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(properties.getSignedUrlSeconds()))
                    .getObjectRequest(request)
                    .build()
            );
            return new Access(signed.url().toString(), signed.expiration());
        } catch (RuntimeException failure) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot issue file access URL");
        }
    }

    @Override
    public void delete(String url) {
        ready();
        client.deleteObject(DeleteObjectRequest.builder().bucket(properties.getBucket()).key(key(url)).build());
    }

    @PreDestroy
    public synchronized void close() {
        if (client != null) client.close();
        if (presigner != null) presigner.close();
    }
}

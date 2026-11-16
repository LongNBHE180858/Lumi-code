package com.lumi.service.payment.impl;

import com.lumi.config.SePayProperties;
import com.lumi.service.dto.SePayWebhookRequestDTO;
import com.lumi.service.payment.SePayWebhookService;
import com.lumi.service.payment.SePayWebhookProcessor;
import jakarta.validation.Validator;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SePayWebhookServiceImpl implements SePayWebhookService {

    static final long SIGNATURE_MAX_AGE_SECONDS = 300; // Theo hướng dẫn HMAC SePay.
    static final ZoneId VIETNAM = ZoneId.of("Asia/Ho_Chi_Minh");
    static final DateTimeFormatter TRANSACTION_DATE = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);
    SePayProperties properties;
    ObjectMapper objectMapper;
    Validator validator;
    SePayWebhookProcessor processor;

    @Override
    public void handle(byte[] rawBody, String signature, String timestamp) {
        Instant receivedAt = Instant.now();
        validateConfiguration();
        verifySignature(rawBody, signature, timestamp, receivedAt);

        SePayWebhookRequestDTO request = parseAndValidate(rawBody);
        Instant transactionAt = parseTransactionTime(request.getTransactionDate());

        // Bean riêng để transaction chỉ bắt đầu sau khi request đã được xác thực.
        processor.process(request, transactionAt, receivedAt);
    }

    private void validateConfiguration() {
        if (!properties.isWebhookEnabled() || !StringUtils.hasText(properties.getWebhookSecret())
            || !StringUtils.hasText(properties.getAccountNumber()) || !StringUtils.hasText(properties.getBankCode())) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Webhook SePay chưa được cấu hình đầy đủ.");
        }
    }

    private void verifySignature(byte[] rawBody, String signature, String timestamp, Instant receivedAt) {
        if (timestamp == null || !timestamp.matches("[0-9]{1,19}")
            || signature == null || !signature.matches("sha256=[0-9a-fA-F]{64}")) {
            throw unauthorized();
        }
        long signedAt;
        try { signedAt = Long.parseLong(timestamp); }
        catch (NumberFormatException exception) { throw unauthorized(); }
        long now = receivedAt.getEpochSecond();
        if (signedAt < now - SIGNATURE_MAX_AGE_SECONDS || signedAt > now + SIGNATURE_MAX_AGE_SECONDS) {
            throw unauthorized();
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(properties.getWebhookSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            mac.update((timestamp + ".").getBytes(StandardCharsets.UTF_8));
            byte[] expected = mac.doFinal(rawBody);
            byte[] provided = HexFormat.of().parseHex(signature.substring("sha256=".length()));
            if (!MessageDigest.isEqual(expected, provided)) throw unauthorized();
        } catch (java.security.GeneralSecurityException exception) {
            throw new IllegalStateException("Không khởi tạo được HMAC-SHA256.", exception);
        }
    }

    private SePayWebhookRequestDTO parseAndValidate(byte[] rawBody) {
        try {
            var json = objectMapper.readTree(rawBody);
            if (json == null || !json.isObject() || !json.has("id") || !json.get("id").isIntegralNumber()
                || !json.get("id").canConvertToLong() || !json.has("transferAmount") || !json.get("transferAmount").isNumber()) {
                throw invalidPayload();
            }
            SePayWebhookRequestDTO request = objectMapper.treeToValue(json, SePayWebhookRequestDTO.class);
            if (!validator.validate(request).isEmpty()) throw invalidPayload();
            return request;
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw invalidPayload(); // Không log raw body, secret hoặc thông tin người chuyển.
        }
    }

    private Instant parseTransactionTime(String value) {
        try { return LocalDateTime.parse(value, TRANSACTION_DATE).atZone(VIETNAM).toInstant(); }
        catch (java.time.DateTimeException exception) { throw invalidPayload(); }
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chữ ký hoặc timestamp SePay không hợp lệ.");
    }

    private ResponseStatusException invalidPayload() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu webhook SePay không hợp lệ.");
    }
}

package com.lumi.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OtpDigest {

    private final byte[] secret;

    public OtpDigest(@Value("${lumi.auth.otp-secret:${jhipster.security.authentication.jwt.base64-secret}}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("lumi.auth.otp-secret phải có ít nhất 32 ký tự");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String hash(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(("lumi-otp:" + value).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể băm OTP", ex);
        }
    }

    public boolean matches(String input, String expectedHash) {
        if (expectedHash == null) return false;

        return MessageDigest.isEqual(hash(input).getBytes(StandardCharsets.US_ASCII), expectedHash.getBytes(StandardCharsets.US_ASCII));
    }
}

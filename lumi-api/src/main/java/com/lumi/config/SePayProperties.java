package com.lumi.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lumi.payment.sepay")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SePayProperties {
    boolean enabled;
    String bankCode;
    String bankName;
    String accountNumber;
    String accountName;
    boolean webhookEnabled;
    String webhookSecret;
}

package com.lumi.service.payment.impl;

import com.lumi.config.SePayProperties;
import com.lumi.service.dto.PaymentInfoDTO;
import com.lumi.service.payment.SePayQrService;
import java.math.BigDecimal;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

/** Chỉ dựng URL ảnh; không gọi SePay hoặc xác nhận giao dịch ngân hàng. */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SePayQrServiceImpl implements SePayQrService {

    static final String QR_IMAGE_ENDPOINT = "https://vietqr.app/img";
    SePayProperties properties;

    @Override
    public PaymentInfoDTO buildPaymentInfo(Long orderId, BigDecimal amount) {
        validateConfiguration();
        validateOrderAmount(orderId, amount);

        String transferContent = "LUMI" + orderId;
        String qrImageUrl = buildQrImageUrl(amount, transferContent);

        return PaymentInfoDTO.builder()
            .bankCode(properties.getBankCode().trim())
            .bankName(properties.getBankName().trim())
            .accountNumber(properties.getAccountNumber().trim())
            .accountName(properties.getAccountName().trim())
            .amount(amount)
            .currency("VND")
            .transferContent(transferContent)
            .qrImageUrl(qrImageUrl)
            .build();
    }

    private void validateConfiguration() {
        if (!properties.isEnabled()
            || !StringUtils.hasText(properties.getBankCode())
            || !StringUtils.hasText(properties.getBankName())
            || !StringUtils.hasText(properties.getAccountNumber())
            || !StringUtils.hasText(properties.getAccountName())) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Thanh toán QR chưa được cấu hình đầy đủ.");
        }
    }

    private void validateOrderAmount(Long orderId, BigDecimal amount) {
        if (orderId == null || orderId <= 0 || amount == null || amount.signum() <= 0 || amount.stripTrailingZeros().scale() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đơn hàng chưa có số tiền thanh toán hợp lệ.");
        }
    }

    private String buildQrImageUrl(BigDecimal amount, String transferContent) {
        return UriComponentsBuilder.fromUriString(QR_IMAGE_ENDPOINT)
            .queryParam("acc", "{account}")
            .queryParam("bank", "{bank}")
            .queryParam("amount", "{amount}")
            .queryParam("des", "{content}")
            .queryParam("holder", "{holder}")
            .queryParam("template", "compact")
            .encode()
            .buildAndExpand(Map.of(
                "account", properties.getAccountNumber().trim(),
                "bank", properties.getBankCode().trim(),
                "amount", amount.toBigIntegerExact().toString(),
                "content", transferContent,
                "holder", properties.getAccountName().trim()
            ))
            .toUriString();
    }
}

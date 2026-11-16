package com.lumi.service.payment;

import com.lumi.service.dto.PaymentInfoDTO;
import java.math.BigDecimal;

public interface SePayQrService {

    PaymentInfoDTO buildPaymentInfo(Long orderId, BigDecimal amount);
}

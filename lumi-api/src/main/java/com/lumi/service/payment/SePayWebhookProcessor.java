package com.lumi.service.payment;

import com.lumi.service.dto.SePayWebhookRequestDTO;
import java.time.Instant;

public interface SePayWebhookProcessor {

    void process(SePayWebhookRequestDTO request, Instant transactionAt, Instant receivedAt);
}

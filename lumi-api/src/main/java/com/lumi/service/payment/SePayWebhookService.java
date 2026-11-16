package com.lumi.service.payment;

public interface SePayWebhookService {

    void handle(byte[] rawBody, String signature, String timestamp);
}

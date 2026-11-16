package com.lumi.web.rest;

import com.lumi.service.payment.SePayWebhookService;
import com.lumi.service.dto.SePayWebhookResponseDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/payments/sepay")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SePayWebhookController {

    SePayWebhookService service;

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SePayWebhookResponseDTO> receive(
        @RequestBody byte[] rawBody,
        @RequestHeader(value = "X-SePay-Signature", required = false) String signature,
        @RequestHeader(value = "X-SePay-Timestamp", required = false) String timestamp
    ) {
        service.handle(rawBody, signature, timestamp);
        // Contract của SePay: {"success":true}, không bọc APIResponse.
        return ResponseEntity.ok(SePayWebhookResponseDTO.builder().success(true).build());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<SePayWebhookResponseDTO> handleWebhookError(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
            .body(SePayWebhookResponseDTO.builder().success(false).message(exception.getReason()).build());
    }
}

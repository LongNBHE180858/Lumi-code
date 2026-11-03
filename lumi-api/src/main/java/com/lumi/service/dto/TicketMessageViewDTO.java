package com.lumi.service.dto;

import java.time.Instant;

public record TicketMessageViewDTO(Long id, String content, Instant createdAt, Long senderId, String senderLogin) {}

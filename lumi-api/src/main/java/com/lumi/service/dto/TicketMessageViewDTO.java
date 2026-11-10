package com.lumi.service.dto;

import java.time.Instant;
import java.util.List;

public record TicketMessageViewDTO(Long id, String content, List<TicketImageDTO> images, Instant createdAt, Long senderId, String senderLogin) {}

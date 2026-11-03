package com.lumi.service.dto;

import java.time.Instant;

public record SupportTicketViewDTO(Long id, String type, String subject, String status, Instant createdAt, Long userId, String userLogin) {}

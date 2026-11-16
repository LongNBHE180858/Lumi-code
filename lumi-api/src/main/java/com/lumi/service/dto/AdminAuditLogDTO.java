package com.lumi.service.dto;

import java.time.Instant;

public record AdminAuditLogDTO(
    Long id,
    String actorLogin,
    String action,
    String targetEntity,
    String targetId,
    String ipAddress,
    String details,
    Instant occurredAt
) {}

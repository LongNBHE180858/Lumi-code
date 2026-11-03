package com.lumi.service.dto;

import com.lumi.domain.enumeration.SupportTicketStatus;
import jakarta.validation.constraints.NotNull;

public record SupportTicketStatusRequest(@NotNull SupportTicketStatus status) {}

package com.lumi.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupportTicketCreateRequest(
    @NotBlank @Size(max = 50) String type,
    @NotBlank @Size(max = 255) String subject,
    @NotBlank @Size(max = 5000) String content
) {}

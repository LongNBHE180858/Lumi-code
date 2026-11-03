package com.lumi.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TicketMessageCreateRequest(@NotBlank @Size(max = 5000) String content) {}

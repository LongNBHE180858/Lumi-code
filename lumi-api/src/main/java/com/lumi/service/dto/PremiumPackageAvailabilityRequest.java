package com.lumi.service.dto;

import jakarta.validation.constraints.NotNull;

public record PremiumPackageAvailabilityRequest(@NotNull Boolean available) {}

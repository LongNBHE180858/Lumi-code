package com.lumi.service.dto;

import jakarta.validation.constraints.NotNull;

public record UserActivationRequest(@NotNull Boolean activated) {}

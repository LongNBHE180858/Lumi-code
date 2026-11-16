package com.lumi.service.dto.ai;
import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
    @NotBlank(message = "Câu hỏi không được để trống.")
    String question
) {}

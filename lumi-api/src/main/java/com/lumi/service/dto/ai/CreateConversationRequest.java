package com.lumi.service.dto.ai;

import jakarta.validation.constraints.Positive;

public record CreateConversationRequest(@Positive Long lessonId) {
}

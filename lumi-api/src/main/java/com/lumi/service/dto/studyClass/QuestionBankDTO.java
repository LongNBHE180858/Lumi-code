package com.lumi.service.dto.studyClass;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;

public final class QuestionBankDTO {

    private QuestionBankDTO() {}

    public record BankSummary(Long id, String name, Long classId, String className, long questionCount, Instant updatedAt) {}

    public record CreateBank(@NotBlank @Size(max = 255) String name, Long classId) {}

    public record OptionItem(Long id, String content, boolean isCorrect, int orderIndex) {}

    public record QuestionItem(Long id, String content, Integer difficulty, String topic, String explanation, List<OptionItem> options) {}

    public record CreateQuestion(
        @NotBlank String content,
        @Min(1) @Max(5) Integer difficulty,
        @Size(max = 255) String topic,
        String explanation,
        @NotEmpty @Size(min = 2, max = 10) List<@NotBlank String> options,
        @NotNull @Min(0) Integer correctIndex
    ) {}

    public record TopicSummary(
        String topic,
        long totalQuestions,
        long difficulty1,
        long difficulty2,
        long difficulty3,
        long difficulty4,
        long difficulty5
    ) {}

    public record ImportError(int row, String message) {}

    public record ImportResult(int importedCount, List<ImportError> errors) {}
}

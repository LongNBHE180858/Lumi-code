package com.lumi.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class InstructorExamDTOs {

    private InstructorExamDTOs() {}

    public record OptionInput(Long id, @NotBlank @Size(max = 2000) String content, @NotNull Boolean isCorrect, Integer orderIndex) {}

    public record OptionDTO(Long id, String content, Boolean isCorrect, Integer orderIndex) {}

    public record QuestionInput(
        Long id,
        @NotBlank String content,
        String type,
        Object difficulty,
        String explanation,
        @DecimalMin("0.0") BigDecimal score,
        Integer orderIndex,
        @NotEmpty List<@Valid OptionInput> options
    ) {
        public Integer getNumericDifficulty() {
            if (difficulty == null) return 3;
            if (difficulty instanceof Number n) {
                int val = n.intValue();
                return val >= 1 && val <= 5 ? val : 3;
            }
            String s = difficulty.toString().trim().toUpperCase();
            return switch (s) {
                case "EASY" -> 1;
                case "HARD" -> 5;
                case "1" -> 1;
                case "2" -> 2;
                case "3" -> 3;
                case "4" -> 4;
                case "5" -> 5;
                default -> 3;
            };
        }
    }

    public record QuestionDetailDTO(
        Long id,
        String content,
        String type,
        Integer difficulty,
        String explanation,
        BigDecimal score,
        Integer orderIndex,
        List<OptionDTO> options
    ) {}

    public record CreateExamInput(
        @NotBlank @Size(max = 255) String title,
        @Min(0) Integer durationMinutes,
        @DecimalMin("0.0") BigDecimal passScore,
        Integer maxAttempts,
        Boolean shuffleQuestions,
        BigDecimal videoTriggerSeconds
    ) {}

    public record UpdateExamInput(
        @NotBlank @Size(max = 255) String title,
        @Min(0) Integer durationMinutes,
        @DecimalMin("0.0") BigDecimal passScore,
        Integer maxAttempts,
        Boolean shuffleQuestions,
        BigDecimal videoTriggerSeconds
    ) {}

    public record ExamSummaryDTO(
        Long id,
        String title,
        String assessmentType,
        Integer durationMinutes,
        BigDecimal passScore,
        Integer maxAttempts,
        Boolean shuffleQuestions,
        BigDecimal videoTriggerSeconds,
        Long courseId,
        Long chapterId,
        Long lessonId,
        int totalQuestions,
        BigDecimal totalScore,
        Instant createdAt
    ) {}

    public record ExamDetailDTO(
        Long id,
        String title,
        String assessmentType,
        Integer durationMinutes,
        BigDecimal passScore,
        Integer maxAttempts,
        Boolean shuffleQuestions,
        BigDecimal videoTriggerSeconds,
        Long courseId,
        Long chapterId,
        Long lessonId,
        List<QuestionDetailDTO> questions,
        Instant createdAt
    ) {}
}

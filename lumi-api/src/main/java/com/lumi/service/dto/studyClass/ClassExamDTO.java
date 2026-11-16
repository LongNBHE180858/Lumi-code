package com.lumi.service.dto.studyClass;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class ClassExamDTO {

    private ClassExamDTO() {}

    public record OptionInput(@NotBlank String content) {}

    public record QuestionInput(
        @NotBlank String content,
        String explanation,
        @NotNull @DecimalMin("0.01") BigDecimal points,
        @NotEmpty @Size(min = 2, max = 6) List<@Valid OptionInput> options,
        @NotNull @Min(0) Integer correctIndex
    ) {}

    public record SaveExam(
        @NotNull Long materialId,
        @NotBlank @Size(max = 255) String title,
        @NotNull @Min(1) @Max(180) Integer durationMinutes,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal passScore,
        Long assignedStudentId,
        @NotEmpty @Size(max = 30) List<@Valid QuestionInput> questions
    ) {}

    public record UpdateExam(
        @NotBlank @Size(max = 255) String title,
        @NotNull @Min(1) @Max(180) Integer durationMinutes,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal passScore,
        Long assignedStudentId,
        @NotBlank String status,
        List<@Valid QuestionInput> questions
    ) {}

    public record Editor(Summary summary, List<QuestionInput> questions) {}

    public record Summary(
        Long id,
        Long materialId,
        String title,
        int durationMinutes,
        BigDecimal passScore,
        String status,
        int questionCount,
        Long assignedStudentId
    ) {}

    public record MatrixRow(String topic, Integer difficulty, @NotNull @Min(1) Integer count, BigDecimal points) {}

    public record GeneratePreviewRequest(@NotNull Long bankId, @NotEmpty List<@Valid MatrixRow> matrix) {}

    public record PickedQuestion(
        Long questionId,
        String content,
        Integer difficulty,
        String topic,
        BigDecimal points,
        List<String> options,
        int correctIndex,
        String explanation
    ) {}

    public record CreateFromMatrixRequest(
        @NotBlank @Size(max = 255) String title,
        @NotNull @Min(1) @Max(180) Integer durationMinutes,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal passScore,
        Long assignedStudentId,
        Long materialId,
        @NotNull Long bankId,
        @NotEmpty List<Long> questionIds,
        BigDecimal pointsPerQuestion
    ) {}

    public record Option(Long id, String content) {}

    public record QuestionView(Long id, String content, BigDecimal points, List<Option> options) {}

    public record AttemptView(Long attemptId, Long examId, Instant deadlineAt, List<QuestionView> questions) {}

    public record AnswerInput(@NotNull Long questionId, Long optionId) {}

    public record Submit(@NotNull List<@Valid AnswerInput> answers) {}

    public record Feedback(Long questionId, String question, String selected, String correct, boolean isCorrect, String explanation) {}

    public record Result(Long attemptId, BigDecimal score, boolean passed, Instant submittedAt, List<Feedback> feedback) {}

    public record Mistake(Long examId, String examTitle, String question, String selected, String correct, String explanation) {}

    public record StudentProgress(
        Long studentId,
        String name,
        int completedExams,
        int totalExams,
        BigDecimal averageScore,
        int mistakes,
        List<Mistake> details
    ) {}

    public record Progress(List<StudentProgress> students, BigDecimal classAverageScore, int totalExams) {}
}

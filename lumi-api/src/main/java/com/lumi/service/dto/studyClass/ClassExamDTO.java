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
        List<@Valid QuestionInput> questions,

        @NotBlank @Size(max = 255) String assessmentType,
        Integer maxAttempt,
        @NotBlank String resultReleaseMode,
        @NotBlank String answerReleaseMode,
        Boolean shuffleQuestions,
        @NotNull Instant openAt,
        @NotNull Instant closesAt
    ) {}

    public record UpdateStatus(@NotBlank String status) {}

    public record Editor(Summary summary, List<QuestionInput> questions) {}

    public record Summary(
        Long id,
        Long materialId,
        String title,
        Integer durationMinutes,
        BigDecimal passScore,
        String status,
        int questionCount,
        Long assignedStudentId,

        String assessmentType,
        Integer maxAttempts,
        String resultReleaseMode,
        String answerReleaseMode,
        Instant opensAt,
        Instant closesAt,
        Boolean shuffleQuestions
    ) {}

    // DTO danh sách đề dành riêng cho học sinh
    public record StudentExamSummary(
        Long id,
        Long materialId,
        String title,
        Integer durationMinutes,
        BigDecimal passScore,
        int questionCount,
        String assessmentType,
        Integer maxAttempts,
        int usedAttempts,
        Integer remainingAttempts,
        Instant opensAt,
        Instant closesAt,
        String status, // "AVAILABLE", "UPCOMING", "CLOSED", "COMPLETED"
        Long inProgressAttemptId
    ) {}

    // DTO xem lượt làm bài (bổ sung savedAnswers và remainingAttempts)
    public record AttemptView(
        Long attemptId,
        Long examId,
        Instant deadlineAt,
        List<QuestionView> questions,
        List<AnswerInput> savedAnswers,
        Integer remainingAttempts
    ) {}

    public record SaveDraftResult(int savedCount, Instant lastSavedAt) {}

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
        BigDecimal pointsPerQuestion,

        @NotBlank @Size(max = 255) String assessmentType,
        Integer maxAttempt,
        @NotBlank String resultReleaseMode,
        @NotBlank String answerReleaseMode,
        Boolean shuffleQuestions,
        @NotNull Instant openAt,
        @NotNull Instant closesAt
    ) {}

    public record Option(Long id, String content) {}

    public record QuestionView(Long id, String content, BigDecimal points, List<Option> options) {}

    public record AnswerInput(@NotNull Long questionId, Long optionId) {}

    public record Submit(@NotNull List<@Valid AnswerInput> answers) {}

    public record SubmitExam(@Size(max = 100) List<@Valid AnswerInput> answers) {}

    public record Feedback(
        Long questionId,
        String question,
        String selected,
        String correct,
        boolean isCorrect,
        String explanation,
        List<String> options
    ) {}

    public record Result(
        Long attemptId,
        String status,
        BigDecimal score,
        Boolean passed,
        Instant submittedAt,
        Instant resultAvailableAt,
        Instant answersAvailableAt,
        List<Feedback> feedback
    ) {}

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

    public record AssessmentRulesConfig(
        String type,
        int defaultMaxAttempts,
        Integer maxAllowedAttempts,
        boolean allowUnlimitedAttempts,
        int defaultDurationMinutes,
        boolean requireDuration,
        int defaultPassScore,
        String defaultResultReleaseMode,
        String defaultAnswerReleaseMode,
        List<String> availableResultModes,
        List<String> availableAnswerModes
    ) {}
}

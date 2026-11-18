package com.lumi.service.dto.studyClass;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class ClassAssignmentDTO {

    private ClassAssignmentDTO() {}

    public record Submit(@NotBlank @Size(max = 10000) String content) {}

    public record Attachment(String fileName, String fileType, long sizeBytes) {}

    public record Grade(
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal score,
        @Size(max = 2000) String feedback,
        @NotNull Long version
    ) {}

    public record Submission(
        Long id,
        Long assignmentId,
        Long studentId,
        String studentName,
        String content,
        String status,
        BigDecimal score,
        String feedback,
        Instant submittedAt,
        Instant gradedAt,
        Long version,
        List<Attachment> attachments
    ) {}

    public record SubmissionList(List<Submission> items, long assignedCount) {}
}

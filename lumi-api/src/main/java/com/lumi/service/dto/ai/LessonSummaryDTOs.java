package com.lumi.service.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class LessonSummaryDTOs {

    private LessonSummaryDTOs() {}

    public record Request(
        @Size(max = 255) String lessonTitle,
        @NotBlank(message = "Nội dung bài học không được để trống") String content
    ) {}

    public record Response(String summary, CurriculumClassification classification) {
        public Response(String summary) {
            this(summary, null);
        }
    }

    public record ExtractionPreview(
        String fileName,
        String fileType,
        int pageCount,
        boolean ocrUsed,
        int extractedLength,
        String extractedContent
    ) {}

    public record CurriculumClassification(boolean isValid, String subject, Integer grade, String topic, String reason) {}
}

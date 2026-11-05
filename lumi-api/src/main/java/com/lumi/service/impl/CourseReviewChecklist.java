package com.lumi.service.impl;

import com.lumi.domain.CourseReviewRequest;
import com.lumi.domain.enumeration.*;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.InstructorCourseDTOs.*;
import java.math.BigDecimal;
import java.util.List;

/** One set of submission rules for builder responses and the atomic submit operation. */
final class CourseReviewChecklist {

    private CourseReviewChecklist() {}

    static ReviewState evaluate(CourseDTO course, List<ChapterNode> chapters, CourseReviewRequest request) {
        List<LessonNode> lessons = chapters
            .stream()
            .flatMap(node -> node.lessons().stream())
            .toList();
        List<ReviewCheck> checks = List.of(
            check(
                "identity",
                "basic-info",
                "Tên khóa học, môn học và lớp",
                true,
                text(course.getTitle()) &&
                    !"Khóa học chưa đặt tên".equals(course.getTitle().trim()) &&
                    course.getSubject() != null &&
                    course.getGrade() != null
            ),
            check(
                "description",
                "basic-info",
                "Mô tả ngắn và mô tả đầy đủ",
                true,
                text(course.getShortDescription()) && text(course.getDescription())
            ),
            check(
                "audience",
                "basic-info",
                "Đối tượng và mục tiêu học tập",
                true,
                text(course.getTargetAudience()) &&
                    course.getLearningObjectives() != null &&
                    !course.getLearningObjectives().isEmpty() &&
                    course.getLearningObjectives().stream().allMatch(CourseReviewChecklist::text)
            ),
            check("thumbnail", "basic-info", "Ảnh bìa khóa học", true, text(course.getThumbnailUrl())),
            check(
                "curriculum",
                "curriculum",
                "Chương và bài học đã có nội dung",
                true,
                !chapters.isEmpty() &&
                    chapters.stream().allMatch(node -> text(node.chapter().getTitle()) && !node.lessons().isEmpty()) &&
                    !lessons.isEmpty() &&
                    lessons.stream().allMatch(CourseReviewChecklist::lessonReady)
            ),
            check("pricing", "pricing", "Học phí và thời hạn truy cập", true, pricingReady(course)),
            check("intro", "basic-info", "Video giới thiệu", false, text(course.getIntroVideoUrl())),
            check(
                "preview",
                "curriculum",
                "Bài học bật xem thử",
                false,
                lessons.stream().anyMatch(node -> Boolean.TRUE.equals(node.lesson().getIsPreview()))
            ),
            check("prerequisites", "basic-info", "Điều kiện học", false, text(course.getPrerequisites()))
        );
        boolean pending = request != null && request.getStatus() == CourseReviewRequestStatus.PENDING;
        boolean canSubmit =
            course.getStatus() == RecordStatus.DRAFT &&
            !pending &&
            checks.stream().filter(ReviewCheck::required).allMatch(ReviewCheck::complete);
        ReviewSummary summary =
            request == null
                ? null
                : new ReviewSummary(
                      request.getId(),
                      request.getStatus() == null ? null : request.getStatus().name(),
                      request.getCourseVersion(),
                      request.getSubmissionNote(),
                      request.getRightsConfirmedAt(),
                      request.getSubmittedAt(),
                      request.getReviewedAt(),
                      request.getReviewerFeedback()
                  );
        return new ReviewState(course.getVersion(), canSubmit, checks, summary);
    }

    private static ReviewCheck check(String code, String tab, String label, boolean required, boolean complete) {
        return new ReviewCheck(code, tab, label, required, complete);
    }

    private static boolean lessonReady(LessonNode node) {
        return (
            text(node.lesson().getTitle()) &&
            (text(node.lesson().getVideoUrl()) || !node.materials().isEmpty()) &&
            node
                .materials()
                .stream()
                .allMatch(material -> text(material.getFileUrl()) && text(material.getFileType()))
        );
    }

    private static boolean pricingReady(CourseDTO course) {
        BigDecimal price = course.getPrice();
        boolean priceReady =
            price != null &&
            price.signum() >= 0 &&
            price.stripTrailingZeros().scale() <= 0 &&
            price.compareTo(new BigDecimal("10000000000000000000")) < 0;
        boolean legacy = course.getAccessDurationValue() == null && course.getAccessDurationUnit() == null;
        Integer duration = legacy ? course.getValidDurationDays() : course.getAccessDurationValue();
        return priceReady && duration != null && duration > 0 && (legacy || course.getAccessDurationUnit() != null);
    }

    private static boolean text(String value) {
        return value != null && !value.isBlank();
    }
}

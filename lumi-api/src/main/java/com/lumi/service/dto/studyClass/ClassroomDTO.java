package com.lumi.service.dto.studyClass;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public record ClassroomDTO(
    Long id,
    String name,
    String description,
    String joinCode,
    List<Member> students,
    List<Material> lessons,
    List<Task> assignments
) {
    public record Member(Long id, Long studentId, String name, String email, String status, Instant joinedAt) {}

    public record Material(Long id, String title, String fileType, String fileUrl, Instant uploadedAt, Integer orderIndex) {}

    public record Task(
        Long id,
        String title,
        String instructions,
        Instant dueAt,
        String status,
        String fileName,
        String fileType,
        Long fileSizeBytes
    ) {}

    public record JoinedClass(Long id, String name, String description, String membershipStatus) {}

    public record MemberPage(List<Member> items, int page, long totalElements, int totalPages, long activeCount, long pendingCount) {}

    public record JoinedClassPage(List<JoinedClass> items, int page, long totalElements, int totalPages) {}

    public record JoinRequest(@NotBlank @Size(max = 10) String code) {}

    public record CreateTaskRequest(@NotBlank @Size(max = 255) String title, @Size(max = 5000) String instructions, Instant dueAt) {}
}

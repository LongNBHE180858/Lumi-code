package com.lumi.service.dto;

import java.time.Instant;
import java.util.List;

public record AdminDashboardDTO(
    Counts counts,
    Counts previousMonth,
    List<MonthlyPoint> monthlyUsers,
    List<Activity> activities,
    Requests requests,
    long unclassifiedUsers
) {
    public record Counts(long students, long parents, long instructors, long courses, long users) {
        public Counts(long students, long parents, long instructors, long courses) {
            this(students, parents, instructors, courses, students + parents + instructors);
        }
    }

    public record MonthlyPoint(String month, Long users) {}

    public record Activity(String kind, Instant occurredAt) {}

    public record Requests(long instructorApplications, long courseReviews, long supportTickets) {}

    public record PendingRequest(String kind, long id, String label) {}

    public record Page<T>(List<T> items, boolean hasMore) {}
}

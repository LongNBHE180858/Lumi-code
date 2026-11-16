package com.lumi.service;

import com.lumi.service.dto.AdminDashboardDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminDashboardService {

    private static final ZoneId DASHBOARD_ZONE = ZoneId.of("Asia/Bangkok");

    private static final String USER_ROLES = """
    with classified as (
        select u.id,
            case
                when exists (select 1 from jhi_user_authority a where a.user_id = u.id and a.authority_name = 'ROLE_INSTRUCTOR') then 'instructor'
                when exists (select 1 from jhi_user_authority a where a.user_id = u.id and a.authority_name = 'ROLE_PARENT') then 'parent'
                when exists (select 1 from jhi_user_authority a where a.user_id = u.id and a.authority_name = 'ROLE_STUDENT') then 'student'
            end as role
        from jhi_user u
        where not exists (select 1 from jhi_user_authority a where a.user_id = u.id and a.authority_name = 'ROLE_ADMIN')
    )
    select count(*) filter (where role = 'student') as students,
        count(*) filter (where role = 'parent') as parents,
        count(*) filter (where role = 'instructor') as instructors,
        count(*) filter (where role is null) as unclassified
    from classified
    """;

    private static final String PUBLIC_COURSES = """
    select count(*) from courses c join grades g on g.id = c.grade_id
    where c.status = 'ACTIVE' and (c.is_deleted = false or c.is_deleted is null) and g.level between 1 and 9
    """;

    private final JdbcTemplate jdbc;

    public AdminDashboardService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional(readOnly = true)
    public AdminDashboardDTO getDashboard() {
        YearMonth currentMonth = YearMonth.now(DASHBOARD_ZONE);
        AdminDashboardDTO.Counts current = currentCounts();
        List<AdminDashboardDTO.MonthlyPoint> monthlyUsers = new ArrayList<>();
        for (int offset = 5; offset >= 0; offset--) {
            YearMonth month = currentMonth.minusMonths(offset);
            monthlyUsers.add(new AdminDashboardDTO.MonthlyPoint(month.toString(), offset == 0 ? current.users() : null));
        }
        return new AdminDashboardDTO(current, null, monthlyUsers, getActivities(0, 4, "").items(), requests(), unclassifiedUsers());
    }

    @Transactional(readOnly = true)
    public AdminDashboardDTO.Page<AdminDashboardDTO.Activity> getActivities(int page, int size, String search) {
        List<AdminDashboardDTO.Activity> items = jdbc.query(
            """
            select kind, occurred_at from (
                select 'USER_REGISTERED' as kind, u.id, u.created_date as occurred_at, 'Người dùng mới đăng ký' as label from jhi_user u
                where u.created_date is not null
                    and not exists (select 1 from jhi_user_authority a where a.user_id = u.id and a.authority_name = 'ROLE_ADMIN')
                    and exists (select 1 from jhi_user_authority a where a.user_id = u.id and a.authority_name in ('ROLE_STUDENT', 'ROLE_PARENT', 'ROLE_INSTRUCTOR'))
                union all
                select 'INSTRUCTOR_APPROVED', p.user_id, p.instructor_reviewed_at, 'Giảng viên mới tham gia' from user_profiles p
                where p.instructor_status = 'ACTIVE' and p.instructor_reviewed_at is not null
                union all
                select 'COURSE_CREATED', c.id, c.created_at, 'Khóa học mới được tạo' from courses c where c.created_at is not null
                union all
                select 'COURSE_APPROVED', r.id, r.reviewed_at, 'Khóa học được phê duyệt' from course_review_requests r
                where r.status = 'APPROVED' and r.reviewed_at is not null
            ) events where position(lower(?) in lower(label)) > 0
            order by occurred_at desc, kind, id desc limit ? offset ?
            """,
            (rs, rowNum) -> activity(rs),
            search,
            size + 1,
            (long) page * size
        );
        return slice(items, size);
    }

    @Transactional(readOnly = true)
    public AdminDashboardDTO.Page<AdminDashboardDTO.PendingRequest> getPendingRequests(int page, int size, String search) {
        List<AdminDashboardDTO.PendingRequest> items = jdbc.query(
            """
            select kind, id, label from (
                select 'INSTRUCTOR_APPLICATION' as kind, u.id,
                    concat('Giảng viên: ', coalesce(nullif(concat_ws(' ', u.first_name, u.last_name), ''), u.login)) as label,
                    coalesce(p.updated_at, u.created_date) as occurred_at
                from user_profiles p join jhi_user u on u.id = p.user_id
                where p.instructor_status = 'PENDING' and u.activated = true
                    and exists (select 1 from jhi_user_authority a where a.user_id = u.id and a.authority_name = 'ROLE_INSTRUCTOR')
                union all
                select 'COURSE_REVIEW', r.id, concat('Khóa học: ', c.title), r.submitted_at
                from course_review_requests r join courses c on c.id = r.course_id
                where r.status = 'PENDING'
                union all
                select 'SUPPORT_TICKET', t.id, concat('Hỗ trợ: ', t.subject), t.updated_at
                from support_tickets t where t.status = 'OPEN'
            ) pending where position(lower(?) in lower(label)) > 0
            order by occurred_at desc nulls last, kind, id desc limit ? offset ?
            """,
            (rs, rowNum) -> new AdminDashboardDTO.PendingRequest(rs.getString("kind"), rs.getLong("id"), rs.getString("label")),
            search,
            size + 1,
            (long) page * size
        );
        return slice(items, size);
    }

    private <T> AdminDashboardDTO.Page<T> slice(List<T> items, int size) {
        return new AdminDashboardDTO.Page<>(List.copyOf(items.subList(0, Math.min(items.size(), size))), items.size() > size);
    }

    private AdminDashboardDTO.Counts currentCounts() {
        long[] users = jdbc.queryForObject(USER_ROLES, (rs, rowNum) -> new long[] {
            rs.getLong("students"),
            rs.getLong("parents"),
            rs.getLong("instructors"),
        });
        long courses = jdbc.queryForObject(PUBLIC_COURSES, Long.class);
        return new AdminDashboardDTO.Counts(users[0], users[1], users[2], courses);
    }

    private long unclassifiedUsers() {
        return jdbc.queryForObject(USER_ROLES, (rs, rowNum) -> rs.getLong("unclassified"));
    }

    private AdminDashboardDTO.Requests requests() {
        long applications = jdbc.queryForObject(
            """
            select count(*) from user_profiles p join jhi_user u on u.id = p.user_id
            where p.instructor_status = 'PENDING' and u.activated = true
                and exists (select 1 from jhi_user_authority a where a.user_id = u.id and a.authority_name = 'ROLE_INSTRUCTOR')
            """,
            Long.class
        );
        long reviews = jdbc.queryForObject("select count(*) from course_review_requests where status = 'PENDING'", Long.class);
        long tickets = jdbc.queryForObject("select count(*) from support_tickets where status = 'OPEN'", Long.class);
        return new AdminDashboardDTO.Requests(applications, reviews, tickets);
    }

    private AdminDashboardDTO.Activity activity(ResultSet rs) throws SQLException {
        return new AdminDashboardDTO.Activity(rs.getString("kind"), rs.getTimestamp("occurred_at").toInstant());
    }
}

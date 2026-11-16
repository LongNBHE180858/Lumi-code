package com.lumi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lumi.IntegrationTest;
import com.lumi.service.AdminDashboardService;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@IntegrationTest
@Transactional
class AdminDashboardResourceIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private AdminDashboardService dashboardService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void countsBusinessRolesAndExcludesUserOnlyAccounts() throws Exception {
        var before = dashboardService.getDashboard();
        addUser("ROLE_STUDENT");
        addUser("ROLE_USER");

        var after = dashboardService.getDashboard();
        assertThat(after.counts().students()).isEqualTo(before.counts().students() + 1);
        assertThat(after.counts().users()).isEqualTo(before.counts().users() + 1);
        assertThat(after.unclassifiedUsers()).isEqualTo(before.unclassifiedUsers() + 1);
        assertThat(after.previousMonth()).isNull();
        assertThat(after.monthlyUsers().subList(0, 5)).allSatisfy(point -> assertThat(point.users()).isNull());
        assertThat(after.monthlyUsers().get(5).users()).isEqualTo(after.counts().users());
        mockMvc
            .perform(get("/api/admin/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.counts.students").value(after.counts().students()))
            .andExpect(jsonPath("$.counts.users").value(after.counts().users()))
            .andExpect(jsonPath("$.monthlyUsers.length()").value(6));
    }

    private void addUser(String role) {
        Long id = jdbc.queryForObject("select nextval('sequence_generator')", Long.class);
        jdbc.update(
            "insert into jhi_user (id, login, password_hash, activated, created_by, created_date) values (?, ?, ?, ?, ?, ?)",
            id,
            "dashboard-" + UUID.randomUUID(),
            "x".repeat(60),
            true,
            "test",
            Timestamp.from(Instant.now())
        );
        jdbc.update("insert into jhi_user_authority (user_id, authority_name) values (?, ?)", id, role);
    }
}

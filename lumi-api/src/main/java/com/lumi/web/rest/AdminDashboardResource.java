package com.lumi.web.rest;

import com.lumi.service.AdminDashboardService;
import com.lumi.service.dto.AdminDashboardDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminDashboardResource {

    private final AdminDashboardService dashboardService;

    public AdminDashboardResource(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public AdminDashboardDTO getDashboard() {
        return dashboardService.getDashboard();
    }

    @GetMapping("/activities")
    public AdminDashboardDTO.Page<AdminDashboardDTO.Activity> getActivities(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "") String search
    ) {
        validatePagination(page, size, search);
        return dashboardService.getActivities(page, size, search.strip());
    }

    @GetMapping("/requests")
    public AdminDashboardDTO.Page<AdminDashboardDTO.PendingRequest> getRequests(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "") String search
    ) {
        validatePagination(page, size, search);
        return dashboardService.getPendingRequests(page, size, search.strip());
    }

    private void validatePagination(int page, int size, String search) {
        if (page < 0 || size < 1 || size > 100 || search.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination");
        }
    }
}

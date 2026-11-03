package com.lumi.web.rest;

import com.lumi.domain.enumeration.SupportTicketStatus;
import com.lumi.service.SupportTicketManagementService;
import com.lumi.service.dto.SupportTicketStatusRequest;
import com.lumi.service.dto.SupportTicketViewDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/admin/support-tickets")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminSupportTicketResource {

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("id", "createdAt", "status", "type", "subject");

    private final SupportTicketManagementService ticketService;

    public AdminSupportTicketResource(SupportTicketManagementService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<SupportTicketViewDTO>> findAll(
        @RequestParam(required = false) SupportTicketStatus status,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String search,
        @PageableDefault(
            size = 20,
            sort = "createdAt",
            direction = Sort.Direction.DESC
        ) @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        if (
            pageable.getPageSize() > 100 ||
            pageable
                .getSort()
                .stream()
                .anyMatch(order -> !ALLOWED_SORT_PROPERTIES.contains(order.getProperty()))
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination or sort");
        }
        Page<SupportTicketViewDTO> page = ticketService.findAllForAdmin(status, type, search, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PatchMapping("/{id}/status")
    public SupportTicketViewDTO changeStatus(@PathVariable Long id, @Valid @RequestBody SupportTicketStatusRequest request) {
        return ticketService.changeStatus(id, request.status());
    }
}

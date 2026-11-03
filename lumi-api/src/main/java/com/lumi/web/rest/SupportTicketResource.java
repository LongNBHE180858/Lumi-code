package com.lumi.web.rest;

import com.lumi.service.SupportTicketManagementService;
import com.lumi.service.dto.SupportTicketCreateRequest;
import com.lumi.service.dto.SupportTicketViewDTO;
import com.lumi.service.dto.TicketMessageCreateRequest;
import com.lumi.service.dto.TicketMessageViewDTO;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/support-tickets")
@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_INSTRUCTOR', 'ROLE_PARENT')")
public class SupportTicketResource {

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("id", "createdAt", "status", "type", "subject");

    private final SupportTicketManagementService ticketService;

    public SupportTicketResource(SupportTicketManagementService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<SupportTicketViewDTO> create(@Valid @RequestBody SupportTicketCreateRequest request) {
        SupportTicketViewDTO ticket = ticketService.create(request);
        return ResponseEntity.created(URI.create("/api/support-tickets/" + ticket.id())).body(ticket);
    }

    @GetMapping
    public ResponseEntity<List<SupportTicketViewDTO>> findMine(
        @PageableDefault(
            size = 20,
            sort = "createdAt",
            direction = Sort.Direction.DESC
        ) @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        validatePageable(pageable);
        Page<SupportTicketViewDTO> page = ticketService.findMine(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public SupportTicketViewDTO findOne(@PathVariable Long id) {
        return ticketService.findOne(id);
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<TicketMessageViewDTO>> findMessages(
        @PathVariable Long id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid page or size");
        }
        Page<TicketMessageViewDTO> messages = ticketService.findMessages(id, PageRequest.of(page, size));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), messages);
        return ResponseEntity.ok().headers(headers).body(messages.getContent());
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<TicketMessageViewDTO> addMessage(@PathVariable Long id, @Valid @RequestBody TicketMessageCreateRequest request) {
        TicketMessageViewDTO message = ticketService.addMessage(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PostMapping("/{id}/reopen")
    public SupportTicketViewDTO reopen(@PathVariable Long id) {
        return ticketService.reopen(id);
    }

    private void validatePageable(Pageable pageable) {
        if (
            pageable.getPageSize() > 100 ||
            pageable
                .getSort()
                .stream()
                .anyMatch(order -> !ALLOWED_SORT_PROPERTIES.contains(order.getProperty()))
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination or sort");
        }
    }
}

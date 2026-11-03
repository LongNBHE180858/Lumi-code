package com.lumi.web.rest;

import com.lumi.service.AdminPremiumPackageService;
import com.lumi.service.dto.PremiumPackageAvailabilityRequest;
import com.lumi.service.dto.PremiumPackageDTO;
import com.lumi.service.dto.PremiumPackageRequest;
import jakarta.validation.Valid;
import java.net.URI;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/admin/premium-packages")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminPremiumPackageResource {

    private static final Set<String> ALLOWED_SORTS = Set.of(
        "id",
        "name",
        "packageType",
        "price",
        "durationDays",
        "storageQuotaMb",
        "aiGenerationLimit",
        "maxClasses",
        "available"
    );

    private final AdminPremiumPackageService service;

    public AdminPremiumPackageResource(AdminPremiumPackageService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PremiumPackageDTO>> list(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        if (
            pageable
                .getSort()
                .stream()
                .anyMatch(order -> !ALLOWED_SORTS.contains(order.getProperty()))
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort property");
        }
        Page<PremiumPackageDTO> page = service.list(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public PremiumPackageDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<PremiumPackageDTO> create(@Valid @RequestBody PremiumPackageRequest request) {
        PremiumPackageDTO created = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/premium-packages/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public PremiumPackageDTO update(@PathVariable Long id, @Valid @RequestBody PremiumPackageRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/availability")
    public PremiumPackageDTO setAvailability(@PathVariable Long id, @Valid @RequestBody PremiumPackageAvailabilityRequest request) {
        return service.setAvailability(id, request.available());
    }
}

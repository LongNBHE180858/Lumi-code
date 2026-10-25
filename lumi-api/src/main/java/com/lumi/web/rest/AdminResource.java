package com.lumi.web.rest;

import com.lumi.config.Constants;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import com.lumi.domain.enumeration.Role;
import com.lumi.service.AdminService;
import com.lumi.service.MailService;
import com.lumi.service.dto.AdminUserDTO;
import com.lumi.service.dto.CatalogItemDTO;
import com.lumi.service.dto.InstructorApplicationDTO;
import com.lumi.service.dto.RejectInstructorApplicationRequest;
import com.lumi.service.dto.UserActivationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminResource {

    private static final Logger LOG = LoggerFactory.getLogger(AdminResource.class);

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = List.of(
        "id",
        "login",
        "firstName",
        "lastName",
        "email",
        "activated",
        "langKey",
        "createdBy",
        "createdDate",
        "lastModifiedBy",
        "lastModifiedDate"
    );

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final AdminService adminService;
    private final MailService mailService;

    public AdminResource(AdminService adminService, MailService mailService) {
        this.adminService = adminService;
        this.mailService = mailService;
    }

    // CREATE: POST /api/admin/users
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody AdminUserDTO userDTO) throws URISyntaxException {
        LOG.debug("REST request to create User: {}", userDTO);

        User createdUser = adminService.createUser(userDTO);
        mailService.sendCreationEmail(createdUser);

        return ResponseEntity.created(new URI("/api/admin/users/" + createdUser.getLogin()))
            .headers(HeaderUtil.createAlert(applicationName, "userManagement.created", createdUser.getLogin()))
            .body(createdUser);
    }

    // READ: GET /api/admin/users
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserDTO>> getAllUsers(
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String search,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get all Users");

        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        Role selectedRole = null;
        if (role != null) {
            try {
                selectedRole = Role.valueOf(role);
            } catch (IllegalArgumentException exception) {
                return ResponseEntity.badRequest().build();
            }
        }
        Page<AdminUserDTO> page = adminService.getAllUsers(selectedRole, search, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    // READ: GET /api/admin/users/{login}
    @GetMapping("/users/{login}")
    public ResponseEntity<AdminUserDTO> getUser(@PathVariable("login") @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
        LOG.debug("REST request to get User: {}", login);

        Optional<AdminUserDTO> user = adminService.getUser(login);

        return ResponseUtil.wrapOrNotFound(user);
    }

    @PatchMapping("/users/{login}/activation")
    public AdminUserDTO setUserActivation(
        @PathVariable("login") @Pattern(regexp = Constants.LOGIN_REGEX) String login,
        @Valid @RequestBody UserActivationRequest request
    ) {
        return adminService.setUserActivation(login, request.activated());
    }

    @GetMapping("/roles")
    public List<String> getManagedRoles() {
        return adminService.getManagedRoles();
    }

    @GetMapping("/catalog/{kind}")
    public List<CatalogItemDTO> getCatalog(@PathVariable String kind) {
        return adminService.getCatalog(kind);
    }

    @PostMapping("/catalog/{kind}")
    public ResponseEntity<CatalogItemDTO> createCatalogItem(@PathVariable String kind, @Valid @RequestBody CatalogItemDTO input) {
        CatalogItemDTO created = adminService.saveCatalog(kind, null, input);
        return ResponseEntity.created(URI.create("/api/admin/catalog/" + kind + "/" + created.id())).body(created);
    }

    @PutMapping("/catalog/{kind}/{id}")
    public CatalogItemDTO updateCatalogItem(@PathVariable String kind, @PathVariable Long id, @Valid @RequestBody CatalogItemDTO input) {
        return adminService.saveCatalog(kind, id, input);
    }

    @PatchMapping("/catalog/{kind}/{id}/active")
    public CatalogItemDTO setCatalogActive(@PathVariable String kind, @PathVariable Long id, @RequestBody Map<String, Boolean> input) {
        Boolean active = input.get("active");
        if (active == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "active is required");
        }
        return adminService.setCatalogActive(kind, id, active);
    }

    @GetMapping("/instructor-applications")
    public ResponseEntity<List<InstructorApplicationDTO>> getInstructorApplications(
        @RequestParam(defaultValue = "PENDING") InstructorApplicationStatus status,
        @PageableDefault(
            size = 20,
            sort = "id",
            direction = Sort.Direction.DESC
        ) @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        boolean hasUnsupportedSort = pageable
            .getSort()
            .stream()
            .anyMatch(order -> !"id".equals(order.getProperty()));
        if (hasUnsupportedSort) {
            return ResponseEntity.badRequest().build();
        }
        Page<InstructorApplicationDTO> page = adminService.getInstructorApplications(status, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/instructor-applications/{userId}")
    public InstructorApplicationDTO getInstructorApplication(@PathVariable Long userId) {
        return adminService.getInstructorApplication(userId);
    }

    @PostMapping("/instructor-applications/{userId}/approve")
    public InstructorApplicationDTO approveInstructorApplication(@PathVariable Long userId) {
        return adminService.approveInstructorApplication(userId);
    }

    @PostMapping("/instructor-applications/{userId}/reject")
    public InstructorApplicationDTO rejectInstructorApplication(
        @PathVariable Long userId,
        @Valid @RequestBody RejectInstructorApplicationRequest request
    ) {
        return adminService.rejectInstructorApplication(userId, request.reason());
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }
}

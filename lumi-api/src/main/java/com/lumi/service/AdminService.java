package com.lumi.service;

import com.lumi.config.Constants;
import com.lumi.domain.Authority;
import com.lumi.domain.Category;
import com.lumi.domain.Grade;
import com.lumi.domain.Subject;
import com.lumi.domain.User;
import com.lumi.domain.UserProfile;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import com.lumi.domain.enumeration.Role;
import com.lumi.repository.AuthorityRepository;
import com.lumi.repository.CategoryRepository;
import com.lumi.repository.GradeRepository;
import com.lumi.repository.SubjectRepository;
import com.lumi.repository.UserProfileRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.security.SecurityUtils;
import com.lumi.service.dto.AdminUserDTO;
import com.lumi.service.dto.CatalogItemDTO;
import com.lumi.service.dto.InstructorApplicationDTO;
import com.lumi.web.rest.errors.BadRequestAlertException;
import com.lumi.web.rest.errors.EmailAlreadyUsedException;
import com.lumi.web.rest.errors.LoginAlreadyUsedException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tech.jhipster.security.RandomUtil;

@Service
@Transactional
public class AdminService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;

    public AdminService(
        UserRepository userRepository,
        UserProfileRepository userProfileRepository,
        AuthorityRepository authorityRepository,
        PasswordEncoder passwordEncoder,
        UserService userService,
        CategoryRepository categoryRepository,
        SubjectRepository subjectRepository,
        GradeRepository gradeRepository
    ) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.subjectRepository = subjectRepository;
        this.gradeRepository = gradeRepository;
    }

    @Transactional(readOnly = true)
    public List<CatalogItemDTO> getCatalog(String kind) {
        return switch (kind) {
            case "subjects" -> subjectRepository
                .findAll()
                .stream()
                .map(this::catalogItem)
                .sorted(Comparator.comparing(CatalogItemDTO::name))
                .toList();
            case "grades" -> gradeRepository
                .findAll()
                .stream()
                .map(this::catalogItem)
                .sorted(Comparator.comparing(CatalogItemDTO::level))
                .toList();
            case "categories" -> categoryRepository
                .findAll()
                .stream()
                .map(this::catalogItem)
                .sorted(Comparator.comparing(CatalogItemDTO::name))
                .toList();
            default -> throw catalogNotFound();
        };
    }

    public CatalogItemDTO saveCatalog(String kind, Long id, CatalogItemDTO input) {
        String name = validateCatalogName(kind, input);
        return switch (kind) {
            case "subjects" -> saveSubject(id, name, input);
            case "grades" -> saveGrade(id, name, input);
            case "categories" -> saveCategory(id, name, input);
            default -> throw catalogNotFound();
        };
    }

    public CatalogItemDTO setCatalogActive(String kind, Long id, boolean active) {
        return switch (kind) {
            case "subjects" -> {
                Subject item = subjectRepository.findById(id).orElseThrow(this::missingCatalog);
                item.setActive(active);
                yield catalogItem(item);
            }
            case "grades" -> {
                Grade item = gradeRepository.findById(id).orElseThrow(this::missingCatalog);
                item.setActive(active);
                yield catalogItem(item);
            }
            case "categories" -> {
                Category item = categoryRepository.findById(id).orElseThrow(this::missingCatalog);
                if (!active && categoryRepository.existsByParentIdAndActiveTrue(id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Deactivate child categories first");
                }
                item.setActive(active);
                yield catalogItem(item);
            }
            default -> throw catalogNotFound();
        };
    }

    private String validateCatalogName(String kind, CatalogItemDTO input) {
        if (input == null || input.name() == null || input.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        String name = input.name().trim();
        if (name.length() > ("grades".equals(kind) ? 50 : 100)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is too long");
        }
        return name;
    }

    private CatalogItemDTO saveSubject(Long id, String name, CatalogItemDTO input) {
        Subject item = id == null ? new Subject() : subjectRepository.findById(id).orElseThrow(this::missingCatalog);
        boolean nameExists = subjectRepository
            .findByNameIgnoreCase(name)
            .filter(other -> id == null || !Objects.equals(other.getId(), id))
            .isPresent();
        if (nameExists) {
            throw duplicateCatalog();
        }
        item.setName(name);
        item.setDescription(input.description());
        return catalogItem(subjectRepository.save(item));
    }

    private CatalogItemDTO saveGrade(Long id, String name, CatalogItemDTO input) {
        if (input.level() == null || input.level() < 1 || input.level() > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grade level must be between 1 and 12");
        }
        Grade item = id == null ? new Grade() : gradeRepository.findById(id).orElseThrow(this::missingCatalog);
        boolean levelExists = gradeRepository
            .findByLevel(input.level())
            .filter(other -> id == null || !Objects.equals(other.getId(), id))
            .isPresent();
        if (levelExists) {
            throw duplicateCatalog();
        }
        item.setName(name);
        item.setLevel(input.level());
        return catalogItem(gradeRepository.save(item));
    }

    private CatalogItemDTO saveCategory(Long id, String name, CatalogItemDTO input) {
        Category item = id == null ? new Category() : categoryRepository.findById(id).orElseThrow(this::missingCatalog);
        boolean nameExists = categoryRepository
            .findByNameIgnoreCase(name)
            .filter(other -> id == null || !Objects.equals(other.getId(), id))
            .isPresent();
        if (nameExists) {
            throw duplicateCatalog();
        }
        Category parent = input.parentId() == null ? null : categoryRepository.findById(input.parentId()).orElseThrow(this::missingCatalog);
        if (parent != null && !Boolean.TRUE.equals(parent.getActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent category is inactive");
        }
        for (Category ancestor = parent; ancestor != null; ancestor = ancestor.getParent()) {
            if (Objects.equals(ancestor.getId(), id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category cycle is not allowed");
            }
        }
        item.setName(name);
        item.setDescription(input.description());
        item.setParent(parent);
        return catalogItem(categoryRepository.save(item));
    }

    private ResponseStatusException catalogNotFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog not found");
    }

    private ResponseStatusException missingCatalog() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog item not found");
    }

    private ResponseStatusException duplicateCatalog() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Catalog item already exists");
    }

    private CatalogItemDTO catalogItem(Subject item) {
        return new CatalogItemDTO(item.getId(), item.getName(), item.getDescription(), null, null, item.getActive());
    }

    private CatalogItemDTO catalogItem(Grade item) {
        return new CatalogItemDTO(item.getId(), item.getName(), null, item.getLevel(), null, item.getActive());
    }

    private CatalogItemDTO catalogItem(Category item) {
        Long parentId = item.getParent() == null ? null : item.getParent().getId();
        return new CatalogItemDTO(item.getId(), item.getName(), item.getDescription(), null, parentId, item.getActive());
    }

    public User createUser(AdminUserDTO userDTO) {
        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
        }
        if (userDTO.getAuthorities() != null && userDTO.getAuthorities().contains(AuthoritiesConstants.ADMIN)) {
            throw new BadRequestAlertException("Admin accounts cannot be created", "userManagement", "adminCreationDisabled");
        }
        if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        }
        if (userDTO.getEmail() != null && userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        }
        User user = new User();

        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }

        user.setImageUrl(userDTO.getImageUrl());
        user.setLangKey(userDTO.getLangKey() == null ? Constants.DEFAULT_LANGUAGE : userDTO.getLangKey());

        String rawPassword = RandomUtil.generatePassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);

        user.setAuthorities(resolveManagedAuthorities(userDTO.getAuthorities()));

        User savedUser = userRepository.save(user);
        userService.clearUserCaches(savedUser);
        LOG.debug("Created Information for User: {}", savedUser);

        return savedUser;
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        if (userDTO.getEmail() != null) {
            userRepository
                .findOneByEmailIgnoreCase(userDTO.getEmail())
                .filter(user -> !user.getId().equals(userDTO.getId()))
                .ifPresent(user -> {
                    throw new EmailAlreadyUsedException();
                });
        }
        userRepository
            .findOneByLogin(userDTO.getLogin().toLowerCase())
            .filter(user -> !user.getId().equals(userDTO.getId()))
            .ifPresent(user -> {
                throw new LoginAlreadyUsedException();
            });
        Optional<User> existingUser = userRepository.findById(userDTO.getId());

        if (existingUser.isEmpty()) {
            return Optional.empty();
        }

        User user = existingUser.orElseThrow();
        preventProtectedAdminUpdate(user, userDTO);
        userService.clearUserCaches(user);

        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }

        user.setImageUrl(userDTO.getImageUrl());
        user.setActivated(userDTO.isActivated());
        user.setLangKey(userDTO.getLangKey());

        Set<Authority> requestedAuthorities = resolveManagedAuthorities(userDTO.getAuthorities());
        Set<Authority> authorities = user.getAuthorities();
        authorities.clear();
        authorities.addAll(requestedAuthorities);

        User savedUser = userRepository.save(user);
        userService.clearUserCaches(savedUser);
        LOG.debug("Changed Information for User: {}", savedUser);

        return Optional.of(new AdminUserDTO(savedUser));
    }

    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllUsers(Role role, String search, Pageable pageable) {
        boolean hasSearch = search != null && !search.isBlank();
        String searchPattern = !hasSearch
            ? "%"
            : "%" + search.trim().toLowerCase(Locale.ROOT).replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
        Page<User> users =
            role == null && !hasSearch
                ? userRepository.findAll(pageable)
                : userRepository.findAllByManagedRoleAndSearch(role == null ? "" : role.name(), searchPattern, pageable);
        return users.map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<AdminUserDTO> getUser(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login).map(AdminUserDTO::new);
    }

    public AdminUserDTO setUserActivation(String login, boolean activated) {
        User user = userRepository
            .findOneWithAuthoritiesByLogin(login)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (hasAdminRole(user)) {
            throw new BadRequestAlertException("The admin account cannot be locked or activated", "userManagement", "adminAccessProtected");
        }
        userService.clearUserCaches(user);
        user.setActivated(activated);
        User savedUser = userRepository.save(user);
        userService.clearUserCaches(savedUser);
        return new AdminUserDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public List<String> getManagedRoles() {
        return Arrays.stream(Role.values())
            .filter(role -> role != Role.ROLE_ADMIN)
            .map(Role::name)
            .toList();
    }

    @Transactional(readOnly = true)
    public Page<InstructorApplicationDTO> getInstructorApplications(InstructorApplicationStatus status, Pageable pageable) {
        if (
            status != InstructorApplicationStatus.PENDING &&
            status != InstructorApplicationStatus.ACTIVE &&
            status != InstructorApplicationStatus.REJECTED
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported instructor application status");
        }
        return userProfileRepository
            .findInstructorApplications(AuthoritiesConstants.INSTRUCTOR, status, pageable)
            .map(InstructorApplicationDTO::from);
    }

    @Transactional(readOnly = true)
    public InstructorApplicationDTO getInstructorApplication(Long userId) {
        UserProfile profile = userProfileRepository
            .findOneByUserId(userId)
            .filter(this::isInstructorApplication)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor application not found"));
        return InstructorApplicationDTO.from(profile);
    }

    public InstructorApplicationDTO approveInstructorApplication(Long userId) {
        return reviewInstructorApplication(userId, InstructorApplicationStatus.ACTIVE, null);
    }

    public InstructorApplicationDTO rejectInstructorApplication(Long userId, String reason) {
        if (reason == null || reason.isBlank() || reason.length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rejection reason must be between 1 and 2000 characters");
        }
        return reviewInstructorApplication(userId, InstructorApplicationStatus.REJECTED, reason.trim());
    }

    private InstructorApplicationDTO reviewInstructorApplication(Long userId, InstructorApplicationStatus decision, String reason) {
        UserProfile profile = userProfileRepository
            .findInstructorApplicationForUpdate(userId)
            .filter(this::isInstructorApplication)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor application not found"));
        if (!profile.getUser().isActivated()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email verification is required before review");
        }
        if (profile.getInstructorStatus() != InstructorApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Instructor application has already been reviewed");
        }
        SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccessDeniedException("Admin login required"));
        Instant now = Instant.now();
        profile.setInstructorStatus(decision);
        profile.setInstructorRejectionReason(reason);
        profile.setInstructorReviewedAt(now);
        profile.setUpdatedAt(now);
        return InstructorApplicationDTO.from(userProfileRepository.saveAndFlush(profile));
    }

    private boolean isInstructorApplication(UserProfile profile) {
        return (
            profile.getInstructorStatus() != null &&
            profile
                .getUser()
                .getAuthorities()
                .stream()
                .anyMatch(authority -> AuthoritiesConstants.INSTRUCTOR.equals(authority.getName()))
        );
    }

    public void deleteUser(String login) {
        Optional<User> existingUser = userRepository.findOneByLogin(login);

        if (existingUser.isEmpty()) {
            return;
        }

        User user = existingUser.orElseThrow();
        preventProtectedAdminDeletion(user);
        userRepository.delete(user);
        userService.clearUserCaches(user);

        LOG.debug("Deleted User: {}", user);
    }

    private void preventProtectedAdminUpdate(User user, AdminUserDTO requestedUser) {
        boolean hasAdminRole = hasAdminRole(user);
        boolean keepsAdminRole =
            requestedUser.getAuthorities() != null && requestedUser.getAuthorities().contains(AuthoritiesConstants.ADMIN);
        if (!hasAdminRole && keepsAdminRole) {
            throw new BadRequestAlertException("Admin access cannot be granted", "userManagement", "adminCreationDisabled");
        }
        boolean removesAdminRole = hasAdminRole && !keepsAdminRole;
        boolean deactivatesAdmin = hasAdminRole && user.isActivated() && !requestedUser.isActivated();
        if (removesAdminRole || deactivatesAdmin) {
            throw new BadRequestAlertException("The admin account cannot lose access", "userManagement", "adminAccessProtected");
        }
    }

    private void preventProtectedAdminDeletion(User user) {
        if (hasAdminRole(user)) {
            throw new BadRequestAlertException("The admin account cannot be deleted", "userManagement", "adminDeleteProtected");
        }
    }

    private boolean hasAdminRole(User user) {
        return user
            .getAuthorities()
            .stream()
            .anyMatch(authority -> AuthoritiesConstants.ADMIN.equals(authority.getName()));
    }

    private Set<Authority> resolveManagedAuthorities(Set<String> requestedRoles) {
        Set<String> roles = requestedRoles == null ? Set.of() : requestedRoles;
        Set<String> allowedRoles = Set.of(
            AuthoritiesConstants.USER,
            Role.ROLE_ADMIN.name(),
            Role.ROLE_INSTRUCTOR.name(),
            Role.ROLE_STUDENT.name(),
            Role.ROLE_PARENT.name()
        );
        long selectedBusinessRoles = roles
            .stream()
            .filter(role -> !AuthoritiesConstants.USER.equals(role))
            .count();
        if (!allowedRoles.containsAll(roles) || selectedBusinessRoles > 1) {
            throw new BadRequestAlertException("Choose one valid role", "userManagement", "invalidRole");
        }

        // Existing clients submit ROLE_USER alone. Treat it as the student role.
        String businessRole = roles
            .stream()
            .filter(role -> !AuthoritiesConstants.USER.equals(role))
            .findFirst()
            .orElse(Role.ROLE_STUDENT.name());
        return Set.of(requireAuthority(AuthoritiesConstants.USER), requireAuthority(businessRole));
    }

    private Authority requireAuthority(String name) {
        return authorityRepository.findById(name).orElseThrow(() -> new IllegalStateException("Missing authority: " + name));
    }
}

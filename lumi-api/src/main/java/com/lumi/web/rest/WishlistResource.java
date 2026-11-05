package com.lumi.web.rest;

import com.lumi.domain.User;
import com.lumi.repository.CourseRepository;
import com.lumi.repository.UserRepository;
import com.lumi.repository.WishlistRepository;
import com.lumi.security.SecurityUtils;
import com.lumi.service.WishlistService;
import com.lumi.service.criteria.WishlistCriteria;
import com.lumi.service.dto.UserDTO;
import com.lumi.service.dto.WishlistDTO;
import com.lumi.service.query.WishlistQueryService;
import com.lumi.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumi.domain.Wishlist}.
 *
 * <p>The current user is always derived from the authentication context and used both as the
 * wishlist owner and as the scope for read/delete operations. Client supplied owner or creation
 * timestamp values are never trusted.
 */
@RestController
@RequestMapping("/api/wishlists")
public class WishlistResource {

    private static final Logger LOG = LoggerFactory.getLogger(WishlistResource.class);

    private static final String ENTITY_NAME = "wishlist";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final WishlistService wishlistService;

    private final WishlistRepository wishlistRepository;

    private final WishlistQueryService wishlistQueryService;

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    public WishlistResource(
        WishlistService wishlistService,
        WishlistRepository wishlistRepository,
        WishlistQueryService wishlistQueryService,
        UserRepository userRepository,
        CourseRepository courseRepository
    ) {
        this.wishlistService = wishlistService;
        this.wishlistRepository = wishlistRepository;
        this.wishlistQueryService = wishlistQueryService;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * {@code POST  /wishlists} : Create a new wishlist for the current user.
     *
     * @param wishlistDTO the wishlistDTO to create (the owner is resolved server-side).
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wishlistDTO, or with status {@code 400 (Bad Request)} if the wishlist already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WishlistDTO> createWishlist(@Valid @RequestBody WishlistDTO wishlistDTO) throws URISyntaxException {
        LOG.debug("REST request to save Wishlist : {}", wishlistDTO);
        if (wishlistDTO.getId() != null) {
            throw new BadRequestAlertException("A new wishlist cannot already have an ID", ENTITY_NAME, "idexists");
        }

        User currentUser = getCurrentUser();

        // Ownership is derived from the authenticated user; the client supplied student is ignored.
        wishlistDTO.setStudent(toCurrentUserDto(currentUser));

        // The target course must exist.
        if (
            wishlistDTO.getCourse() == null ||
            wishlistDTO.getCourse().getId() == null ||
            !courseRepository.existsById(wishlistDTO.getCourse().getId())
        ) {
            throw new BadRequestAlertException("Course not found", ENTITY_NAME, "coursenotfound");
        }

        // No duplicate (student, course) wishlist.
        if (wishlistRepository.existsByStudent_IdAndCourse_Id(currentUser.getId(), wishlistDTO.getCourse().getId())) {
            throw new BadRequestAlertException("A wishlist for this course already exists", ENTITY_NAME, "duplicate");
        }

        // The creation timestamp is generated server side.
        wishlistDTO.setCreatedAt(Instant.now());

        try {
            wishlistDTO = wishlistService.save(wishlistDTO);
        } catch (DataIntegrityViolationException ex) {
            // Concurrency safety net once a unique (student_id, course_id) constraint is added.
            LOG.debug("Duplicate wishlist prevented by unique constraint", ex);
            throw new BadRequestAlertException("A wishlist for this course already exists", ENTITY_NAME, "duplicate");
        }
        return ResponseEntity.created(new URI("/api/wishlists/" + wishlistDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, wishlistDTO.getId().toString()))
            .body(wishlistDTO);
    }

    /**
     * {@code PUT  /wishlists/:id} : Updates an existing wishlist owned by the current user.
     *
     * @param id the id of the wishlistDTO to save.
     * @param wishlistDTO the wishlistDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishlistDTO,
     * or with status {@code 400 (Bad Request)} if the wishlistDTO is not valid,
     * or with status {@code 404 (Not Found)} if the wishlist is not owned by the current user.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WishlistDTO> updateWishlist(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WishlistDTO wishlistDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Wishlist : {}, {}", id, wishlistDTO);
        if (wishlistDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wishlistDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        User currentUser = getCurrentUser();

        // The record must exist and belong to the current user; otherwise surface a 404 so the
        // existence of other users' records is not disclosed.
        WishlistDTO existing = wishlistService
            .findOne(id)
            .filter(wishlist -> Objects.equals(wishlist.getStudent().getId(), currentUser.getId()))
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

        // Ownership and creation timestamp are server managed.
        wishlistDTO.setStudent(toCurrentUserDto(currentUser));
        wishlistDTO.setCreatedAt(existing.getCreatedAt());

        // Prevent duplicate (student, course) while allowing the record itself to be kept.
        if (wishlistDTO.getCourse() != null && wishlistDTO.getCourse().getId() != null) {
            Long courseId = wishlistDTO.getCourse().getId();
            wishlistRepository
                .findOneByStudent_IdAndCourse_Id(currentUser.getId(), courseId)
                .filter(other -> !Objects.equals(other.getId(), id))
                .ifPresent(other -> {
                    throw new BadRequestAlertException("A wishlist for this course already exists", ENTITY_NAME, "duplicate");
                });
        }

        wishlistDTO = wishlistService.update(wishlistDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wishlistDTO.getId().toString()))
            .body(wishlistDTO);
    }

    /**
     * {@code PATCH  /wishlists/:id} : Partial updates given fields of an existing wishlist owned by the current user, field will ignore if it is null
     *
     * @param id the id of the wishlistDTO to save.
     * @param wishlistDTO the wishlistDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishlistDTO,
     * or with status {@code 400 (Bad Request)} if the wishlistDTO is not valid,
     * or with status {@code 404 (Not Found)} if the wishlist is not owned by the current user.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WishlistDTO> partialUpdateWishlist(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WishlistDTO wishlistDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Wishlist : {}, {}", id, wishlistDTO);
        if (wishlistDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wishlistDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        User currentUser = getCurrentUser();

        // The record must exist and belong to the current user.
        wishlistService
            .findOne(id)
            .filter(wishlist -> Objects.equals(wishlist.getStudent().getId(), currentUser.getId()))
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

        // The student is always the current user and the creation timestamp can never be changed.
        wishlistDTO.setStudent(toCurrentUserDto(currentUser));
        wishlistDTO.setCreatedAt(null);

        Optional<WishlistDTO> result = wishlistService.partialUpdate(wishlistDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wishlistDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /wishlists} : get the Wishlists of the current user.
     *
     * @param criteria the criteria which the requested entities should match (scoped to the current user).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Wishlists in body.
     */
    //    @GetMapping("")
    //    public ResponseEntity<List<WishlistDTO>> getAllWishlists(
    //        WishlistCriteria criteria,
    //        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    //    ) {
    //        LOG.debug("REST request to get Wishlists by criteria: {}", criteria);
    //
    //        // Never trust a client supplied student filter: always scope to the current user.
    //        restrictToCurrentUser(criteria);
    //        List<WishlistDTO> entityList = wishlistQueryService.findByCriteria(criteria);
    //        return ResponseEntity.ok().body(entityList);
    //    }

    /**
     * {@code GET  /wishlists/count} : count the wishlists of the current user.
     *
     * @param criteria the criteria which the requested entities should match (scoped to the current user).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countWishlists(WishlistCriteria criteria) {
        LOG.debug("REST request to count Wishlists by criteria: {}", criteria);
        restrictToCurrentUser(criteria);
        return ResponseEntity.ok().body(wishlistQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /wishlists/:id} : get the "id" wishlist if owned by the current user.
     *
     * @param id the id of the wishlistDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wishlistDTO,
     * or with status {@code 404 (Not Found)} if it does not exist or is not owned by the current user.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WishlistDTO> getWishlist(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Wishlist : {}", id);
        Optional<WishlistDTO> wishlistDTO = wishlistService
            .findOne(id)
            .filter(wishlist -> Objects.equals(wishlist.getStudent().getId(), getCurrentUser().getId()));
        return ResponseUtil.wrapOrNotFound(wishlistDTO);
    }

    /**
     * {@code DELETE  /wishlists/:id} : delete the "id" wishlist if owned by the current user.
     *
     * @param id the id of the wishlistDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}, or with status
     * {@code 404 (Not Found)} if it does not exist or is not owned by the current user.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWishlist(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Wishlist : {}", id);
        boolean ownedByCurrentUser = wishlistService
            .findOne(id)
            .filter(wishlist -> Objects.equals(wishlist.getStudent().getId(), getCurrentUser().getId()))
            .isPresent();
        if (!ownedByCurrentUser) {
            // Keep the response uniform for existing-but-unowned and missing records.
            return ResponseEntity.notFound().build();
        }
        wishlistService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    private User getCurrentUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("No authenticated user", ENTITY_NAME, "unauthorized")
        );
        return userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("Current user not found", ENTITY_NAME, "userNotFound"));
    }

    private UserDTO toCurrentUserDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        return dto;
    }

    private void restrictToCurrentUser(WishlistCriteria criteria) {
        LongFilter studentFilter = new LongFilter();
        studentFilter.setEquals(getCurrentUser().getId());
        criteria.setStudentId(studentFilter);
    }
}

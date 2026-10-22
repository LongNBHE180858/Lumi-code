package com.lumi.web.rest;

import com.lumi.domain.Authority;
import com.lumi.repository.AuthorityRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/** REST controller for managing JHipster authorities. */
@RestController
@RequestMapping("/api/authorities")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Transactional(rollbackFor = Exception.class)
public class AuthorityResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorityResource.class);
    private static final String ENTITY_NAME = "adminAuthority";
    private static final Set<String> PROTECTED_ROLES = Set.of(
        AuthoritiesConstants.USER,
        AuthoritiesConstants.STUDENT,
        AuthoritiesConstants.INSTRUCTOR,
        AuthoritiesConstants.PARENT,
        AuthoritiesConstants.ADMIN
    );

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final AuthorityRepository authorityRepository;

    public AuthorityResource(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @PostMapping("")
    public ResponseEntity<Authority> createAuthority(@Valid @RequestBody Authority authority) throws URISyntaxException {
        LOG.debug("REST request to save Authority : {}", authority);
        if (authorityRepository.existsById(authority.getName())) {
            throw new BadRequestAlertException("authority already exists", ENTITY_NAME, "idexists");
        }
        authority = authorityRepository.save(authority);
        return ResponseEntity.created(new URI("/api/authorities/" + authority.getName()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, authority.getName()))
            .body(authority);
    }

    @GetMapping("")
    public List<Authority> getAllAuthorities() {
        LOG.debug("REST request to get all Authorities");
        return authorityRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Authority> getAuthority(@PathVariable("id") String id) {
        LOG.debug("REST request to get Authority : {}", id);
        Optional<Authority> authority = authorityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(authority);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthority(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Authority : {}", id);
        if (PROTECTED_ROLES.contains(id)) {
            throw new BadRequestAlertException("Core roles cannot be deleted", ENTITY_NAME, "protectedRole");
        }
        authorityRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id))
            .build();
    }
}

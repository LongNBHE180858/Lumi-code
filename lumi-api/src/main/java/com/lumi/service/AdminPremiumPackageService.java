package com.lumi.service;

import com.lumi.domain.SystemPlan;
import com.lumi.domain.enumeration.PremiumPackageType;
import com.lumi.repository.SystemPlanRepository;
import com.lumi.service.dto.PremiumPackageDTO;
import com.lumi.service.dto.PremiumPackageRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AdminPremiumPackageService {

    private final SystemPlanRepository repository;

    public AdminPremiumPackageService(SystemPlanRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<PremiumPackageDTO> list(Pageable pageable) {
        return repository.findByCurrentRevisionTrue(pageable).map(PremiumPackageDTO::from);
    }

    @Transactional(readOnly = true)
    public PremiumPackageDTO get(Long id) {
        SystemPlan plan = repository.findById(id).orElseThrow(this::notFound);
        if (!Boolean.TRUE.equals(plan.getCurrentRevision())) {
            throw notFound();
        }
        return PremiumPackageDTO.from(plan);
    }

    public PremiumPackageDTO create(PremiumPackageRequest request) {
        validate(request);
        SystemPlan plan = new SystemPlan();
        copyFields(plan, request);
        plan.setPackageKey(UUID.randomUUID().toString());
        plan.setRevision(1);
        plan.setCurrentRevision(true);
        plan.setAvailable(false);
        return PremiumPackageDTO.from(repository.saveAndFlush(plan));
    }

    public PremiumPackageDTO update(Long id, PremiumPackageRequest request) {
        validate(request);
        SystemPlan previous = currentForUpdate(id);
        if (previous.getPackageType() != null && previous.getPackageType() != request.packageType()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Package type cannot be changed");
        }
        if (previous.getRevision() == Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Package revision limit reached");
        }
        if (previous.getPackageKey() == null) {
            previous.setPackageKey(UUID.randomUUID().toString());
        }
        previous.setCurrentRevision(false);
        repository.saveAndFlush(previous);

        SystemPlan next = new SystemPlan();
        copyFields(next, request);
        next.setPackageKey(previous.getPackageKey());
        next.setRevision(previous.getRevision() + 1);
        next.setCurrentRevision(true);
        next.setAvailable(previous.getAvailable());
        return PremiumPackageDTO.from(repository.saveAndFlush(next));
    }

    public PremiumPackageDTO setAvailability(Long id, boolean available) {
        SystemPlan plan = currentForUpdate(id);
        if (available && plan.getPackageType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Classify package before making it available");
        }
        plan.setAvailable(available);
        return PremiumPackageDTO.from(repository.saveAndFlush(plan));
    }

    private SystemPlan currentForUpdate(Long id) {
        SystemPlan plan = repository.findByIdForUpdate(id).orElseThrow(this::notFound);
        if (!Boolean.TRUE.equals(plan.getCurrentRevision())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Package revision is no longer current");
        }
        return plan;
    }

    private void validate(PremiumPackageRequest request) {
        PremiumPackageType type = request.packageType();
        if (type == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Package type is required");
        }
        boolean instructor = type == PremiumPackageType.INSTRUCTOR_PERSONAL || type == PremiumPackageType.INSTRUCTOR_CLASS;
        if (instructor && request.maxClasses() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Instructor package requires maxClasses");
        }
        if (!instructor && request.maxClasses() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent package cannot have maxClasses");
        }
    }

    private void copyFields(SystemPlan plan, PremiumPackageRequest request) {
        plan.setName(request.name().trim());
        plan.setPackageType(request.packageType());
        plan.setPrice(request.price());
        plan.setDurationDays(request.durationDays());
        plan.setStorageQuotaMb(request.storageQuotaMb());
        plan.setAiGenerationLimit(request.aiGenerationLimit());
        plan.setMaxClasses(request.maxClasses());
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Premium package not found");
    }
}

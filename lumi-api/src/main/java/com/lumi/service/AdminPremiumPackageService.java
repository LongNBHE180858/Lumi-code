package com.lumi.service;

import com.lumi.domain.SystemPlan;
import com.lumi.domain.enumeration.PremiumPackageStatus;
import com.lumi.domain.enumeration.PremiumPackageType;
import com.lumi.repository.SystemPlanRepository;
import com.lumi.service.dto.PremiumPackageDTO;
import com.lumi.service.dto.PremiumPackageRequest;
import java.time.Instant;
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
        return repository.findByStatusNot(PremiumPackageStatus.SUPERSEDED.name(), pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public PremiumPackageDTO get(Long id) {
        SystemPlan plan = repository.findById(id).orElseThrow(this::notFound);
        if (PremiumPackageStatus.SUPERSEDED.name().equals(plan.getStatus())) {
            throw notFound();
        }
        return toDto(plan);
    }

    public PremiumPackageDTO create(PremiumPackageRequest request) {
        validate(request);
        SystemPlan plan = new SystemPlan();
        copyFields(plan, request);
        plan.setPackageKey(UUID.randomUUID().toString());
        plan.setStatus(PremiumPackageStatus.UNAVAILABLE.name());
        return toDto(repository.saveAndFlush(plan));
    }

    public PremiumPackageDTO update(Long id, PremiumPackageRequest request) {
        validate(request);
        SystemPlan previous = currentForUpdate(id);
        PremiumPackageType previousType = PremiumPackageType.from(previous.getPlanType(), previous.getTargetRole());
        if (previousType != null && previousType != request.packageType()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Package type cannot be changed");
        }
        if (previous.getPackageKey() == null) {
            previous.setPackageKey(UUID.randomUUID().toString());
        }
        String nextStatus =
            PremiumPackageStatus.AVAILABLE.name().equals(previous.getStatus()) && previousType != null
                ? PremiumPackageStatus.AVAILABLE.name()
                : PremiumPackageStatus.UNAVAILABLE.name();
        previous.setStatus(PremiumPackageStatus.SUPERSEDED.name());
        previous.setUpdatedAt(Instant.now());
        repository.saveAndFlush(previous);

        SystemPlan next = new SystemPlan();
        copyFields(next, request);
        next.setPackageKey(previous.getPackageKey());
        next.setStatus(nextStatus);
        return toDto(repository.saveAndFlush(next));
    }

    public PremiumPackageDTO setAvailability(Long id, boolean available) {
        SystemPlan plan = currentForUpdate(id);
        if (available && PremiumPackageType.from(plan.getPlanType(), plan.getTargetRole()) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Classify package before making it available");
        }
        plan.setStatus(available ? PremiumPackageStatus.AVAILABLE.name() : PremiumPackageStatus.UNAVAILABLE.name());
        plan.setUpdatedAt(Instant.now());
        return toDto(repository.saveAndFlush(plan));
    }

    private SystemPlan currentForUpdate(Long id) {
        SystemPlan plan = repository.findByIdForUpdate(id).orElseThrow(this::notFound);
        if (PremiumPackageStatus.SUPERSEDED.name().equals(plan.getStatus())) {
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
        plan.setPlanType(request.packageType().getPlanType());
        plan.setTargetRole(request.packageType().getTargetRole());
        plan.setPrice(request.price());
        plan.setDurationDays(request.durationDays());
        plan.setStorageQuotaMb(request.storageQuotaMb());
        plan.setAiGenerationLimit(request.aiGenerationLimit());
        plan.setAiLimitPeriod(request.aiLimitPeriod().name());
        plan.setMaxClasses(request.maxClasses() == null ? 0 : request.maxClasses());
        plan.setFeatures(request.aiGenerationLimit() == 0 ? "[]" : "[\"AI_REQUESTS\"]");
        plan.setUpdatedAt(Instant.now());
    }

    private PremiumPackageDTO toDto(SystemPlan plan) {
        long revision = plan.getPackageKey() == null ? 1 : repository.countByPackageKey(plan.getPackageKey());
        if (revision > Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Package revision limit reached");
        }
        return PremiumPackageDTO.from(plan, (int) revision);
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Premium package not found");
    }
}

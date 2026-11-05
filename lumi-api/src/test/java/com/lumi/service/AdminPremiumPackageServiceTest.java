package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.lumi.domain.SystemPlan;
import com.lumi.domain.enumeration.PremiumAiLimitPeriod;
import com.lumi.domain.enumeration.PremiumPackageStatus;
import com.lumi.domain.enumeration.PremiumPackageType;
import com.lumi.repository.SystemPlanRepository;
import com.lumi.service.dto.PremiumPackageRequest;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AdminPremiumPackageServiceTest {

    @Mock
    private SystemPlanRepository repository;

    @Test
    void createsFamilyPackageUsingExistingColumns() {
        when(repository.saveAndFlush(any(SystemPlan.class))).thenAnswer(call -> {
            SystemPlan plan = call.getArgument(0);
            plan.setId(5L);
            return plan;
        });
        when(repository.countByPackageKey(any(String.class))).thenReturn(1L);

        var result = new AdminPremiumPackageService(repository).create(request(PremiumPackageType.PARENT_FAMILY, null));

        assertThat(result.coveredPeople()).isEqualTo(4);
        assertThat(result.maxClasses()).isNull();
        assertThat(result.revision()).isEqualTo(1);
        assertThat(result.available()).isFalse();
        assertThat(result.aiLimitPeriod()).isEqualTo("MONTH");
    }

    @Test
    void createsNewRevisionWithoutChangingPreviousBenefits() {
        SystemPlan previous = plan(PremiumPackageType.INSTRUCTOR_CLASS);
        previous.setId(7L);
        previous.setPackageKey("key");
        previous.setStatus(PremiumPackageStatus.AVAILABLE.name());
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(previous));
        when(repository.saveAndFlush(any(SystemPlan.class))).thenAnswer(call -> call.getArgument(0));
        when(repository.countByPackageKey("key")).thenReturn(2L);

        var result = new AdminPremiumPackageService(repository).update(
            7L,
            new PremiumPackageRequest(
                "Revised",
                PremiumPackageType.INSTRUCTOR_CLASS,
                new BigDecimal("200000"),
                60,
                2048L,
                20,
                PremiumAiLimitPeriod.DAY,
                3
            )
        );

        assertThat(previous.getPrice()).isEqualByComparingTo("100000");
        assertThat(previous.getStatus()).isEqualTo(PremiumPackageStatus.SUPERSEDED.name());
        assertThat(result.revision()).isEqualTo(2);
        assertThat(result.available()).isTrue();
        assertThat(result.aiLimitPeriod()).isEqualTo("DAY");
    }

    @Test
    void rejectsInvalidClassLimits() {
        AdminPremiumPackageService service = new AdminPremiumPackageService(repository);
        assertThatThrownBy(() -> service.create(request(PremiumPackageType.PARENT_PERSONAL, 2))).isInstanceOf(
            ResponseStatusException.class
        );
        assertThatThrownBy(() -> service.create(request(PremiumPackageType.INSTRUCTOR_PERSONAL, null))).isInstanceOf(
            ResponseStatusException.class
        );
    }

    @Test
    void legacyPackageCannotBePublishedBeforeClassification() {
        SystemPlan legacy = plan(null);
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(legacy));
        assertThatThrownBy(() -> new AdminPremiumPackageService(repository).setAvailability(1L, true)).isInstanceOf(
            ResponseStatusException.class
        );
    }

    @Test
    void historicalRevisionCannotBeEdited() {
        SystemPlan historical = plan(PremiumPackageType.PARENT_FAMILY);
        historical.setStatus(PremiumPackageStatus.SUPERSEDED.name());
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(historical));
        assertThatThrownBy(() ->
            new AdminPremiumPackageService(repository).update(1L, request(PremiumPackageType.PARENT_FAMILY, null))
        ).isInstanceOf(ResponseStatusException.class);
    }

    private PremiumPackageRequest request(PremiumPackageType type, Integer maxClasses) {
        return new PremiumPackageRequest("Package", type, new BigDecimal("100000"), 30, 1024L, 10, PremiumAiLimitPeriod.MONTH, maxClasses);
    }

    private SystemPlan plan(PremiumPackageType type) {
        SystemPlan plan = new SystemPlan()
            .name("Original")
            .price(new BigDecimal("100000"))
            .durationDays(30)
            .storageQuotaMb(1024L)
            .aiGenerationLimit(10)
            .aiLimitPeriod("MONTH")
            .maxClasses(type == null || type == PremiumPackageType.PARENT_FAMILY ? 0 : 2)
            .features("[\"AI_REQUESTS\"]")
            .updatedAt(java.time.Instant.now())
            .status(PremiumPackageStatus.UNAVAILABLE.name());
        if (type != null) {
            plan.setPlanType(type.getPlanType());
            plan.setTargetRole(type.getTargetRole());
        } else {
            plan.setPlanType("LEGACY");
            plan.setTargetRole("LEGACY");
        }
        return plan;
    }
}

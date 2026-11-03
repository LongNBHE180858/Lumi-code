package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lumi.domain.SystemPlan;
import com.lumi.domain.enumeration.PremiumPackageType;
import com.lumi.repository.SystemPlanRepository;
import com.lumi.service.dto.PremiumPackageDTO;
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
    void createsInactiveFamilyPackageWithFixedCoverage() {
        when(repository.saveAndFlush(any(SystemPlan.class))).thenAnswer(call -> {
            SystemPlan plan = call.getArgument(0);
            plan.setId(5L);
            return plan;
        });
        AdminPremiumPackageService service = new AdminPremiumPackageService(repository);

        PremiumPackageDTO result = service.create(request(PremiumPackageType.PARENT_FAMILY, null));

        assertThat(result.coveredPeople()).isEqualTo(4);
        assertThat(result.revision()).isEqualTo(1);
        assertThat(result.available()).isFalse();
        assertThat(result.id()).isEqualTo(5L);
    }

    @Test
    void editsCreateNewRevisionWithoutChangingPriorBenefits() {
        SystemPlan old = plan(PremiumPackageType.INSTRUCTOR_CLASS);
        old.setId(7L);
        old.setPackageKey("key");
        old.setAvailable(true);
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(old));
        when(repository.saveAndFlush(any(SystemPlan.class))).thenAnswer(call -> call.getArgument(0));
        AdminPremiumPackageService service = new AdminPremiumPackageService(repository);

        PremiumPackageRequest updated = new PremiumPackageRequest(
            "Revised",
            PremiumPackageType.INSTRUCTOR_CLASS,
            new BigDecimal("200000"),
            60,
            2048L,
            20,
            3
        );
        PremiumPackageDTO result = service.update(7L, updated);

        assertThat(old.getPrice()).isEqualByComparingTo("100000");
        assertThat(old.getStorageQuotaMb()).isEqualTo(1024L);
        assertThat(old.getCurrentRevision()).isFalse();
        assertThat(result.revision()).isEqualTo(2);
        assertThat(result.available()).isTrue();
        assertThat(result.price()).isEqualByComparingTo("200000");
        verify(repository).saveAndFlush(old);
    }

    @Test
    void rejectsParentClassLimitAndInstructorWithoutClassLimit() {
        AdminPremiumPackageService service = new AdminPremiumPackageService(repository);
        assertThatThrownBy(() -> service.create(request(PremiumPackageType.PARENT_PERSONAL, 2))).isInstanceOf(
            ResponseStatusException.class
        );
        assertThatThrownBy(() -> service.create(request(PremiumPackageType.INSTRUCTOR_PERSONAL, null))).isInstanceOf(
            ResponseStatusException.class
        );
    }

    @Test
    void legacyPlanCannotBePublishedUntilClassified() {
        SystemPlan legacy = plan(null);
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(legacy));
        AdminPremiumPackageService service = new AdminPremiumPackageService(repository);
        assertThatThrownBy(() -> service.setAvailability(1L, true)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void refusesEditingHistoricalRevision() {
        SystemPlan historical = plan(PremiumPackageType.PARENT_FAMILY);
        historical.setCurrentRevision(false);
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(historical));
        AdminPremiumPackageService service = new AdminPremiumPackageService(repository);
        assertThatThrownBy(() -> service.update(1L, request(PremiumPackageType.PARENT_FAMILY, null))).isInstanceOf(
            ResponseStatusException.class
        );
    }

    private PremiumPackageRequest request(PremiumPackageType type, Integer maxClasses) {
        return new PremiumPackageRequest("Package", type, new BigDecimal("100000"), 30, 1024L, 10, maxClasses);
    }

    private SystemPlan plan(PremiumPackageType type) {
        SystemPlan plan = new SystemPlan()
            .name("Original")
            .price(new BigDecimal("100000"))
            .durationDays(30)
            .storageQuotaMb(1024L)
            .aiGenerationLimit(10);
        plan.setPackageType(type);
        plan.setMaxClasses(type == PremiumPackageType.INSTRUCTOR_CLASS ? 2 : null);
        return plan;
    }
}

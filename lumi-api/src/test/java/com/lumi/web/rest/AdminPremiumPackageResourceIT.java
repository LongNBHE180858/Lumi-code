package com.lumi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lumi.IntegrationTest;
import com.lumi.domain.SystemPlan;
import com.lumi.domain.enumeration.PremiumAiLimitPeriod;
import com.lumi.domain.enumeration.PremiumPackageStatus;
import com.lumi.domain.enumeration.PremiumPackageType;
import com.lumi.repository.SystemPlanRepository;
import com.lumi.service.dto.PremiumPackageRequest;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(authorities = "ROLE_ADMIN")
class AdminPremiumPackageResourceIT {

    private static final String URL = "/api/admin/premium-packages";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SystemPlanRepository repository;

    @Test
    void createsInactiveFamilyPackageWithFourCoveredPeople() throws Exception {
        PremiumPackageRequest request = request(PremiumPackageType.PARENT_FAMILY, null);
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.packageType").value("PARENT_FAMILY"))
            .andExpect(jsonPath("$.coveredPeople").value(4))
            .andExpect(jsonPath("$.aiLimitPeriod").value("MONTH"))
            .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void createsPersonalAndClassPackageTypes() throws Exception {
        for (PremiumPackageType type : new PremiumPackageType[] {
            PremiumPackageType.PARENT_PERSONAL,
            PremiumPackageType.INSTRUCTOR_PERSONAL,
            PremiumPackageType.INSTRUCTOR_CLASS,
        }) {
            Integer maxClasses = type == PremiumPackageType.PARENT_PERSONAL ? null : 2;
            mvc.perform(
                post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request(type, maxClasses)))
            )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.packageType").value(type.name()))
                .andExpect(jsonPath("$.available").value(false));
        }
    }

    @Test
    void updateCreatesRevisionAndPreservesOldValues() throws Exception {
        SystemPlan first = repository.saveAndFlush(plan(PremiumPackageType.INSTRUCTOR_CLASS));
        PremiumPackageRequest changed = new PremiumPackageRequest(
            "New name",
            PremiumPackageType.INSTRUCTOR_CLASS,
            new BigDecimal("200000"),
            60,
            2048L,
            20,
            PremiumAiLimitPeriod.DAY,
            3
        );

        mvc.perform(
            put(URL + "/{id}", first.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(changed))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.revision").value(2))
            .andExpect(jsonPath("$.price").value(200000));

        SystemPlan previous = repository.findById(first.getId()).orElseThrow();
        assertThat(previous.getStatus()).isEqualTo(PremiumPackageStatus.SUPERSEDED.name());
        assertThat(previous.getPrice()).isEqualByComparingTo("100000");
        assertThat(previous.getPackageKey()).isNotNull();
        mvc.perform(get(URL + "/{id}", first.getId())).andExpect(status().isNotFound());
        mvc.perform(
            put(URL + "/{id}", first.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(changed))
        ).andExpect(status().isConflict());
    }

    @Test
    void listsCurrentPackagesWithPagination() throws Exception {
        SystemPlan old = plan(PremiumPackageType.PARENT_FAMILY);
        old.setStatus(PremiumPackageStatus.SUPERSEDED.name());
        repository.saveAndFlush(old);
        SystemPlan current = repository.saveAndFlush(plan(PremiumPackageType.PARENT_PERSONAL));

        mvc.perform(get(URL).param("page", "0").param("size", "1").param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(current.getId()));
        mvc.perform(get(URL).param("sort", "notAColumn,desc")).andExpect(status().isBadRequest());
    }

    @Test
    void togglesAvailabilityWithoutChangingPlanBenefits() throws Exception {
        SystemPlan current = repository.saveAndFlush(plan(PremiumPackageType.PARENT_FAMILY));

        mvc.perform(
            patch(URL + "/{id}/availability", current.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"available\":true}")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.available").value(true));
        mvc.perform(
            patch(URL + "/{id}/availability", current.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"available\":false}")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.available").value(false));
        assertThat(repository.findById(current.getId()).orElseThrow().getPrice()).isEqualByComparingTo("100000");
    }

    @Test
    void classifiesLegacyPackageAsNewRevision() throws Exception {
        SystemPlan legacy = repository.saveAndFlush(plan(null));
        PremiumPackageRequest classified = request(PremiumPackageType.PARENT_FAMILY, null);

        String response = mvc
            .perform(
                put(URL + "/{id}", legacy.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(classified))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.revision").value(2))
            .andExpect(jsonPath("$.needsClassification").value(false))
            .andReturn()
            .getResponse()
            .getContentAsString();

        Long newId = objectMapper.readTree(response).get("id").asLong();
        assertThat(repository.findById(legacy.getId()).orElseThrow().getPlanType()).isEqualTo("LEGACY");
        assertThat(repository.findById(legacy.getId()).orElseThrow().getPackageKey()).isEqualTo(
            repository.findById(newId).orElseThrow().getPackageKey()
        );
        mvc.perform(
            patch(URL + "/{id}/availability", newId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"available\":true}")
        ).andExpect(status().isOk());
    }

    @Test
    void rejectsInvalidPackageShapesAndFractionalVnd() throws Exception {
        PremiumPackageRequest parentWithClasses = request(PremiumPackageType.PARENT_PERSONAL, 2);
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(parentWithClasses))).andExpect(
            status().isBadRequest()
        );

        PremiumPackageRequest fractional = new PremiumPackageRequest(
            "Family",
            PremiumPackageType.PARENT_FAMILY,
            new BigDecimal("1000.50"),
            30,
            100L,
            10,
            PremiumAiLimitPeriod.MONTH,
            null
        );
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(fractional))).andExpect(
            status().isBadRequest()
        );

        PremiumPackageRequest instructorWithoutClasses = request(PremiumPackageType.INSTRUCTOR_CLASS, null);
        mvc.perform(
            post(URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(instructorWithoutClasses))
        ).andExpect(status().isBadRequest());

        SystemPlan parent = repository.saveAndFlush(plan(PremiumPackageType.PARENT_FAMILY));
        mvc.perform(
            put(URL + "/{id}", parent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request(PremiumPackageType.PARENT_PERSONAL, null)))
        ).andExpect(status().isBadRequest());

        PremiumPackageRequest negativePrice = new PremiumPackageRequest(
            "Invalid",
            PremiumPackageType.PARENT_PERSONAL,
            new BigDecimal("-1"),
            30,
            0L,
            0,
            PremiumAiLimitPeriod.DAY,
            null
        );
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(negativePrice))).andExpect(
            status().isBadRequest()
        );
    }

    @Test
    void legacyPackageRequiresClassificationBeforeSale() throws Exception {
        SystemPlan legacy = repository.saveAndFlush(plan(null));
        mvc.perform(
            patch(URL + "/{id}/availability", legacy.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"available\":true}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void nonAdminCannotReadOrWritePackages() throws Exception {
        mvc.perform(get(URL)).andExpect(status().isForbidden());
        mvc.perform(
            post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request(PremiumPackageType.PARENT_FAMILY, null)))
        ).andExpect(status().isForbidden());
        mvc.perform(
            patch(URL + "/1/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"available\":true}")
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void anonymousCannotAccessPackages() throws Exception {
        mvc.perform(get(URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void oldGeneratedEndpointIsGone() throws Exception {
        mvc.perform(get("/api/system-plans")).andExpect(status().isNotFound());
        mvc.perform(post("/api/system-plans").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isNotFound());
    }

    private PremiumPackageRequest request(PremiumPackageType type, Integer maxClasses) {
        return new PremiumPackageRequest("Family", type, new BigDecimal("100000"), 30, 1024L, 10, PremiumAiLimitPeriod.MONTH, maxClasses);
    }

    private SystemPlan plan(PremiumPackageType type) {
        SystemPlan plan = new SystemPlan()
            .name("Original")
            .price(new BigDecimal("100000"))
            .durationDays(30)
            .storageQuotaMb(1024L)
            .aiGenerationLimit(10)
            .maxClasses(type == PremiumPackageType.INSTRUCTOR_CLASS || type == PremiumPackageType.INSTRUCTOR_PERSONAL ? 2 : 0)
            .aiLimitPeriod("MONTH")
            .features("[\"AI_REQUESTS\"]")
            .updatedAt(java.time.Instant.now())
            .status(PremiumPackageStatus.UNAVAILABLE.name());
        plan.setPlanType(type == null ? "LEGACY" : type.getPlanType());
        plan.setTargetRole(type == null ? "LEGACY" : type.getTargetRole());
        return plan;
    }
}

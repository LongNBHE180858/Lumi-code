package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.SystemPlan} entity. This class is used
 * in {@link com.lumi.web.rest.SystemPlanResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /system-plans?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SystemPlanCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private BigDecimalFilter price;

    private IntegerFilter durationDays;

    private LongFilter storageQuotaMb;

    private IntegerFilter aiGenerationLimit;

    private IntegerFilter maxClasses;

    private Boolean distinct;

    public SystemPlanCriteria() {}

    public SystemPlanCriteria(SystemPlanCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.price = other.optionalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.durationDays = other.optionalDurationDays().map(IntegerFilter::copy).orElse(null);
        this.storageQuotaMb = other.optionalStorageQuotaMb().map(LongFilter::copy).orElse(null);
        this.aiGenerationLimit = other.optionalAiGenerationLimit().map(IntegerFilter::copy).orElse(null);
        this.maxClasses = other.optionalMaxClasses().map(IntegerFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SystemPlanCriteria copy() {
        return new SystemPlanCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public Optional<BigDecimalFilter> optionalPrice() {
        return Optional.ofNullable(price);
    }

    public BigDecimalFilter price() {
        if (price == null) {
            setPrice(new BigDecimalFilter());
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public IntegerFilter getDurationDays() {
        return durationDays;
    }

    public Optional<IntegerFilter> optionalDurationDays() {
        return Optional.ofNullable(durationDays);
    }

    public IntegerFilter durationDays() {
        if (durationDays == null) {
            setDurationDays(new IntegerFilter());
        }
        return durationDays;
    }

    public void setDurationDays(IntegerFilter durationDays) {
        this.durationDays = durationDays;
    }

    public LongFilter getStorageQuotaMb() {
        return storageQuotaMb;
    }

    public Optional<LongFilter> optionalStorageQuotaMb() {
        return Optional.ofNullable(storageQuotaMb);
    }

    public LongFilter storageQuotaMb() {
        if (storageQuotaMb == null) {
            setStorageQuotaMb(new LongFilter());
        }
        return storageQuotaMb;
    }

    public void setStorageQuotaMb(LongFilter storageQuotaMb) {
        this.storageQuotaMb = storageQuotaMb;
    }

    public IntegerFilter getAiGenerationLimit() {
        return aiGenerationLimit;
    }

    public Optional<IntegerFilter> optionalAiGenerationLimit() {
        return Optional.ofNullable(aiGenerationLimit);
    }

    public IntegerFilter aiGenerationLimit() {
        if (aiGenerationLimit == null) {
            setAiGenerationLimit(new IntegerFilter());
        }
        return aiGenerationLimit;
    }

    public void setAiGenerationLimit(IntegerFilter aiGenerationLimit) {
        this.aiGenerationLimit = aiGenerationLimit;
    }

    public IntegerFilter getMaxClasses() {
        return maxClasses;
    }

    public Optional<IntegerFilter> optionalMaxClasses() {
        return Optional.ofNullable(maxClasses);
    }

    public IntegerFilter maxClasses() {
        if (maxClasses == null) {
            setMaxClasses(new IntegerFilter());
        }
        return maxClasses;
    }

    public void setMaxClasses(IntegerFilter maxClasses) {
        this.maxClasses = maxClasses;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SystemPlanCriteria that = (SystemPlanCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(price, that.price) &&
            Objects.equals(durationDays, that.durationDays) &&
            Objects.equals(storageQuotaMb, that.storageQuotaMb) &&
            Objects.equals(aiGenerationLimit, that.aiGenerationLimit) &&
            Objects.equals(maxClasses, that.maxClasses) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, durationDays, storageQuotaMb, aiGenerationLimit, maxClasses, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemPlanCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalPrice().map(f -> "price=" + f + ", ").orElse("") +
            optionalDurationDays().map(f -> "durationDays=" + f + ", ").orElse("") +
            optionalStorageQuotaMb().map(f -> "storageQuotaMb=" + f + ", ").orElse("") +
            optionalAiGenerationLimit().map(f -> "aiGenerationLimit=" + f + ", ").orElse("") +
            optionalMaxClasses().map(f -> "maxClasses=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

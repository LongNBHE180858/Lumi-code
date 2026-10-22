package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.AiMessage} entity. This class is used
 * in {@link com.lumi.web.rest.AiMessageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ai-messages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AiMessageCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter senderRole;

    private StringFilter content;

    private InstantFilter createdAt;

    private LongFilter conversationId;

    private Boolean distinct;

    public AiMessageCriteria() {}

    public AiMessageCriteria(AiMessageCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.senderRole = other.optionalSenderRole().map(StringFilter::copy).orElse(null);
        this.content = other.optionalContent().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.conversationId = other.optionalConversationId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AiMessageCriteria copy() {
        return new AiMessageCriteria(this);
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

    public StringFilter getSenderRole() {
        return senderRole;
    }

    public Optional<StringFilter> optionalSenderRole() {
        return Optional.ofNullable(senderRole);
    }

    public StringFilter senderRole() {
        if (senderRole == null) {
            setSenderRole(new StringFilter());
        }
        return senderRole;
    }

    public void setSenderRole(StringFilter senderRole) {
        this.senderRole = senderRole;
    }

    public StringFilter getContent() {
        return content;
    }

    public Optional<StringFilter> optionalContent() {
        return Optional.ofNullable(content);
    }

    public StringFilter content() {
        if (content == null) {
            setContent(new StringFilter());
        }
        return content;
    }

    public void setContent(StringFilter content) {
        this.content = content;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public LongFilter getConversationId() {
        return conversationId;
    }

    public Optional<LongFilter> optionalConversationId() {
        return Optional.ofNullable(conversationId);
    }

    public LongFilter conversationId() {
        if (conversationId == null) {
            setConversationId(new LongFilter());
        }
        return conversationId;
    }

    public void setConversationId(LongFilter conversationId) {
        this.conversationId = conversationId;
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
        final AiMessageCriteria that = (AiMessageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(senderRole, that.senderRole) &&
            Objects.equals(content, that.content) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(conversationId, that.conversationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, senderRole, content, createdAt, conversationId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AiMessageCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSenderRole().map(f -> "senderRole=" + f + ", ").orElse("") +
            optionalContent().map(f -> "content=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalConversationId().map(f -> "conversationId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

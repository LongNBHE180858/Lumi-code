package com.lumi.service.dto;

import com.lumi.domain.enumeration.FeedbackStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO for updating Feedback status.
 */
public class FeedbackStatusUpdateDTO implements Serializable {

    @NotNull
    private FeedbackStatus status;

    public FeedbackStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FeedbackStatusUpdateDTO{" +
            "status=" + status +
            '}';
    }
}

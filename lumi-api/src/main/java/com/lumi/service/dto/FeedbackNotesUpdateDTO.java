package com.lumi.service.dto;

import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for updating Feedback admin notes.
 */
public class FeedbackNotesUpdateDTO implements Serializable {

    @Size(max = 5000)
    private String adminNotes;

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    @Override
    public String toString() {
        return "FeedbackNotesUpdateDTO{" +
            "adminNotes='" + adminNotes + '\'' +
            '}';
    }
}

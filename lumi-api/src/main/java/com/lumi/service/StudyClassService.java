package com.lumi.service;

import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.service.dto.PageResponseDTO;
import com.lumi.service.dto.StudyClassDTO;
import com.lumi.service.dto.studyClass.CreateStudyClassRequestDTO;
import com.lumi.service.dto.studyClass.InstructorClassListItemDTO;
import com.lumi.service.dto.studyClass.StudyClassDetailDTO;
import com.lumi.service.dto.studyClass.UpdateStudyClassRequestDTO;
import java.util.List;

/**
 * Service Interface for managing {@link com.lumi.domain.StudyClass}.
 */
public interface StudyClassService {
    StudyClassDetailDTO createClass(CreateStudyClassRequestDTO request);
    StudyClassDetailDTO updateClass(long id, UpdateStudyClassRequestDTO request);
    PageResponseDTO<InstructorClassListItemDTO> listClass(int page, int size, String keyword, RecordStatus status);
    void deleteClass(long id);

    Long requireInstructorId();
}

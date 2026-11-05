package com.lumi.service;

import com.lumi.service.dto.StudyClassDTO;
import com.lumi.service.dto.studyClass.CreateStudyClassRequestDTO;
import com.lumi.service.dto.studyClass.StudyClassDetailDTO;
import com.lumi.service.dto.studyClass.UpdateStudyClassRequestDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.StudyClass}.
 */
public interface StudyClassService {
    StudyClassDTO save(StudyClassDTO studyClassDTO);
    StudyClassDTO update(StudyClassDTO studyClassDTO);
    Optional<StudyClassDTO> partialUpdate(StudyClassDTO studyClassDTO);
    Page<StudyClassDTO> findAllWithEagerRelationships(Pageable pageable);
    Optional<StudyClassDTO> findOne(Long id);
    void delete(Long id);

    StudyClassDetailDTO createClass(CreateStudyClassRequestDTO request);
    StudyClassDetailDTO updateClass(long id, UpdateStudyClassRequestDTO request);
    List<StudyClassDetailDTO> listClass();
}

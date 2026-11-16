package com.lumi.service;

import com.lumi.service.dto.SubjectDTO;
import java.util.Optional;

public interface SubjectService {
    Optional<SubjectDTO> findOne(Long id);
}

package com.lumi.service;

import com.lumi.service.dto.GradeDTO;
import java.util.Optional;

public interface GradeService {
    Optional<GradeDTO> findOne(Long id);
}

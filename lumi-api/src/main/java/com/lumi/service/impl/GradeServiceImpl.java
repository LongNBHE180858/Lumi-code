package com.lumi.service.impl;

import com.lumi.repository.GradeRepository;
import com.lumi.service.GradeService;
import com.lumi.service.dto.GradeDTO;
import com.lumi.service.mapper.GradeMapper;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final GradeMapper gradeMapper;

    public GradeServiceImpl(GradeRepository gradeRepository, GradeMapper gradeMapper) {
        this.gradeRepository = gradeRepository;
        this.gradeMapper = gradeMapper;
    }

    @Override
    public Optional<GradeDTO> findOne(Long id) {
        return gradeRepository.findById(id).map(gradeMapper::toDto);
    }
}

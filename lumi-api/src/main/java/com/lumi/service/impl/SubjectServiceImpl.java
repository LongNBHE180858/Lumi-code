package com.lumi.service.impl;

import com.lumi.repository.SubjectRepository;
import com.lumi.service.SubjectService;
import com.lumi.service.dto.SubjectDTO;
import com.lumi.service.mapper.SubjectMapper;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    public SubjectServiceImpl(SubjectRepository subjectRepository, SubjectMapper subjectMapper) {
        this.subjectRepository = subjectRepository;
        this.subjectMapper = subjectMapper;
    }

    @Override
    public Optional<SubjectDTO> findOne(Long id) {
        return subjectRepository.findById(id).map(subjectMapper::toDto);
    }
}

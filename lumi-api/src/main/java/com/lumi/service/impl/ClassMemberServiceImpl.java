package com.lumi.service.impl;

import com.lumi.domain.ClassMember;
import com.lumi.repository.ClassMemberRepository;
import com.lumi.service.ClassMemberService;
import com.lumi.service.dto.ClassMemberDTO;
import com.lumi.service.mapper.ClassMemberMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.ClassMember}.
 */
@Service
@Transactional
public class ClassMemberServiceImpl implements ClassMemberService {

    private static final Logger LOG = LoggerFactory.getLogger(ClassMemberServiceImpl.class);

    private final ClassMemberRepository classMemberRepository;

    private final ClassMemberMapper classMemberMapper;

    public ClassMemberServiceImpl(ClassMemberRepository classMemberRepository, ClassMemberMapper classMemberMapper) {
        this.classMemberRepository = classMemberRepository;
        this.classMemberMapper = classMemberMapper;
    }

    @Override
    public ClassMemberDTO save(ClassMemberDTO classMemberDTO) {
        LOG.debug("Request to save ClassMember : {}", classMemberDTO);
        ClassMember classMember = classMemberMapper.toEntity(classMemberDTO);
        classMember = classMemberRepository.save(classMember);
        return classMemberMapper.toDto(classMember);
    }

    @Override
    public ClassMemberDTO update(ClassMemberDTO classMemberDTO) {
        LOG.debug("Request to update ClassMember : {}", classMemberDTO);
        ClassMember classMember = classMemberMapper.toEntity(classMemberDTO);
        classMember = classMemberRepository.save(classMember);
        return classMemberMapper.toDto(classMember);
    }

    @Override
    public Optional<ClassMemberDTO> partialUpdate(ClassMemberDTO classMemberDTO) {
        LOG.debug("Request to partially update ClassMember : {}", classMemberDTO);

        return classMemberRepository
            .findById(classMemberDTO.getId())
            .map(existingClassMember -> {
                classMemberMapper.partialUpdate(existingClassMember, classMemberDTO);

                return existingClassMember;
            })
            .map(classMemberRepository::save)
            .map(classMemberMapper::toDto);
    }

    public Page<ClassMemberDTO> findAllWithEagerRelationships(Pageable pageable) {
        return classMemberRepository.findAllWithEagerRelationships(pageable).map(classMemberMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClassMemberDTO> findOne(Long id) {
        LOG.debug("Request to get ClassMember : {}", id);
        return classMemberRepository.findOneWithEagerRelationships(id).map(classMemberMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ClassMember : {}", id);
        classMemberRepository.deleteById(id);
    }
}

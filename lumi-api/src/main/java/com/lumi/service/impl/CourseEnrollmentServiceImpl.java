package com.lumi.service.impl;

import com.lumi.domain.CourseEnrollment;
import com.lumi.repository.CourseEnrollmentRepository;
import com.lumi.service.CourseEnrollmentService;
import com.lumi.service.dto.CourseEnrollmentDTO;
import com.lumi.service.mapper.CourseEnrollmentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.CourseEnrollment}.
 */
@Service
@Transactional
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {

    private final jakarta.persistence.EntityManager entityManager;

    private static final Logger LOG = LoggerFactory.getLogger(CourseEnrollmentServiceImpl.class);

    private final CourseEnrollmentRepository courseEnrollmentRepository;

    private final CourseEnrollmentMapper courseEnrollmentMapper;

    public CourseEnrollmentServiceImpl(
        CourseEnrollmentRepository courseEnrollmentRepository,
        CourseEnrollmentMapper courseEnrollmentMapper,
        jakarta.persistence.EntityManager entityManager
    ) {
        this.entityManager = entityManager;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.courseEnrollmentMapper = courseEnrollmentMapper;
    }

    @Override
    public CourseEnrollmentDTO save(CourseEnrollmentDTO courseEnrollmentDTO) {
        LOG.debug("Request to save CourseEnrollment : {}", courseEnrollmentDTO);
        CourseEnrollment courseEnrollment = courseEnrollmentMapper.toEntity(courseEnrollmentDTO);
        if (courseEnrollmentDTO.getCourse() != null && courseEnrollmentDTO.getCourse().getId() != null) {
            courseEnrollment.setCourse(entityManager.getReference(com.lumi.domain.Course.class, courseEnrollmentDTO.getCourse().getId()));
        }
        courseEnrollment = courseEnrollmentRepository.save(courseEnrollment);
        return courseEnrollmentMapper.toDto(courseEnrollment);
    }

    @Override
    public CourseEnrollmentDTO update(CourseEnrollmentDTO courseEnrollmentDTO) {
        LOG.debug("Request to update CourseEnrollment : {}", courseEnrollmentDTO);
        CourseEnrollment courseEnrollment = courseEnrollmentMapper.toEntity(courseEnrollmentDTO);
        if (courseEnrollmentDTO.getCourse() != null && courseEnrollmentDTO.getCourse().getId() != null) {
            courseEnrollment.setCourse(entityManager.getReference(com.lumi.domain.Course.class, courseEnrollmentDTO.getCourse().getId()));
        }
        courseEnrollment = courseEnrollmentRepository.save(courseEnrollment);
        return courseEnrollmentMapper.toDto(courseEnrollment);
    }

    @Override
    public Optional<CourseEnrollmentDTO> partialUpdate(CourseEnrollmentDTO courseEnrollmentDTO) {
        LOG.debug("Request to partially update CourseEnrollment : {}", courseEnrollmentDTO);

        return courseEnrollmentRepository
            .findById(courseEnrollmentDTO.getId())
            .map(existingCourseEnrollment -> {
                courseEnrollmentMapper.partialUpdate(existingCourseEnrollment, courseEnrollmentDTO);
                if (courseEnrollmentDTO.getCourse() != null && courseEnrollmentDTO.getCourse().getId() != null) {
                    existingCourseEnrollment.setCourse(
                        entityManager.getReference(com.lumi.domain.Course.class, courseEnrollmentDTO.getCourse().getId())
                    );
                }

                return existingCourseEnrollment;
            })
            .map(courseEnrollmentRepository::save)
            .map(courseEnrollmentMapper::toDto);
    }

    public Page<CourseEnrollmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseEnrollmentRepository.findAllWithEagerRelationships(pageable).map(courseEnrollmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseEnrollmentDTO> findOne(Long id) {
        LOG.debug("Request to get CourseEnrollment : {}", id);
        return courseEnrollmentRepository.findOneWithEagerRelationships(id).map(courseEnrollmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CourseEnrollment : {}", id);
        courseEnrollmentRepository.deleteById(id);
    }
}

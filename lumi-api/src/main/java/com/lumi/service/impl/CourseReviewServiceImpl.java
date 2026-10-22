package com.lumi.service.impl;

import com.lumi.domain.CourseReview;
import com.lumi.repository.CourseReviewRepository;
import com.lumi.service.CourseReviewService;
import com.lumi.service.dto.CourseReviewDTO;
import com.lumi.service.mapper.CourseReviewMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.CourseReview}.
 */
@Service
@Transactional
public class CourseReviewServiceImpl implements CourseReviewService {

    private final jakarta.persistence.EntityManager entityManager;

    private static final Logger LOG = LoggerFactory.getLogger(CourseReviewServiceImpl.class);

    private final CourseReviewRepository courseReviewRepository;

    private final CourseReviewMapper courseReviewMapper;

    public CourseReviewServiceImpl(
        CourseReviewRepository courseReviewRepository,
        CourseReviewMapper courseReviewMapper,
        jakarta.persistence.EntityManager entityManager
    ) {
        this.entityManager = entityManager;
        this.courseReviewRepository = courseReviewRepository;
        this.courseReviewMapper = courseReviewMapper;
    }

    @Override
    public CourseReviewDTO save(CourseReviewDTO courseReviewDTO) {
        LOG.debug("Request to save CourseReview : {}", courseReviewDTO);
        CourseReview courseReview = courseReviewMapper.toEntity(courseReviewDTO);
        if (courseReviewDTO.getCourse() != null && courseReviewDTO.getCourse().getId() != null) {
            courseReview.setCourse(entityManager.getReference(com.lumi.domain.Course.class, courseReviewDTO.getCourse().getId()));
        }
        courseReview = courseReviewRepository.save(courseReview);
        return courseReviewMapper.toDto(courseReview);
    }

    @Override
    public CourseReviewDTO update(CourseReviewDTO courseReviewDTO) {
        LOG.debug("Request to update CourseReview : {}", courseReviewDTO);
        CourseReview courseReview = courseReviewMapper.toEntity(courseReviewDTO);
        if (courseReviewDTO.getCourse() != null && courseReviewDTO.getCourse().getId() != null) {
            courseReview.setCourse(entityManager.getReference(com.lumi.domain.Course.class, courseReviewDTO.getCourse().getId()));
        }
        courseReview = courseReviewRepository.save(courseReview);
        return courseReviewMapper.toDto(courseReview);
    }

    @Override
    public Optional<CourseReviewDTO> partialUpdate(CourseReviewDTO courseReviewDTO) {
        LOG.debug("Request to partially update CourseReview : {}", courseReviewDTO);

        return courseReviewRepository
            .findById(courseReviewDTO.getId())
            .map(existingCourseReview -> {
                courseReviewMapper.partialUpdate(existingCourseReview, courseReviewDTO);
                if (courseReviewDTO.getCourse() != null && courseReviewDTO.getCourse().getId() != null) {
                    existingCourseReview.setCourse(
                        entityManager.getReference(com.lumi.domain.Course.class, courseReviewDTO.getCourse().getId())
                    );
                }

                return existingCourseReview;
            })
            .map(courseReviewRepository::save)
            .map(courseReviewMapper::toDto);
    }

    public Page<CourseReviewDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseReviewRepository.findAllWithEagerRelationships(pageable).map(courseReviewMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseReviewDTO> findOne(Long id) {
        LOG.debug("Request to get CourseReview : {}", id);
        return courseReviewRepository.findOneWithEagerRelationships(id).map(courseReviewMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CourseReview : {}", id);
        courseReviewRepository.deleteById(id);
    }
}

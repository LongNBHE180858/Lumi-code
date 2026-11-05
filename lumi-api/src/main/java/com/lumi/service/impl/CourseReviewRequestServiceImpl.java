package com.lumi.service.impl;

import com.lumi.domain.CourseReviewRequest;
import com.lumi.repository.CourseReviewRequestRepository;
import com.lumi.service.CourseReviewRequestService;
import com.lumi.service.dto.CourseReviewRequestDTO;
import com.lumi.service.mapper.CourseReviewRequestMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.CourseReviewRequest}.
 */
@Service
@Transactional
public class CourseReviewRequestServiceImpl implements CourseReviewRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseReviewRequestServiceImpl.class);

    private final CourseReviewRequestRepository courseReviewRequestRepository;

    private final CourseReviewRequestMapper courseReviewRequestMapper;

    public CourseReviewRequestServiceImpl(
        CourseReviewRequestRepository courseReviewRequestRepository,
        CourseReviewRequestMapper courseReviewRequestMapper
    ) {
        this.courseReviewRequestRepository = courseReviewRequestRepository;
        this.courseReviewRequestMapper = courseReviewRequestMapper;
    }

    @Override
    public CourseReviewRequestDTO save(CourseReviewRequestDTO courseReviewRequestDTO) {
        LOG.debug("Request to save CourseReviewRequest : {}", courseReviewRequestDTO);
        CourseReviewRequest courseReviewRequest = courseReviewRequestMapper.toEntity(courseReviewRequestDTO);
        courseReviewRequest = courseReviewRequestRepository.save(courseReviewRequest);
        return courseReviewRequestMapper.toDto(courseReviewRequest);
    }

    @Override
    public CourseReviewRequestDTO update(CourseReviewRequestDTO courseReviewRequestDTO) {
        LOG.debug("Request to update CourseReviewRequest : {}", courseReviewRequestDTO);
        CourseReviewRequest courseReviewRequest = courseReviewRequestMapper.toEntity(courseReviewRequestDTO);
        courseReviewRequest = courseReviewRequestRepository.save(courseReviewRequest);
        return courseReviewRequestMapper.toDto(courseReviewRequest);
    }

    @Override
    public Optional<CourseReviewRequestDTO> partialUpdate(CourseReviewRequestDTO courseReviewRequestDTO) {
        LOG.debug("Request to partially update CourseReviewRequest : {}", courseReviewRequestDTO);

        return courseReviewRequestRepository
            .findById(courseReviewRequestDTO.getId())
            .map(existingCourseReviewRequest -> {
                courseReviewRequestMapper.partialUpdate(existingCourseReviewRequest, courseReviewRequestDTO);

                return existingCourseReviewRequest;
            })
            .map(courseReviewRequestRepository::save)
            .map(courseReviewRequestMapper::toDto);
    }

    public Page<CourseReviewRequestDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseReviewRequestRepository.findAllWithEagerRelationships(pageable).map(courseReviewRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseReviewRequestDTO> findOne(Long id) {
        LOG.debug("Request to get CourseReviewRequest : {}", id);
        return courseReviewRequestRepository.findOneWithEagerRelationships(id).map(courseReviewRequestMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CourseReviewRequest : {}", id);
        courseReviewRequestRepository.deleteById(id);
    }
}

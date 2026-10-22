package com.lumi.service.impl;

import com.lumi.domain.CourseOrder;
import com.lumi.repository.CourseOrderRepository;
import com.lumi.service.CourseOrderService;
import com.lumi.service.dto.CourseOrderDTO;
import com.lumi.service.mapper.CourseOrderMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.CourseOrder}.
 */
@Service
@Transactional
public class CourseOrderServiceImpl implements CourseOrderService {

    private final jakarta.persistence.EntityManager entityManager;

    private static final Logger LOG = LoggerFactory.getLogger(CourseOrderServiceImpl.class);

    private final CourseOrderRepository courseOrderRepository;

    private final CourseOrderMapper courseOrderMapper;

    public CourseOrderServiceImpl(
        CourseOrderRepository courseOrderRepository,
        CourseOrderMapper courseOrderMapper,
        jakarta.persistence.EntityManager entityManager
    ) {
        this.entityManager = entityManager;
        this.courseOrderRepository = courseOrderRepository;
        this.courseOrderMapper = courseOrderMapper;
    }

    @Override
    public CourseOrderDTO save(CourseOrderDTO courseOrderDTO) {
        LOG.debug("Request to save CourseOrder : {}", courseOrderDTO);
        CourseOrder courseOrder = courseOrderMapper.toEntity(courseOrderDTO);
        if (courseOrderDTO.getCourse() != null && courseOrderDTO.getCourse().getId() != null) {
            courseOrder.setCourse(entityManager.getReference(com.lumi.domain.Course.class, courseOrderDTO.getCourse().getId()));
        }
        courseOrder = courseOrderRepository.save(courseOrder);
        return courseOrderMapper.toDto(courseOrder);
    }

    @Override
    public CourseOrderDTO update(CourseOrderDTO courseOrderDTO) {
        LOG.debug("Request to update CourseOrder : {}", courseOrderDTO);
        CourseOrder courseOrder = courseOrderMapper.toEntity(courseOrderDTO);
        if (courseOrderDTO.getCourse() != null && courseOrderDTO.getCourse().getId() != null) {
            courseOrder.setCourse(entityManager.getReference(com.lumi.domain.Course.class, courseOrderDTO.getCourse().getId()));
        }
        courseOrder = courseOrderRepository.save(courseOrder);
        return courseOrderMapper.toDto(courseOrder);
    }

    @Override
    public Optional<CourseOrderDTO> partialUpdate(CourseOrderDTO courseOrderDTO) {
        LOG.debug("Request to partially update CourseOrder : {}", courseOrderDTO);

        return courseOrderRepository
            .findById(courseOrderDTO.getId())
            .map(existingCourseOrder -> {
                courseOrderMapper.partialUpdate(existingCourseOrder, courseOrderDTO);
                if (courseOrderDTO.getCourse() != null && courseOrderDTO.getCourse().getId() != null) {
                    existingCourseOrder.setCourse(
                        entityManager.getReference(com.lumi.domain.Course.class, courseOrderDTO.getCourse().getId())
                    );
                }

                return existingCourseOrder;
            })
            .map(courseOrderRepository::save)
            .map(courseOrderMapper::toDto);
    }

    public Page<CourseOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseOrderRepository.findAllWithEagerRelationships(pageable).map(courseOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseOrderDTO> findOne(Long id) {
        LOG.debug("Request to get CourseOrder : {}", id);
        return courseOrderRepository.findOneWithEagerRelationships(id).map(courseOrderMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CourseOrder : {}", id);
        courseOrderRepository.deleteById(id);
    }
}

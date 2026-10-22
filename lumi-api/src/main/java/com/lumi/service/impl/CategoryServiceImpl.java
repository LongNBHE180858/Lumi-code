package com.lumi.service.impl;

import com.lumi.domain.Category;
import com.lumi.repository.CategoryRepository;
import com.lumi.service.CategoryService;
import com.lumi.service.dto.CategoryDTO;
import com.lumi.service.mapper.CategoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.Category}.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryDTO save(CategoryDTO categoryDTO) {
        LOG.debug("Request to save Category : {}", categoryDTO);
        Category category = categoryMapper.toEntity(categoryDTO);
        if (category.getActive() == null) category.setActive(true);
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDTO update(CategoryDTO categoryDTO) {
        LOG.debug("Request to update Category : {}", categoryDTO);
        Category category = categoryMapper.toEntity(categoryDTO);
        if (category.getActive() == null) category.setActive(
            categoryRepository.findById(categoryDTO.getId()).map(Category::getActive).orElse(true)
        );
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public Optional<CategoryDTO> partialUpdate(CategoryDTO categoryDTO) {
        LOG.debug("Request to partially update Category : {}", categoryDTO);

        return categoryRepository
            .findById(categoryDTO.getId())
            .map(existingCategory -> {
                categoryMapper.partialUpdate(existingCategory, categoryDTO);

                return existingCategory;
            })
            .map(categoryRepository::save)
            .map(categoryMapper::toDto);
    }

    public Page<CategoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return categoryRepository.findAllWithEagerRelationships(pageable).map(categoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> findOne(Long id) {
        LOG.debug("Request to get Category : {}", id);
        return categoryRepository.findOneWithEagerRelationships(id).map(categoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Category : {}", id);
        categoryRepository.findById(id).ifPresent(category -> category.setActive(false));
    }
}

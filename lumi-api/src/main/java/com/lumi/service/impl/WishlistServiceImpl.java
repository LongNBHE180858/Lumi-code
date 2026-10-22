package com.lumi.service.impl;

import com.lumi.domain.Wishlist;
import com.lumi.repository.WishlistRepository;
import com.lumi.service.WishlistService;
import com.lumi.service.dto.WishlistDTO;
import com.lumi.service.mapper.WishlistMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.Wishlist}.
 */
@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final jakarta.persistence.EntityManager entityManager;

    private static final Logger LOG = LoggerFactory.getLogger(WishlistServiceImpl.class);

    private final WishlistRepository wishlistRepository;

    private final WishlistMapper wishlistMapper;

    public WishlistServiceImpl(
        WishlistRepository wishlistRepository,
        WishlistMapper wishlistMapper,
        jakarta.persistence.EntityManager entityManager
    ) {
        this.entityManager = entityManager;
        this.wishlistRepository = wishlistRepository;
        this.wishlistMapper = wishlistMapper;
    }

    @Override
    public WishlistDTO save(WishlistDTO wishlistDTO) {
        LOG.debug("Request to save Wishlist : {}", wishlistDTO);
        Wishlist wishlist = wishlistMapper.toEntity(wishlistDTO);
        if (wishlistDTO.getCourse() != null && wishlistDTO.getCourse().getId() != null) {
            wishlist.setCourse(entityManager.getReference(com.lumi.domain.Course.class, wishlistDTO.getCourse().getId()));
        }
        wishlist = wishlistRepository.save(wishlist);
        return wishlistMapper.toDto(wishlist);
    }

    @Override
    public WishlistDTO update(WishlistDTO wishlistDTO) {
        LOG.debug("Request to update Wishlist : {}", wishlistDTO);
        Wishlist wishlist = wishlistMapper.toEntity(wishlistDTO);
        if (wishlistDTO.getCourse() != null && wishlistDTO.getCourse().getId() != null) {
            wishlist.setCourse(entityManager.getReference(com.lumi.domain.Course.class, wishlistDTO.getCourse().getId()));
        }
        wishlist = wishlistRepository.save(wishlist);
        return wishlistMapper.toDto(wishlist);
    }

    @Override
    public Optional<WishlistDTO> partialUpdate(WishlistDTO wishlistDTO) {
        LOG.debug("Request to partially update Wishlist : {}", wishlistDTO);

        return wishlistRepository
            .findById(wishlistDTO.getId())
            .map(existingWishlist -> {
                wishlistMapper.partialUpdate(existingWishlist, wishlistDTO);
                if (wishlistDTO.getCourse() != null && wishlistDTO.getCourse().getId() != null) {
                    existingWishlist.setCourse(entityManager.getReference(com.lumi.domain.Course.class, wishlistDTO.getCourse().getId()));
                }

                return existingWishlist;
            })
            .map(wishlistRepository::save)
            .map(wishlistMapper::toDto);
    }

    public List<WishlistDTO> findAllWithEagerRelationships() {
        return wishlistRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(wishlistMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WishlistDTO> findOne(Long id) {
        LOG.debug("Request to get Wishlist : {}", id);
        return wishlistRepository.findOneWithEagerRelationships(id).map(wishlistMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Wishlist : {}", id);
        wishlistRepository.deleteById(id);
    }
}

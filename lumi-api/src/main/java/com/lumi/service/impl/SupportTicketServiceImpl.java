package com.lumi.service.impl;

import com.lumi.domain.SupportTicket;
import com.lumi.repository.SupportTicketRepository;
import com.lumi.service.SupportTicketService;
import com.lumi.service.dto.SupportTicketDTO;
import com.lumi.service.mapper.SupportTicketMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.SupportTicket}.
 */
@Service
@Transactional
public class SupportTicketServiceImpl implements SupportTicketService {

    private static final Logger LOG = LoggerFactory.getLogger(SupportTicketServiceImpl.class);

    private final SupportTicketRepository supportTicketRepository;

    private final SupportTicketMapper supportTicketMapper;

    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository, SupportTicketMapper supportTicketMapper) {
        this.supportTicketRepository = supportTicketRepository;
        this.supportTicketMapper = supportTicketMapper;
    }

    @Override
    public SupportTicketDTO save(SupportTicketDTO supportTicketDTO) {
        LOG.debug("Request to save SupportTicket : {}", supportTicketDTO);
        SupportTicket supportTicket = supportTicketMapper.toEntity(supportTicketDTO);
        supportTicket = supportTicketRepository.save(supportTicket);
        return supportTicketMapper.toDto(supportTicket);
    }

    @Override
    public SupportTicketDTO update(SupportTicketDTO supportTicketDTO) {
        LOG.debug("Request to update SupportTicket : {}", supportTicketDTO);
        SupportTicket supportTicket = supportTicketMapper.toEntity(supportTicketDTO);
        supportTicket = supportTicketRepository.save(supportTicket);
        return supportTicketMapper.toDto(supportTicket);
    }

    @Override
    public Optional<SupportTicketDTO> partialUpdate(SupportTicketDTO supportTicketDTO) {
        LOG.debug("Request to partially update SupportTicket : {}", supportTicketDTO);

        return supportTicketRepository
            .findById(supportTicketDTO.getId())
            .map(existingSupportTicket -> {
                supportTicketMapper.partialUpdate(existingSupportTicket, supportTicketDTO);

                return existingSupportTicket;
            })
            .map(supportTicketRepository::save)
            .map(supportTicketMapper::toDto);
    }

    public Page<SupportTicketDTO> findAllWithEagerRelationships(Pageable pageable) {
        return supportTicketRepository.findAllWithEagerRelationships(pageable).map(supportTicketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupportTicketDTO> findOne(Long id) {
        LOG.debug("Request to get SupportTicket : {}", id);
        return supportTicketRepository.findOneWithEagerRelationships(id).map(supportTicketMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SupportTicket : {}", id);
        supportTicketRepository.deleteById(id);
    }
}

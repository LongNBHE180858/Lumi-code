package com.lumi.service.impl;

import com.lumi.domain.TicketMessage;
import com.lumi.repository.TicketMessageRepository;
import com.lumi.service.TicketMessageService;
import com.lumi.service.dto.TicketMessageDTO;
import com.lumi.service.mapper.TicketMessageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.TicketMessage}.
 */
@Service
@Transactional
public class TicketMessageServiceImpl implements TicketMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketMessageServiceImpl.class);

    private final TicketMessageRepository ticketMessageRepository;

    private final TicketMessageMapper ticketMessageMapper;

    public TicketMessageServiceImpl(TicketMessageRepository ticketMessageRepository, TicketMessageMapper ticketMessageMapper) {
        this.ticketMessageRepository = ticketMessageRepository;
        this.ticketMessageMapper = ticketMessageMapper;
    }

    @Override
    public TicketMessageDTO save(TicketMessageDTO ticketMessageDTO) {
        LOG.debug("Request to save TicketMessage : {}", ticketMessageDTO);
        TicketMessage ticketMessage = ticketMessageMapper.toEntity(ticketMessageDTO);
        ticketMessage = ticketMessageRepository.save(ticketMessage);
        return ticketMessageMapper.toDto(ticketMessage);
    }

    @Override
    public TicketMessageDTO update(TicketMessageDTO ticketMessageDTO) {
        LOG.debug("Request to update TicketMessage : {}", ticketMessageDTO);
        TicketMessage ticketMessage = ticketMessageMapper.toEntity(ticketMessageDTO);
        ticketMessage = ticketMessageRepository.save(ticketMessage);
        return ticketMessageMapper.toDto(ticketMessage);
    }

    @Override
    public Optional<TicketMessageDTO> partialUpdate(TicketMessageDTO ticketMessageDTO) {
        LOG.debug("Request to partially update TicketMessage : {}", ticketMessageDTO);

        return ticketMessageRepository
            .findById(ticketMessageDTO.getId())
            .map(existingTicketMessage -> {
                ticketMessageMapper.partialUpdate(existingTicketMessage, ticketMessageDTO);

                return existingTicketMessage;
            })
            .map(ticketMessageRepository::save)
            .map(ticketMessageMapper::toDto);
    }

    public Page<TicketMessageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ticketMessageRepository.findAllWithEagerRelationships(pageable).map(ticketMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketMessageDTO> findOne(Long id) {
        LOG.debug("Request to get TicketMessage : {}", id);
        return ticketMessageRepository.findOneWithEagerRelationships(id).map(ticketMessageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TicketMessage : {}", id);
        ticketMessageRepository.deleteById(id);
    }
}

package com.lumi.service.impl;

import com.lumi.domain.PayoutRequest;
import com.lumi.repository.PayoutRequestRepository;
import com.lumi.service.PayoutRequestService;
import com.lumi.service.dto.PayoutRequestDTO;
import com.lumi.service.mapper.PayoutRequestMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.PayoutRequest}.
 */
@Service
@Transactional
public class PayoutRequestServiceImpl implements PayoutRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(PayoutRequestServiceImpl.class);

    private final PayoutRequestRepository payoutRequestRepository;

    private final PayoutRequestMapper payoutRequestMapper;

    public PayoutRequestServiceImpl(PayoutRequestRepository payoutRequestRepository, PayoutRequestMapper payoutRequestMapper) {
        this.payoutRequestRepository = payoutRequestRepository;
        this.payoutRequestMapper = payoutRequestMapper;
    }

    @Override
    public PayoutRequestDTO save(PayoutRequestDTO payoutRequestDTO) {
        LOG.debug("Request to save PayoutRequest : {}", payoutRequestDTO);
        PayoutRequest payoutRequest = payoutRequestMapper.toEntity(payoutRequestDTO);
        payoutRequest = payoutRequestRepository.save(payoutRequest);
        return payoutRequestMapper.toDto(payoutRequest);
    }

    @Override
    public PayoutRequestDTO update(PayoutRequestDTO payoutRequestDTO) {
        LOG.debug("Request to update PayoutRequest : {}", payoutRequestDTO);
        PayoutRequest payoutRequest = payoutRequestMapper.toEntity(payoutRequestDTO);
        payoutRequest = payoutRequestRepository.save(payoutRequest);
        return payoutRequestMapper.toDto(payoutRequest);
    }

    @Override
    public Optional<PayoutRequestDTO> partialUpdate(PayoutRequestDTO payoutRequestDTO) {
        LOG.debug("Request to partially update PayoutRequest : {}", payoutRequestDTO);

        return payoutRequestRepository
            .findById(payoutRequestDTO.getId())
            .map(existingPayoutRequest -> {
                payoutRequestMapper.partialUpdate(existingPayoutRequest, payoutRequestDTO);

                return existingPayoutRequest;
            })
            .map(payoutRequestRepository::save)
            .map(payoutRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PayoutRequestDTO> findOne(Long id) {
        LOG.debug("Request to get PayoutRequest : {}", id);
        return payoutRequestRepository.findById(id).map(payoutRequestMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PayoutRequest : {}", id);
        payoutRequestRepository.deleteById(id);
    }
}

package com.lumi.service;

import com.lumi.domain.SupportTicket;
import com.lumi.domain.TicketMessage;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.SupportTicketStatus;
import com.lumi.repository.SupportTicketRepository;
import com.lumi.repository.TicketMessageRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.security.SecurityUtils;
import com.lumi.service.dto.SupportTicketCreateRequest;
import com.lumi.service.dto.SupportTicketViewDTO;
import com.lumi.service.dto.TicketMessageCreateRequest;
import com.lumi.service.dto.TicketMessageViewDTO;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class SupportTicketManagementService {

    private final SupportTicketRepository ticketRepository;
    private final TicketMessageRepository messageRepository;
    private final UserRepository userRepository;

    public SupportTicketManagementService(
        SupportTicketRepository ticketRepository,
        TicketMessageRepository messageRepository,
        UserRepository userRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public SupportTicketViewDTO create(SupportTicketCreateRequest request) {
        User user = currentUser();
        SupportTicket ticket = new SupportTicket()
            .type(request.type().trim())
            .subject(request.subject().trim())
            .status(SupportTicketStatus.OPEN.name())
            .createdAt(Instant.now())
            .user(user);
        ticket = ticketRepository.save(ticket);
        TicketMessage message = new TicketMessage()
            .content(request.content().trim())
            .createdAt(ticket.getCreatedAt())
            .sender(user)
            .ticket(ticket);
        messageRepository.save(message);
        return toTicketView(ticket);
    }

    @Transactional(readOnly = true)
    public Page<SupportTicketViewDTO> findMine(Pageable pageable) {
        return ticketRepository.findByUserId(currentUser().getId(), pageable).map(this::toTicketView);
    }

    @Transactional(readOnly = true)
    public Page<SupportTicketViewDTO> findAllForAdmin(SupportTicketStatus status, String type, String search, Pageable pageable) {
        requireAdmin();
        String normalizedType = type == null || type.isBlank() ? null : type.trim();
        String normalizedSearch = search == null || search.isBlank() ? null : "%" + search.trim().toLowerCase() + "%";
        return ticketRepository
            .findAdminPage(status == null ? null : status.name(), normalizedType, normalizedSearch, pageable)
            .map(this::toTicketView);
    }

    @Transactional(readOnly = true)
    public SupportTicketViewDTO findOne(Long id) {
        User user = currentUser();
        SupportTicket ticket = ticketRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireAccess(ticket, user);
        return toTicketView(ticket);
    }

    @Transactional(readOnly = true)
    public Page<TicketMessageViewDTO> findMessages(Long id, Pageable pageable) {
        User user = currentUser();
        SupportTicket ticket = ticketRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireAccess(ticket, user);
        return messageRepository.findByTicketIdOrderByCreatedAtAscIdAsc(id, pageable).map(this::toMessageView);
    }

    public TicketMessageViewDTO addMessage(Long id, TicketMessageCreateRequest request) {
        User user = currentUser();
        SupportTicket ticket = ticketRepository.findByIdForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireAccess(ticket, user);
        if (
            (!SupportTicketStatus.OPEN.name().equals(ticket.getStatus()) &&
                !SupportTicketStatus.IN_PROGRESS.name().equals(ticket.getStatus())) ||
            (SupportTicketStatus.OPEN.name().equals(ticket.getStatus()) && isAdmin())
        ) {
            ticket.setStatus(SupportTicketStatus.IN_PROGRESS.name());
        }
        TicketMessage message = new TicketMessage().content(request.content().trim()).createdAt(Instant.now()).sender(user).ticket(ticket);
        return toMessageView(messageRepository.save(message));
    }

    public SupportTicketViewDTO reopen(Long id) {
        User user = currentUser();
        SupportTicket ticket = ticketRepository.findByIdForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireAccess(ticket, user);
        if (!SupportTicketStatus.RESOLVED.name().equals(ticket.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only resolved tickets can be reopened");
        }
        ticket.setStatus(SupportTicketStatus.IN_PROGRESS.name());
        return toTicketView(ticket);
    }

    public SupportTicketViewDTO changeStatus(Long id, SupportTicketStatus status) {
        requireAdmin();
        if (status == SupportTicketStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OPEN is only used for new tickets");
        }
        SupportTicket ticket = ticketRepository.findByIdForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ticket.setStatus(status.name());
        return toTicketView(ticket);
    }

    private User currentUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return userRepository.findOneByLogin(login).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private void requireAccess(SupportTicket ticket, User user) {
        if (!isAdmin() && !ticket.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private void requireAdmin() {
        if (!isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private boolean isAdmin() {
        return SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN);
    }

    private SupportTicketViewDTO toTicketView(SupportTicket ticket) {
        return new SupportTicketViewDTO(
            ticket.getId(),
            ticket.getType(),
            ticket.getSubject(),
            ticket.getStatus(),
            ticket.getCreatedAt(),
            ticket.getUser().getId(),
            ticket.getUser().getLogin()
        );
    }

    private TicketMessageViewDTO toMessageView(TicketMessage message) {
        return new TicketMessageViewDTO(
            message.getId(),
            message.getContent(),
            message.getCreatedAt(),
            message.getSender().getId(),
            message.getSender().getLogin()
        );
    }
}

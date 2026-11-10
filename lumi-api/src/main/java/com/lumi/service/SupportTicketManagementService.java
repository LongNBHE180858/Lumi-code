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
import com.lumi.service.dto.TicketImageDTO;
import com.lumi.service.dto.TicketMessageCreateRequest;
import com.lumi.service.dto.TicketMessageContent;
import com.lumi.service.dto.TicketMessageViewDTO;
import com.lumi.service.storage.CourseFileValidator;
import com.lumi.service.storage.R2CourseObjectStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class SupportTicketManagementService {

    private static final String IMAGE_FORMAT = "lumi.ticket-message.v1";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final SupportTicketRepository ticketRepository;
    private final TicketMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CourseFileValidator fileValidator;
    private final R2CourseObjectStorage imageStorage;

    public SupportTicketManagementService(
        SupportTicketRepository ticketRepository,
        TicketMessageRepository messageRepository,
        UserRepository userRepository,
        CourseFileValidator fileValidator,
        R2CourseObjectStorage imageStorage
    ) {
        this.ticketRepository = ticketRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.fileValidator = fileValidator;
        this.imageStorage = imageStorage;
    }

    public SupportTicketViewDTO create(SupportTicketCreateRequest request) {
        return createWithImages(request, List.of());
    }

    public SupportTicketViewDTO createWithImages(SupportTicketCreateRequest request, List<MultipartFile> images) {
        if (
            request.type() == null || request.type().isBlank() || request.type().trim().length() > 50 ||
            request.subject() == null || request.subject().isBlank() || request.subject().trim().length() > 255
        ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ticket details");
        validateText(request.content(), true);
        validateImages(images);
        User user = currentUser();
        Instant now = Instant.now();
        SupportTicket ticket = new SupportTicket()
            .type(request.type().trim())
            .subject(request.subject().trim())
            .status(SupportTicketStatus.OPEN.name())
            .createdAt(now)
            .updatedAt(now)
            .user(user);
        ticket = ticketRepository.save(ticket);
        TicketMessage message = new TicketMessage()
            .content(encodeContent(request.content().trim(), uploadImages(ticket.getId(), images)))
            .createdAt(now)
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
        return addMessageWithImages(id, request.content(), List.of());
    }

    public TicketMessageViewDTO addMessageWithImages(Long id, String content, List<MultipartFile> images) {
        validateImages(images);
        validateText(content, images == null || images.isEmpty());
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
        Instant now = Instant.now();
        ticket.setUpdatedAt(now);
        TicketMessage message = new TicketMessage()
            .content(encodeContent(content == null ? "" : content.trim(), uploadImages(ticket.getId(), images)))
            .createdAt(now)
            .sender(user)
            .ticket(ticket);
        return toMessageView(messageRepository.save(message));
    }

    @Transactional(readOnly = true)
    public com.lumi.service.storage.CourseObjectStorage.Access imageAccess(Long ticketId, Long messageId, int imageIndex) {
        User user = currentUser();
        SupportTicket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireAccess(ticket, user);
        TicketMessage message = messageRepository.findById(messageId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!message.getTicket().getId().equals(ticketId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        List<TicketMessageContent.Image> images = decodeContent(message.getContent()).images();
        if (imageIndex < 0 || imageIndex >= images.size()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        var image = images.get(imageIndex);
        return imageStorage.ticketImageAccess(ticketId, image.key(), image.fileName());
    }

    public SupportTicketViewDTO reopen(Long id) {
        User user = currentUser();
        SupportTicket ticket = ticketRepository.findByIdForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireAccess(ticket, user);
        if (!SupportTicketStatus.RESOLVED.name().equals(ticket.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only resolved tickets can be reopened");
        }
        ticket.setStatus(SupportTicketStatus.IN_PROGRESS.name());
        ticket.setUpdatedAt(Instant.now());
        return toTicketView(ticket);
    }

    public SupportTicketViewDTO changeStatus(Long id, SupportTicketStatus status) {
        requireAdmin();
        if (status == SupportTicketStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OPEN is only used for new tickets");
        }
        SupportTicket ticket = ticketRepository.findByIdForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!status.name().equals(ticket.getStatus())) {
            ticket.setStatus(status.name());
            ticket.setUpdatedAt(Instant.now());
        }
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
        TicketMessageContent content = decodeContent(message.getContent());
        List<TicketImageDTO> images = new ArrayList<>();
        for (int index = 0; index < content.images().size(); index++) {
            images.add(new TicketImageDTO(index, content.images().get(index).fileName()));
        }
        return new TicketMessageViewDTO(
            message.getId(),
            content.text(),
            images,
            message.getCreatedAt(),
            message.getSender().getId(),
            message.getSender().getLogin()
        );
    }

    private void validateText(String content, boolean required) {
        if ((content == null && required) || (content != null && content.trim().length() > 5000) || (required && content.isBlank())) throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Invalid ticket message"
        );
    }

    private void validateImages(List<MultipartFile> images) {
        if (images == null) return;
        if (images.size() > 3) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many ticket images");
        for (MultipartFile image : images) {
            if (image == null || image.isEmpty() || image.getSize() > 5 * 1024 * 1024) throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid ticket image size"
            );
        }
    }

    private List<TicketMessageContent.Image> uploadImages(Long ticketId, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) return List.of();
        List<TicketMessageContent.Image> uploaded = new ArrayList<>();
        for (MultipartFile image : images) {
            try (var validated = fileValidator.validate(image, CourseFileValidator.Kind.THUMBNAIL)) {
                var stored = imageStorage.uploadTicketImage(ticketId, validated.path(), validated.name(), validated.mimeType());
                uploaded.add(stored);
                TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            if (status == STATUS_ROLLED_BACK) {
                                try {
                                    imageStorage.deleteTicketImage(stored.key());
                                } catch (RuntimeException ignored) {}
                            }
                        }
                    }
                );
            } catch (IOException error) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot prepare ticket image");
            }
        }
        return uploaded;
    }

    private String encodeContent(String text, List<TicketMessageContent.Image> images) {
        return MAPPER.writeValueAsString(new TicketMessageContent(IMAGE_FORMAT, text, images));
    }

    private TicketMessageContent decodeContent(String stored) {
        if (stored != null && stored.startsWith("{")) {
            try {
                TicketMessageContent decoded = MAPPER.readValue(stored, TicketMessageContent.class);
                if (
                    IMAGE_FORMAT.equals(decoded.format()) &&
                    decoded.text() != null &&
                    decoded.images() != null &&
                    decoded.images().size() <= 3 &&
                    decoded.images().stream().allMatch(image -> image != null && image.key() != null && image.fileName() != null)
                ) {
                    return decoded;
                }
            } catch (RuntimeException ignored) {}
        }
        return new TicketMessageContent(null, stored, List.of());
    }
}

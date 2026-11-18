package com.lumi.service.impl;

import com.lumi.domain.Notification;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.NotificationCategory;
import com.lumi.repository.NotificationRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.SecurityUtils;
import com.lumi.service.NotificationService;
import com.lumi.service.dto.NotificationDTO;
import com.lumi.service.mapper.NotificationMapper;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link com.lumi.domain.Notification}.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final UserRepository userRepository;

    private final NotificationWebSocketService notificationWebSocketService;

    public NotificationServiceImpl(
        NotificationRepository notificationRepository,
        NotificationMapper notificationMapper,
        UserRepository userRepository,
        NotificationWebSocketService notificationWebSocketService
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.userRepository = userRepository;
        this.notificationWebSocketService = notificationWebSocketService;
    }

    @Override
    public NotificationDTO save(NotificationDTO notificationDTO) {
        LOG.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    @Override
    public NotificationDTO update(NotificationDTO notificationDTO) {
        LOG.debug("Request to update Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    @Override
    public Optional<NotificationDTO> partialUpdate(NotificationDTO notificationDTO) {
        LOG.debug("Request to partially update Notification : {}", notificationDTO);

        return notificationRepository
            .findById(notificationDTO.getId())
            .map(existingNotification -> {
                notificationMapper.partialUpdate(existingNotification, notificationDTO);

                return existingNotification;
            })
            .map(notificationRepository::save)
            .map(notificationMapper::toDto);
    }

    public Page<NotificationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return notificationRepository.findAllWithEagerRelationships(pageable).map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        LOG.debug("Request to get Notification : {}", id);
        return notificationRepository.findOneWithEagerRelationships(id).map(notificationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Notification : {}", id);
        notificationRepository.deleteById(id);
    }

    private User getCurrentUser() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> findMyNotifications(NotificationCategory category, Pageable pageable) {
        User user = getCurrentUser();
        Page<Notification> page =
            category != null
                ? notificationRepository.findByUserIdAndCategoryOrderByCreatedAtDesc(user.getId(), category, pageable)
                : notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return page.map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMyUnreadNotifications() {
        User user = getCurrentUser();
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    @Override
    public Optional<NotificationDTO> markAsRead(Long id) {
        User user = getCurrentUser();
        return notificationRepository
            .findById(id)
            .filter(n -> n.getUser().getId().equals(user.getId()))
            .map(notification -> {
                if (!Boolean.TRUE.equals(notification.getIsRead())) {
                    notification.setIsRead(true);
                    notification.setReadAt(Instant.now());
                    notification = notificationRepository.save(notification);
                }
                return notificationMapper.toDto(notification);
            });
    }

    @Override
    public int markAllMyNotificationsAsRead() {
        User user = getCurrentUser();
        return notificationRepository.markAllAsRead(user.getId(), Instant.now());
    }

    @Override
    public NotificationDTO createAndSendNotification(
        User recipient,
        String title,
        String content,
        NotificationCategory category,
        String type,
        String actionUrl,
        String targetType,
        Long targetId
    ) {
        Notification notification = new Notification()
            .user(recipient)
            .title(title)
            .content(content)
            .category(category != null ? category : NotificationCategory.COURSE)
            .type(type)
            .actionUrl(actionUrl)
            .targetType(targetType)
            .targetId(targetId)
            .isRead(false)
            .createdAt(Instant.now());

        notification = notificationRepository.save(notification);
        NotificationDTO dto = notificationMapper.toDto(notification);

        // Push real-time notification via WebSocket
        notificationWebSocketService.sendToUser(recipient.getLogin(), dto);

        return dto;
    }
}

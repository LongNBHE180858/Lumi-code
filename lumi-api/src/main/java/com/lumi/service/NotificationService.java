package com.lumi.service;

import com.lumi.domain.User;
import com.lumi.domain.enumeration.NotificationCategory;
import com.lumi.service.dto.NotificationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.Notification}.
 */
public interface NotificationService {
    /**
     * Save a notification.
     *
     * @param notificationDTO the entity to save.
     * @return the persisted entity.
     */
    NotificationDTO save(NotificationDTO notificationDTO);

    /**
     * Updates a notification.
     *
     * @param notificationDTO the entity to update.
     * @return the persisted entity.
     */
    NotificationDTO update(NotificationDTO notificationDTO);

    /**
     * Partially updates a notification.
     *
     * @param notificationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NotificationDTO> partialUpdate(NotificationDTO notificationDTO);

    /**
     * Get all the notifications with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" notification.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NotificationDTO> findOne(Long id);

    /**
     * Delete the "id" notification.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get paginated notifications for current authenticated user.
     *
     * @param category optional filter by category
     * @param pageable pagination info
     * @return page of notifications
     */
    Page<NotificationDTO> findMyNotifications(NotificationCategory category, Pageable pageable);

    /**
     * Count unread notifications for current user.
     *
     * @return unread count
     */
    long countMyUnreadNotifications();

    /**
     * Mark a single notification as read if it belongs to current user.
     *
     * @param id notification ID
     * @return updated notification DTO
     */
    Optional<NotificationDTO> markAsRead(Long id);

    /**
     * Mark all notifications for current user as read.
     *
     * @return number of notifications updated
     */
    int markAllMyNotificationsAsRead();

    /**
     * Create, persist and send a real-time notification to a recipient user.
     */
    NotificationDTO createAndSendNotification(
        User recipient,
        String title,
        String content,
        NotificationCategory category,
        String type,
        String actionUrl,
        String targetType,
        Long targetId
    );
}

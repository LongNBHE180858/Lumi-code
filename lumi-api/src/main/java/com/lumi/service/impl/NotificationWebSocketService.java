package com.lumi.service.impl;

import com.lumi.service.dto.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class NotificationWebSocketService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationWebSocketService.class);

    private final SimpMessageSendingOperations messagingTemplate;

    public NotificationWebSocketService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send real-time notification to a specific user.
     *
     * @param login the recipient's username/login
     * @param notification the notification payload
     */
    public void sendToUser(String login, NotificationDTO notification) {
        if (login == null || notification == null) {
            return;
        }
        try {
            LOG.debug("Sending real-time notification to user: {}, title: {}", login, notification.getTitle());
            // User destination: client subscribes to /user/topic/notifications
            messagingTemplate.convertAndSendToUser(login, "/topic/notifications", notification);
            // Also push to explicit user topic in case client subscribes to /topic/notifications-{login}
            messagingTemplate.convertAndSend("/topic/notifications/" + login, notification);
        } catch (Exception e) {
            LOG.error("Failed to send real-time notification to user {}: {}", login, e.getMessage(), e);
        }
    }

    /**
     * Broadcast real-time message to a specific topic.
     *
     * @param topic the STOMP destination topic, e.g. /topic/course-review/123
     * @param payload the message payload object
     */
    public void broadcastToTopic(String topic, Object payload) {
        if (topic == null || payload == null) {
            return;
        }
        try {
            LOG.debug("Broadcasting to topic {}: {}", topic, payload);
            messagingTemplate.convertAndSend(topic, payload);
        } catch (Exception e) {
            LOG.error("Failed to broadcast to topic {}: {}", topic, e.getMessage(), e);
        }
    }
}

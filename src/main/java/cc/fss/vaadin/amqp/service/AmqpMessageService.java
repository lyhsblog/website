package cc.fss.vaadin.amqp.service;

import cc.fss.vaadin.amqp.model.NotificationMessage;
import cc.fss.vaadin.config.AmqpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AmqpMessageService {

    private static final Logger logger = LoggerFactory.getLogger(AmqpMessageService.class);

    private final RabbitTemplate rabbitTemplate;

    public AmqpMessageService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotificationMessage(NotificationMessage notificationMessage) {
        try {
            rabbitTemplate.convertAndSend(
                    AmqpConfig.NOTIFICATION_EXCHANGE,
                    AmqpConfig.NOTIFICATION_ROUTING_KEY,
                    notificationMessage
            );
            logger.info("Notification message sent: {}", notificationMessage);
        } catch (Exception e) {
            logger.error("Error sending notification message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send notification message", e);
        }
    }

    public void sendInfoNotification(String title, String message, String userId) {
        NotificationMessage notificationMessage = new NotificationMessage(
                UUID.randomUUID().toString(),
                title,
                message,
                "INFO",
                userId
        );
        sendNotificationMessage(notificationMessage);
    }

    public void sendSuccessNotification(String title, String message, String userId) {
        NotificationMessage notificationMessage = new NotificationMessage(
                UUID.randomUUID().toString(),
                title,
                message,
                "SUCCESS",
                userId
        );
        sendNotificationMessage(notificationMessage);
    }

    public void sendWarningNotification(String title, String message, String userId) {
        NotificationMessage notificationMessage = new NotificationMessage(
                UUID.randomUUID().toString(),
                title,
                message,
                "WARNING",
                userId
        );
        sendNotificationMessage(notificationMessage);
    }

    public void sendErrorNotification(String title, String message, String userId) {
        NotificationMessage notificationMessage = new NotificationMessage(
                UUID.randomUUID().toString(),
                title,
                message,
                "ERROR",
                userId
        );
        sendNotificationMessage(notificationMessage);
    }
}

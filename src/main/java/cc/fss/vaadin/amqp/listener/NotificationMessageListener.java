package cc.fss.vaadin.amqp.listener;

import cc.fss.vaadin.amqp.model.NotificationMessage;
import cc.fss.vaadin.config.AmqpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationMessageListener.class);

    @RabbitListener(queues = AmqpConfig.NOTIFICATION_QUEUE)
    public void handleNotificationMessage(NotificationMessage notificationMessage) {
        logger.info("Received notification message: {}", notificationMessage);

        // 在这里处理通知消息
        // 例如：发送邮件、推送通知、记录日志等
        processNotificationMessage(notificationMessage);
    }

    private void processNotificationMessage(NotificationMessage notificationMessage) {
        try {
            switch (notificationMessage.getType()) {
                case "INFO":
                    handleInfoNotification(notificationMessage);
                    break;
                case "SUCCESS":
                    handleSuccessNotification(notificationMessage);
                    break;
                case "WARNING":
                    handleWarningNotification(notificationMessage);
                    break;
                case "ERROR":
                    handleErrorNotification(notificationMessage);
                    break;
                default:
                    logger.warn("Unknown notification type: {}", notificationMessage.getType());
            }
        } catch (Exception e) {
            logger.error("Error processing notification message: {}", e.getMessage(), e);
        }
    }

    private void handleInfoNotification(NotificationMessage notificationMessage) {
        logger.info("Processing info notification: {} - {}",
                notificationMessage.getTitle(), notificationMessage.getMessage());
        // 实现信息通知的处理逻辑
        // 例如：记录到日志、发送邮件等
    }

    private void handleSuccessNotification(NotificationMessage notificationMessage) {
        logger.info("Processing success notification: {} - {}",
                notificationMessage.getTitle(), notificationMessage.getMessage());
        // 实现成功通知的处理逻辑
        // 例如：发送成功邮件、更新用户状态等
    }

    private void handleWarningNotification(NotificationMessage notificationMessage) {
        logger.warn("Processing warning notification: {} - {}",
                notificationMessage.getTitle(), notificationMessage.getMessage());
        // 实现警告通知的处理逻辑
        // 例如：发送警告邮件、记录警告日志等
    }

    private void handleErrorNotification(NotificationMessage notificationMessage) {
        logger.error("Processing error notification: {} - {}",
                notificationMessage.getTitle(), notificationMessage.getMessage());
        // 实现错误通知的处理逻辑
        // 例如：发送错误邮件、记录错误日志、触发告警等
    }
}

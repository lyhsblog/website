package cc.fss.vaadin.amqp.ui.view;

import cc.fss.vaadin.amqp.service.AmqpMessageService;
import cc.fss.vaadin.base.ui.view.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "amqp-test", layout = MainLayout.class)
public class AmqpTestView extends VerticalLayout {

    private final AmqpMessageService amqpMessageService;

    public AmqpTestView(AmqpMessageService amqpMessageService) {
        this.amqpMessageService = amqpMessageService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("AMQP Notification Testing"));

        // 通知消息测试区域
        add(createNotificationMessageSection());
    }

    private VerticalLayout createNotificationMessageSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.getStyle().set("border", "1px solid #ccc");
        section.getStyle().set("border-radius", "8px");

        section.add(new H3("Notification Messages"));

        TextField notificationTitleField = new TextField("Notification Title");
        notificationTitleField.setPlaceholder("Enter notification title");
        notificationTitleField.setWidth("300px");

        TextArea notificationMessageField = new TextArea("Notification Message");
        notificationMessageField.setPlaceholder("Enter notification message");
        notificationMessageField.setWidth("300px");
        notificationMessageField.setHeight("100px");

        Button sendInfoButton = new Button("Send Info Notification", e -> {
            sendNotification("INFO", notificationTitleField, notificationMessageField);
        });

        Button sendSuccessButton = new Button("Send Success Notification", e -> {
            sendNotification("SUCCESS", notificationTitleField, notificationMessageField);
        });

        Button sendWarningButton = new Button("Send Warning Notification", e -> {
            sendNotification("WARNING", notificationTitleField, notificationMessageField);
        });

        Button sendErrorButton = new Button("Send Error Notification", e -> {
            sendNotification("ERROR", notificationTitleField, notificationMessageField);
        });

        section.add(notificationTitleField, notificationMessageField,
                   sendInfoButton, sendSuccessButton, sendWarningButton, sendErrorButton);
        return section;
    }

    private void sendNotification(String type, TextField titleField, TextArea messageField) {
        try {
            String title = titleField.getValue();
            String message = messageField.getValue();

            if (title == null || title.trim().isEmpty()) {
                Notification.show("Please enter a notification title", 3000, Notification.Position.MIDDLE);
                return;
            }

            switch (type) {
                case "INFO":
                    amqpMessageService.sendInfoNotification(title, message, "test-user");
                    break;
                case "SUCCESS":
                    amqpMessageService.sendSuccessNotification(title, message, "test-user");
                    break;
                case "WARNING":
                    amqpMessageService.sendWarningNotification(title, message, "test-user");
                    break;
                case "ERROR":
                    amqpMessageService.sendErrorNotification(title, message, "test-user");
                    break;
            }

            Notification.show(type + " notification sent successfully!", 3000, Notification.Position.MIDDLE);

            // 清空字段
            titleField.clear();
            messageField.clear();
        } catch (Exception ex) {
            Notification.show("Error sending notification: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }
}

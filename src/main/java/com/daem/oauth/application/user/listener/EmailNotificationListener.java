package com.daem.oauth.application.user.listener;

import com.daem.oauth.application.user.EmailService;
import com.daem.oauth.domain.user.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener for domain events, responsible for triggering side effects like sending emails.
 */
@Component
public class EmailNotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationListener.class);

    private final EmailService emailService;

    public EmailNotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Handles the UserRegisteredEvent by sending an activation email.
     * @param event The event containing the newly registered user's data.
     */
    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        logger.info("Handling UserRegisteredEvent for user: {}", event.getUser().getUsername());
        try {
            emailService.sendActivationEmail(
                event.getUser().getEmail(),
                event.getUser().getUsername(),
                event.getUser().getActivationToken()
            );
            logger.info("Activation email sent successfully to {}", event.getUser().getEmail());
        } catch (Exception e) {
            // In a real-world application, you might want to add retry logic or
            // publish another event for a failed notification.
            logger.error("Failed to send activation email for user: {}", event.getUser().getUsername(), e);
        }
    }
}

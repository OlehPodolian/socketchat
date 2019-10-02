package oleg.podolian.socketchat.shell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import oleg.podolian.socketchat.domain.UserNotification;
import oleg.podolian.socketchat.repository.SessionRepository;
import oleg.podolian.socketchat.repository.UserNotificationRepository;
import oleg.podolian.socketchat.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Optional;

@ShellComponent
public class ShellController {

    private final Logger logger = LoggerFactory.getLogger(ShellController.class);

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    private static int counter = 1;

    @ShellMethod("hi")
    public String sessions() {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sessionRepository.getSessions());
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }


    @ShellMethod("message")
    public void message(@ShellOption(value = "email") String email,
                        @ShellOption(value = "message", defaultValue = "Test, message") String message) {
        UserNotification notification = getUserNotification(counter, message);
        final Optional<String> id = sessionRepository.getSessionId(email);
        if (id.isPresent()) {
            String sessionId = id.get();
            try {
                String body = objectMapper.writeValueAsString(notification);
                notificationService.sendMessage(body, sessionId, email);
                logger.info("Sent to user");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            userNotificationRepository.saveUserNotification(email, notification);
            logger.info("Saved to redis");
        }
    }

    private UserNotification getUserNotification(int counter, String message) {
        UserNotification notification = new UserNotification();
        notification.setTimestamp(System.currentTimeMillis());
        notification.setTopic("Topic #" + counter++);
        notification.setMessage(message);
        return notification;
    }

    @ShellMethod("orders")
    public void orders(@ShellOption(value = "email") String email) {
        final Optional<String> id = sessionRepository.getSessionId(email);
        String sessionId = id.orElseThrow(() -> new RuntimeException("No session id found"));
        notificationService.sendOrders("{\"id\":123, \"amount\":2.34, \"rate\":345}", sessionId);
    }

    @ShellMethod("remove")
    public void delete(@ShellOption(value = "session") String sessionId) {
        sessionRepository.removeSession(sessionId);
    }

}

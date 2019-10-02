package oleg.podolian.socketchat.service.impl;

import oleg.podolian.socketchat.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private SimpMessageSendingOperations simpMessagingTemplate;

    @Override
    public void sendMessage(String message, String sessionId, String email) {
        simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/notify/" + email, message, createHeaders(sessionId));
    }

    @Override
    public void sendOrders(String message, String sessionId) {
        simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/orders", message, createHeaders(sessionId));
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}

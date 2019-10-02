package oleg.podolian.socketchat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import oleg.podolian.socketchat.domain.WsMessage;
import oleg.podolian.socketchat.repository.SessionRepository;
import oleg.podolian.socketchat.repository.UserNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {


    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @MessageMapping("/register")
//    @SendTo("/topic/reply")
    public void processMessage(@Payload WsMessage message, SimpMessageHeaderAccessor simpMessageHeaderAccessor) throws Exception {
        sessionRepository.addSession(message.getSessionId(), message.getEmail());
    }

    @MessageMapping("/unregister")
    public void processOffMessage(@Payload WsMessage message, SimpMessageHeaderAccessor simpMessageHeaderAccessor) throws Exception {
        sessionRepository.removeSession(message.getEmail());
    }

    @SubscribeMapping("/queue/notify/{email}")
    public String getNotifications(@DestinationVariable String email) {
        try {
            return  objectMapper.writeValueAsString(userNotificationRepository.findAllByUser(email));
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}

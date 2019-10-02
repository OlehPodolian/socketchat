package oleg.podolian.socketchat.service;

public interface NotificationService {

    void sendMessage(String message, String sessionId, String email);

    void sendOrders(String message, String sessionId);
}

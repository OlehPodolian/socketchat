package oleg.podolian.socketchat.repository;

import oleg.podolian.socketchat.domain.UserNotification;

import java.util.Collection;

public interface UserNotificationRepository {

    Collection<UserNotification> findAllByUser(String email);

    boolean saveUserNotification(String email, UserNotification userNotification);
}

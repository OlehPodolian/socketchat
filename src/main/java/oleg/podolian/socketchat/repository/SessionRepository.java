package oleg.podolian.socketchat.repository;

import java.util.Map;
import java.util.Optional;

public interface SessionRepository {

    void addSession(String sessionId, String email);

    void removeSession(String email);

    Optional<String> getSessionId(String email);

    Map<String, String> getSessions();
}

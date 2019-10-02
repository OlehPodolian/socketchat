package oleg.podolian.socketchat.repository.impl;

import oleg.podolian.socketchat.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class SessionRepositoryImpl implements SessionRepository {

    private final String REDIS_SESSION_MAP = "REDIS_SESSION_MAP";
    private final HashOperations<String, String, String> hashOperations;

    @Autowired
    public SessionRepositoryImpl(@Qualifier("hashOperations") HashOperations<String, String, String> hashOperations) {
        this.hashOperations = hashOperations;
    }

    @Override
    public void addSession(String sessionId, String email) {
        hashOperations.put(REDIS_SESSION_MAP, email, sessionId);
    }

    @Override
    public void removeSession(String email) {
        hashOperations.delete(REDIS_SESSION_MAP, email);
    }

    @Override
    public Optional<String> getSessionId(String email) {
        return Optional.ofNullable(hashOperations.get(REDIS_SESSION_MAP, email));
    }

    @Override
    public Map<String, String> getSessions() {
        return hashOperations.entries(REDIS_SESSION_MAP);
    }

}

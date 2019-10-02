package oleg.podolian.socketchat.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import oleg.podolian.socketchat.domain.UserNotification;
import oleg.podolian.socketchat.repository.UserNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class UserNotificationRepositoryImpl implements UserNotificationRepository {

    private final String USER_NOTIFICATION_KEY = "USER_NOTIFICATION_KEY";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserNotificationRepositoryImpl(StringRedisTemplate redisTemplate,
                                          ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Collection<UserNotification> findAllByUser(String email) {
        final Set<String> keys = Optional.ofNullable(redisTemplate.keys(email + ":*"))
                .orElse(Collections.emptySet());
        if (!keys.isEmpty()) {
            List<UserNotification> nots = new ArrayList<>(keys.size());
            keys.forEach(key -> {
                final String value = redisTemplate.opsForValue().get(key);
                try {
                    nots.add(objectMapper.readValue(value, UserNotification.class));
                    redisTemplate.delete(key);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            nots.sort((o1, o2) -> o1.getTimestamp() > o2.getTimestamp() ? 1 : -1);
            return nots;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean saveUserNotification(String email, UserNotification userNotification) {
        try {
            String key = email.concat(":" + System.currentTimeMillis());
            final String notification = objectMapper.writeValueAsString(userNotification);
            redisTemplate.opsForValue().set(key, notification);
            redisTemplate.expire(key, 120, TimeUnit.SECONDS);
            return true;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse user notification: " + e);
        }
    }
}

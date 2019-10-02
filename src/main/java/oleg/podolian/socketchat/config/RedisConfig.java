package oleg.podolian.socketchat.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Objects;

@Configuration
public class RedisConfig {

//    @Value("${spring.redis.host}")
//    private String redisHost;
//    @Value("${spring.redis.port}")
//    private int redisPort;
//    @Value("${spring.redis.password}")
//    private String redisPassword;
//
//    @Value("${spring.redis.redis.pool.max-active}")
//    private int poolMaxActive;
//    @Value("${spring.redis.redis.pool.max-idle}")
//    private int poolMaxIdle;
//    @Value("${spring.redis.redis.pool.min-idle}")
//    private int poolMinIdle;

/*    @Bean
    public JedisClientConfiguration jedisClientConfiguration() {
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jedisPoolingClientConfigurationBuilder =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(poolMaxActive);
        genericObjectPoolConfig.setMaxIdle(poolMaxIdle);
        genericObjectPoolConfig.setMinIdle(poolMinIdle);
        return jedisPoolingClientConfigurationBuilder.poolConfig(genericObjectPoolConfig).build();
        // https://commons.apache.org/proper/commons-pool/apidocs/org/apache/commons/pool2/impl/GenericObjectPool.html
    }*/

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public StringRedisTemplate redisTemplate() {
        final StringRedisTemplate template = new StringRedisTemplate(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    @Qualifier("listOperations")
    public ListOperations<String, String> listOperations() {
        return redisTemplate().opsForList();
    }

    @Bean
    @Qualifier("setOperations")
    public SetOperations<String, String> setOperations() {
        return redisTemplate().opsForSet();
    }

    @Bean
    @Qualifier("hashOperations")
    public HashOperations<String, String, String> hashOperations() {
        return redisTemplate().opsForHash();
    }
}

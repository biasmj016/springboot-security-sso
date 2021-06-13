package com.abcms031.sso.auth.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Value;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class SecurityConfig {
    private final String HOSTNAME;
    private final int PORT;
    private final int DATABASE;
    private final String PASSWORD;
    private final long TIMEOUT;

    public SecurityConfig(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") int port,
            @Value("${spring.redis.database}") int database,
            @Value("${spring.redis.password}") String password,
            @Value("${spring.redis.timeout}") long timeout
    )
    {
        this.HOSTNAME = host;
        this.PORT = port;
        this.DATABASE = database;
        this.PASSWORD = password;
        this.TIMEOUT = timeout;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(HOSTNAME);
        config.setPort(PORT);
        config.setDatabase(DATABASE);
        config.setPassword(PASSWORD);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(TIMEOUT))
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(
            @Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory
    ) {

        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);

        return template;
    }

    @Bean
    public RedisTemplate<String, byte[]> messagePackRedisTemplate(
            @Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory
    ) {

        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setEnableDefaultSerializer(false);

        return template;
    }

    @Bean
    public ObjectMapper messagePackObjectMapper() {
        return new ObjectMapper(new MessagePackFactory())
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }



}

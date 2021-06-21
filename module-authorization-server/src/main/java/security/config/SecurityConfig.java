package security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
            // @Value 는 resources/~.properties 에서 값을 가져와 적용할 때 사용된다
            @Value("${redis.hostname}") String host,
            @Value("${redis.port}") int port,
            @Value("${redis.database}") int database,
            @Value("${redis.password}") String password,
            @Value("${redis.timeout}") long timeout
    )
    {
        this.HOSTNAME = host;
        this.PORT = port;
        this.DATABASE = database;
        this.TIMEOUT = timeout;
        this.PASSWORD = password;
    }

    // @Configuration 을 상단 클래스에 적용했기때문에
    // 하단의 메소드에 적용해서 나중에 @Autowired 로 @Bean 을 부를 수 있게됨
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
            // @Autowired 와 같이 쓴다
            // 같은 타입의 @Bean 객체가 있을 때 해당 아이디를 적어 원하는 Bean 이 주입되게함
            // 타입은 메소드의 리스폰스값인듯 함..
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

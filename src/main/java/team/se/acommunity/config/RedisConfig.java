package team.se.acommunity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;


@Configuration
public class RedisConfig {
    // 使用bean标注，定义第三方bean，他会自动把自定义的bean装载入spring容器中
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

    }
}

package redis_08;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@SpringBootTest
class Redis08DisLockApplicationTests {

    /**
     * 测试使用Environment对象获取配置文件内容
     */
    @Test
    void contextLoads(@Autowired Environment environment) {
        String property = environment.getProperty("spring.redis.host");
        System.out.println(property);
    }
}

package redis_08.redisson_2;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @projectName: Redis7_PNode
 * @package: redis_08.redisson_2
 * @className: RedissonTest
 * @author: White
 * @description: Redisson测试
 * @date: 2023/1/10 10:16
 * @version: 1.0
 */
@SpringBootTest
public class RedissonTest {

    //测试redisson
    @Resource
    private RedissonClient redissonClient;

    /**
     * 侧视redissonClient
     */
    @Test
    void testRedisson(){
        System.out.println(redissonClient);
    }

}

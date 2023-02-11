package jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;

import java.util.Set;

/**
 * @projectName: Redis7_PNode
 * @package: jedis
 * @className: JedisPoolTest
 * @author: White
 * @description: JedisPool可以解决线程安全问题
 * @date: 2023/1/7 9:48
 * @version: 1.0
 */
public class JedisPoolTest {
    private Jedis jedis;

    private JedisPool jedisPool = new JedisPool("192.168.93.130", 6379);

    // jedis依赖4.0版本及以上才能 使用
    private JedisPooled jedisPooled=new JedisPooled("192.168.93.130", 6379);

    /**
     * 使用jedis进行操作
     */
    @Test
    public void testConnection() {
        jedis = new Jedis("192.168.93.130", 6379);
        //使用linux的redis地址创建jedis
        jedis.set("name2", "李四");
        Set<String> set = jedis.keys("*");
        set.forEach(System.out::println);
    }

    /**
     * 测试使用jedisPool技术
     *   Jedis实例的创建需要使用try-with-resources块
     *     ----Jedis jedis = jedisPool.getResource()
     */
    @Test
    public void testByPool() {
        try (Jedis jedis = jedisPool.getResource()) {
            System.out.println(jedis);

            jedis.set("name3", "王五");
            Set<String> set = jedis.keys("*");
            set.forEach(System.out::println);
        }
    }

    /**
     * 使用JedisPooled创建jedis连接池（不需要使用try-with-resources块）
     */
    @Test
    public void testByPooled() {
        jedisPooled.set("jedispooled",jedisPooled.toString());

        System.out.println("jedispooled  = "+jedisPooled.get("jedispooled"));
    }
}

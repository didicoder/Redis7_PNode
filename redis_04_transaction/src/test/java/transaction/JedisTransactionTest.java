package transaction;

/**
 * @projectName: Redis7_PNode
 * @package: transaction
 * @className: JedisTransactionTest
 * @author: White
 * @description: redis事务测试
 * @date: 2023/1/7 12:42
 * @version: 1.0
 */

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

/**
 * redis事务
 * 1. multi：开启事务
 * 2. exec：执行事务
 * 3. discard：取消事务
 * <p>
 * redis事务隔离
 * 1. 添加资源
 * 2. watch资源
 * 3. 使用事务对资源进行操作
 */
public class JedisTransactionTest {
    private JedisPool jedisPool = new JedisPool("192.168.93.130", 6380);

    /**
     * 测试连通性
     */
    @Test
    public void testConnection() {
        try (Jedis jedis = jedisPool.getResource()) {
            System.out.println(jedis.keys("*"));
        }
    }

    /**
     * 测试事务
     *     语法错误——int a=1/0;
     *     会抛出异常，并取消事务
     */
    @Test
    public void test01() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.mset("name", "zhangsan");
            jedis.set("age", "50");

            //开启事务
            Transaction tx = jedis.multi();

            try {
                tx.set("name", "张三");
                tx.incrBy("age", -30l);

//                //模拟运行错误
//                int a=1/0;

                //执行事务
                tx.exec();
            } catch (Exception e) {
                e.printStackTrace();

                //取消事务
                tx.discard();
            } finally {
                System.out.println("name = "+jedis.get("name"));
                System.out.println("age = "+jedis.get("age"));
            }
        }
    }

    /**
     * 测试事务
     *     Redis执行错误——tx.incr("type");
     *     该异常不会影响其他命令的执行
     */
    @Test
    public void test02() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("id", "1001");
            jedis.set("type", "market");

            //开启事务
            Transaction tx = jedis.multi();

            try {
                tx.set("type", "市场部");
                tx.incr("type");
                tx.incrBy("id", -30l);

                //执行事务
                tx.exec();
            } catch (Exception e) {
                e.printStackTrace();

                //取消事务
                tx.discard();
            } finally {
                System.out.println("id = "+jedis.get("id"));
                System.out.println("type = "+jedis.get("type"));
            }
        }
    }

    /**
     * 测试事务——使用watch监测资源
     */
    @Test
    public void testWatch() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("age","20");
            System.out.println("age 增一前 = "+jedis.get("age"));

            //监听资源
            jedis.watch("age");

            //开启事务
            Transaction tx = jedis.multi();

            try {

                tx.incr("age");

                //执行事务
                tx.exec();
            } catch (Exception e) {
                e.printStackTrace();

                //取消事务
                tx.discard();
            } finally {
                System.out.println("age 增一后 = "+jedis.get("age"));
            }
        }
    }
}

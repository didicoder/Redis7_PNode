package redis_08.controller;

import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @projectName: Redis7_PNode
 * @package: redis_08.controller
 * @className: SeckillController
 * @author: White
 * @description: 秒杀业务表现层
 * @date: 2023/1/9 15:49
 * @version: 1.0
 */
@RestController
@RequestMapping("/seckill")
public class SeckillController {
    //分布式锁的key
    private static final String REDIS_LOCK = "redis_lock";

    /**
     * @Autowired 和 @Resource 注解的区别
     * 一、联系
     * 1.@Autowired 和 @Resource 注解都是作为bean对象注入的时候使用的
     * 2.两者都可以声明在字段和setter方法上
     * 二、区别
     * 1.@Autowired注解是Spring提供的，而@Resource注解是J2EE本身提供的
     * 2.@Autowird注解默认通过byType方式注入，而@Resource 注解默认通过byName方式注入
     * 3.@Autowired注解注入的对象需要在IOC容器中存在，否则需要加上属性required=false
     */
    @Resource
    private StringRedisTemplate template;

    @Value("${server.port}")
    private String port;
    //使用Environment 对象封装所有配置文件参数
    @Autowired
    private Environment environment;

    //自动装配Redisson客户端
    @Resource(name = "redisson")
    private RedissonClient redissonClient;

    //装配3个不同的Redis主从服务
    @Resource(name = "redisson-1")
    private Redisson redisson1;
    @Resource(name = "redisson-2")
    private Redisson redisson2;
    @Resource(name = "redisson-3")
    private Redisson redisson3;


    /**
     * 一、秒杀请求处理——不使用锁
     * 1.问题：存在很多用户同时读取Redis缓存中的"sk:0008"这个key，那么大家读取到的 value 很可能是相同的，均大于零，均可购买。此时就会出现“超卖”。
     *
     * @return
     */
    @GetMapping
    public String seckillHandler() {
        String result = "库存不足，请联系商家补货！";
        //从Redis中获取库存
        String stock = template.opsForValue().get("sk:0008");
        //进行数值转换
        Integer amount = (stock == null ? 0 : Integer.parseInt(stock));

        //修改库存
        if (amount > 0) {
            //修改库存并写回Redis
            template.opsForValue().set("sk:0008", String.valueOf(--amount));
            result = "库存还有" + amount + "件";
        }
        return result + "，" + port;
    }


    /**
     * 二、使用分布式锁，setnx实现方式
     * 1.原理：分布式系统中的哪个节点抢先成功执行了setnx，谁就抢到了锁
     * 2.问题：若处理当前请求的主机在执行完“添加锁”语句后突然宕机，其 finally 中的释放锁代码根本就没有执行，那么，会由于无法获得到锁而永久性阻塞
     *
     * @return
     */
    @GetMapping("/sk2")
    public String seckillHandler2() {
        String result = "抱歉，您没有抢到！";
        try {
            //添加锁
            Boolean lockOK = template.opsForValue().setIfAbsent(REDIS_LOCK, "lock");

            //判断是否加锁
            if (!lockOK) {
                return result;
            }

            //添加锁成功
            //从Redis中获取库存
            String stock = template.opsForValue().get("sk:0008");
            //进行数值转换
            Integer amount = (stock == null ? 0 : Integer.parseInt(stock));

            //修改库存
            if (amount > 0) {
                //修改库存并写回Redis
                template.opsForValue().set("sk:0008", String.valueOf(--amount));
                result = "库存还有" + amount + "件";
            }
        } finally {
            //释放锁
            template.delete(REDIS_LOCK);
        }
        return result;
    }


    /**
     * 三、使用分布式锁，为锁添加过期时间
     * 1.原理：可以为锁添加过期时间，这样就不会出现锁被某节点主机永久性占用的情况
     * 2.建议：在setnx命令中直接给出该key的过期时间，因为是直接在setnx中完成了两步操作，具有原子性。
     * 3.问题：为锁指定的过期时间为5秒，如果请求a的处理时间超过了5秒，这个锁自动过期了。其他请求可以申请到锁并使用，此时请求a处理完了，回来继续执行程序，请求a就会把请求b设置的锁给删除了。此时其它请求就可申请到锁，并与请求b同时访问共享资源，很可能会引发数据的不一致。
     * 4.不满足谁加锁谁解锁
     *
     * @return
     */
    @GetMapping("/sk3")
    public String seckillHandler3() {
        String result = "抱歉，您没有抢到！";
        try {
//            //添加锁方式1，不推荐
//            Boolean lockOK = template.opsForValue().setIfAbsent(REDIS_LOCK, "lock");
//            //为锁添加过期时间
//            template.expire(REDIS_LOCK, 5, TimeUnit.MINUTES);

            //添加锁，并同时添加过期时间5秒
            boolean lockOK = Boolean.TRUE.equals(template.opsForValue().setIfAbsent(REDIS_LOCK, "lock", 5, TimeUnit.SECONDS));

            //判断是否加锁
            if (!lockOK) {
                return result;
            }

            //添加锁成功
            //从Redis中获取库存
            String stock = template.opsForValue().get("sk:0008");
            //进行数值转换
            Integer amount = (stock == null ? 0 : Integer.parseInt(stock));

            //修改库存
            if (amount > 0) {
                //修改库存并写回Redis
                template.opsForValue().set("sk:0008", String.valueOf(--amount));
                result = "库存还有" + amount + "件";
            }
        } finally {

            //释放锁
            template.delete(REDIS_LOCK);
        }
        return result;
    }


    /**
     * 四、使用分布式锁，为锁添加过期时间、客户端标识
     * 1.原理：在删除锁时，只有在发起当前删除操作的客户端的UUID与锁的value相同时才可以。
     * 2.问题：在finally{}中对于删除锁的客户端身份的判断与删除锁操作是两个语句，不具有原子性，在并发场景下可能会出问题。
     * 3.解决方案：①使用redis事务，②使用lua脚本
     *
     * @return
     */
    @GetMapping("/sk4")
    public String seckillHandler4() {
        String result = "抱歉，您没有抢到！";

        //获取UUID，作为客户端标识
        String clientId = UUID.randomUUID().toString();

        try {
            //添加锁，将锁的value设置为clientId
            boolean lockOK = Boolean.TRUE.equals(template.opsForValue().setIfAbsent(REDIS_LOCK, clientId, 5,
                    TimeUnit.SECONDS));

            //判断是否加锁
            if (!lockOK) {
                return result;
            }

            //添加锁成功
            //从Redis中获取库存
            String stock = template.opsForValue().get("sk:0008");
            //进行数值转换
            Integer amount = (stock == null ? 0 : Integer.parseInt(stock));

            //修改库存
            if (amount > 0) {
                //修改库存并写回Redis
                template.opsForValue().set("sk:0008", String.valueOf(--amount));
                result = "库存还有" + amount + "件";
            }
        } finally {
            //判断是否为加锁的客户端释放锁
            if (clientId.equals(template.opsForValue().get(REDIS_LOCK))) {
                //释放锁
                template.delete(REDIS_LOCK);
            }
        }
        return result;
    }


    /**
     * 五、使用分布式锁，添加lua脚本
     * 1.lua脚本：eval "returnKEY[3]" 3 name age sex zs 23 man aaa bbb
     * 2.原理：通过Lua脚本来实现对客户端身份的判断与删除锁操作的合并的原子性。
     * 3.使用：eval命令在RedisTemplate中没有对应的方法，而Jedis中具有该同名方法。
     * 4.问题：请求a的锁过期，但其业务还未执行完毕；请求b申请到了锁，其也正在处理业务。如果此时两个请求都同时修改了共享的库存数据，那么就又会出现数据不一致的问题，即仍然存在并发问题。
     *
     * @return
     */
    @GetMapping("/sk5")
    public String seckillHandler5() {
        String result = "抱歉，您没有抢到！";

        //获取UUID，作为客户端标识
        String clientId = UUID.randomUUID().toString();

        try {
            //添加锁，将锁的value设置为clientId
            boolean lockOK = Boolean.TRUE.equals(template.opsForValue().setIfAbsent(REDIS_LOCK, clientId, 5,
                    TimeUnit.SECONDS));

            //判断是否加锁
            if (!lockOK) {
                return result;
            }

            //添加锁成功
            //从Redis中获取库存
            String stock = template.opsForValue().get("sk:0008");
            //进行数值转换
            Integer amount = (stock == null ? 0 : Integer.parseInt(stock));

            //修改库存
            if (amount > 0) {
                //修改库存并写回Redis
                template.opsForValue().set("sk:0008", String.valueOf(--amount));
                result = "库存还有" + amount + "件";
            }
        } finally {
            //创建jedis线程池
            JedisPool jedisPool = new JedisPool(environment.getProperty("spring.redis.host"), Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.redis.port"))));

            //使用 try-with-block 块获取jedis
            //通过lua脚本，判断是否为加锁的客户端释放锁，并释放锁
            try (Jedis jedis = jedisPool.getResource()) {
                /**
                 *  编写lua脚本
                 *   1.redis.call('get'：是lua中对Redis命令的调用函数
                 *   2.脚本解释 redis.call('get',KEYS[1])——从Redis中获取KEYS[1]的值
                 *     KEYS[1]：Collections.singletonList(REDIS_LOCK)
                 *     ARGV[1]：Collections.singletonList(clientId)
                 *     如果 KEYS[1] == ARGV[1]，则删除KEYS[1]；否则返回0
                 *   3.注意：每行最后有一个空格
                 */
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) " +
                        "end return 0 ";

                //调用lua脚本，eval()方法的返回值为script的返回值
                Object eval = jedis.eval(script, Collections.singletonList(REDIS_LOCK), Collections.singletonList(clientId));

                //eval()的结果：0或1
                if ("1".equals(eval.toString())) {
                    //释放了锁
                    System.out.println("释放锁成功");
                } else {
                    System.out.println("释放锁异常");
                }
            }
        }
        return result;
    }


    /**
     * 六、使用分布式锁，可重用锁（锁续约）
     * 1. 导入依赖 redisson-spring-boot-starter
     * 2. 创建锁对象 redissonClient.getLock(REDIS_LOCK)
     * 3. 加锁：rLock.tryLock()
     * 4. 释放锁：rLock.unlock()
     * <p>
     * 5. 问题：单机情况下，是没有问题的。但如果是在 Redis 主从集群中，那么其还存在锁丢失问题。
     *
     * @return
     */
    @GetMapping("/sk6")
    public String seckillHandler6() {
        String result = "抱歉，您没有抢到！";

        //获取UUID，作为客户端标识
        String clientId = UUID.randomUUID().toString();

        //创建锁对象
        RLock rLock = redissonClient.getLock(REDIS_LOCK);

        try {
            //添加锁，
            boolean lockOK = rLock.tryLock();
            //添加锁，存活时间为5秒，如果申请锁失败最长等待时间为20秒
//            boolean lockOK = rLock.tryLock(20,5,TimeUnit.SECONDS);

            //判断是否加锁
            if (!lockOK) {
                return result;
            }

            //添加锁成功
            //从Redis中获取库存
            String stock = template.opsForValue().get("sk:0008");
            //进行数值转换
            Integer amount = (stock == null ? 0 : Integer.parseInt(stock));

            //修改库存
            if (amount > 0) {
                //修改库存并写回Redis
                template.opsForValue().set("sk:0008", String.valueOf(--amount));
                result = "库存还有" + amount + "件";
            }
        } finally {
            //释放锁
            rLock.unlock();
        }
        return result;
    }


    /**
     * 七、使用分布式锁，redisson红锁
     * 1.优势：红锁可以防止主从集群锁丢失问题
     * 2.问题：只能串行化，而串行化在高并发场景下势必会引发性能问题。
     *
     * @return
     */
    @GetMapping("/sk7")
    public String seckillHandler7() {
        String result = "抱歉，您没有抢到！";

        //获取UUID，作为客户端标识
        String clientId = UUID.randomUUID().toString();

        //创建3个可重用锁对象
        RLock rLock1 = redisson1.getLock(REDIS_LOCK + "-1");
        RLock rLock2 = redisson2.getLock(REDIS_LOCK + "-2");
        RLock rLock3 = redisson3.getLock(REDIS_LOCK + "-3");

        //定义红锁
        RedissonRedLock redLock = new RedissonRedLock(rLock1, rLock2, rLock3);

        try {
            //添加锁，
            boolean lockOK = redLock.tryLock();


            //判断是否加锁
            if (!lockOK) {
                return result;
            }

            //添加锁成功
            //从Redis中获取库存
            String stock = template.opsForValue().get("sk:0008");
            //进行数值转换
            Integer amount = (stock == null ? 0 : Integer.parseInt(stock));

            //修改库存
            if (amount > 0) {
                //修改库存并写回Redis
                template.opsForValue().set("sk:0008", String.valueOf(--amount));
                result = "库存还有" + amount + "件";
            }
        } finally {
            //释放锁
            redLock.unlock();
        }
        return result;
    }

    /**
     * 八、使用分布式锁，公平锁、联锁、读写锁、信号量、可过期信号量、分布式闭锁
     *
     * @return
     */
    @GetMapping("/sk8")
    public void seckillHandler8() {
        //创建3个可重用锁对象
        RLock rLock1 = redisson1.getLock(REDIS_LOCK + "-1");
        RLock rLock2 = redisson2.getLock(REDIS_LOCK + "-2");
        RLock rLock3 = redisson3.getLock(REDIS_LOCK + "-3");


        //1.创建公平锁
        RLock fairLock = redissonClient.getFairLock(REDIS_LOCK);


        //2.创建联锁
        RLock multiLock = redissonClient.getMultiLock(rLock1, rLock2, rLock3, fairLock);


        //3.创建读写锁
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(REDIS_LOCK);
        //读锁
        RLock readLock = readWriteLock.readLock();
        RLock writeLock = readWriteLock.writeLock();


        //4.创建信号量
        RSemaphore semaphore = redissonClient.getSemaphore("redis_semaphore");
        try {
            //申请信号量，信号量-1
            semaphore.acquire();
            //一次申请3个信号量
            semaphore.tryAcquire(3);
            //一次申请1个信号量，若申请不成功，则最多等待10秒
            semaphore.tryAcquire(1, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放信号量
            semaphore.release(2);
        }


        //5.创建可过期信号量,一次只能申请一个信号量
        RPermitExpirableSemaphore permit = redissonClient.getPermitExpirableSemaphore(REDIS_LOCK);
        String permitID = "";
        try {
            //只能申请1个信号量
            permitID = permit.acquire(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放信号量
            boolean tryRelease = permit.tryRelease(permitID);
        }


        //6.创建闭锁(合并线程和条件线程都需要该代码)
        RCountDownLatch latch = redissonClient.getCountDownLatch("countDownLatch");
        /**
         * 设置闭锁计数器初值
         *  1.Redis中没有设置该值
         *  2.Redis中设置了该值，但变为了0，需要重置
         */
        latch.trySetCount(10);
        //合并线程中等待闭锁的打开
        try {
            //阻塞合并线程
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //条件线程代码,使闭锁计数器-1
        latch.countDown();
    }


    /**
     * 九、使用信号量修改秒杀业务（推荐使用分布式锁）
     *
     * @return
     */
    @GetMapping("/sk9")
    public String seckillHandler9() {
        String result = "抱歉，您没有抢到！";

        //1.创建信号量
        RSemaphore semaphore = redissonClient.getSemaphore("redis_semaphore");

        try {
            //申请信号量
            boolean lockOK = semaphore.tryAcquire(5, 10, TimeUnit.SECONDS);

            //判断是否加锁
            if (!lockOK) {
                return result;
            }

            //添加锁成功
            //从Redis中获取库存
            String stock = template.opsForValue().get("sk:0008");
            //进行数值转换
            Integer amount = (stock == null ? 0 : Integer.parseInt(stock));

            //修改库存
            if (amount > 0) {
                //修改库存并写回Redis
                template.opsForValue().set("sk:0008", String.valueOf(--amount));
                result = "库存还有" + amount + "件";
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //不能释放信号量
            String a = "";
        }
        return result;
    }


    /**
     * 使用synchronized，不能解决问题
     *
     * @return
     */
    @GetMapping("/sk1")
    public String seckillHandler1() {
        synchronized (this) {
            String stock = template.opsForValue().get("sk:0008");
            Integer amount = (stock == null ? 0 : Integer.parseInt(stock));
            if (amount > 0) {
                template.opsForValue().set("sk:0008", String.valueOf(--amount));
                return "库存还有" + amount + "件";
            }
        }
        return "库存不足，请联系商家补货！";
    }
}

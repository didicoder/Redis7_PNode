package sentinel_1;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.resps.ScanResult;

import javax.xml.transform.Source;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @projectName: Redis7_PNode
 * @package: sentinel_1
 * @className: JedisSentinelPoolTest
 * @author: White
 * @description: Sentinel主从集群测试
 * @date: 2023/1/7 11:17
 * @version: 1.0
 */
public class JedisSentinelPoolTest {
    //jedis连接池2
    private JedisPooled jedis = new JedisPooled("192.168.93.130", 6380);

    //jedis 主从集群
    private JedisSentinelPool sentinelPool;

    /**
     * 使用代码块配置sentinel的端口
     */
    {
        Set<String> sentinels = new HashSet<String>();
        sentinels.add("192.168.93.130:26380");
        sentinels.add("192.168.93.130:26381");
        sentinels.add("192.168.93.130:26382");
        sentinelPool=new JedisSentinelPool("mymaster",sentinels);
    }

    /**
     * 测试连通性
     *      使用 scan cursor count number命令
     */
    @Test
    public void testRedisConnection() {
        jedis.set("name1", "zhangsan");
        ScanResult<String> result = jedis.scan("0");
        for (int i = 0; i < 4; i++) {
            System.out.println(result.getResult().get(i));
        }
    }

    /**
     * 测试 返回master节点
     */
    @Test
    public void testSentinel(){
        HostAndPort master = sentinelPool.getCurrentHostMaster();
        System.out.println(master.toString());
    }

    /**
     * 测试查询所有键
     */
    @Test
    public void testSentinel1(){
        Jedis resource = sentinelPool.getResource();
        Set<String> keys = resource.keys("*");
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }

}

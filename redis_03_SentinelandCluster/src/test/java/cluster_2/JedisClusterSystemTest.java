package cluster_2;

import org.junit.Test;
import redis.clients.jedis.ConnectionPool;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.resps.ScanResult;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @projectName: Redis7_PNode
 * @package: cluster_2
 * @className: JedisClusterSystemTest
 * @author: White
 * @description: jedis分布式系统测试
 * @date: 2023/1/7 11:34
 * @version: 1.0
 */
public class JedisClusterSystemTest {
    //jedis分布式系统
    private JedisCluster cluster;

    //使用代码块创建分布式系统
    {
        Set<HostAndPort> clusters = new HashSet<>();
        clusters.add(new HostAndPort("192.168.93.130",6380));
        clusters.add(new HostAndPort("192.168.93.130",6381));
        clusters.add(new HostAndPort("192.168.93.130",6382));
        clusters.add(new HostAndPort("192.168.93.130",6383));
        clusters.add(new HostAndPort("192.168.93.130",6384));
        clusters.add(new HostAndPort("192.168.93.130",6385));
        cluster=new JedisCluster(clusters);
    }

    /**
     * 测试连通性
     *      使用 scan cursor count number命令
     */
    @Test
    public void testRedisConnection() {
        cluster.set("name2", "lisi");
        System.out.println(cluster.get("name2"));

    }

    /**
     * 测试使用分组
     *      使用分组功能，则查询时也需要带着分组
     */
    @Test
    public void testCluster(){
        cluster.mset("name3{emp3}","lili","age3{emp3}","18","gender3{emp3}","female");

        System.out.println(cluster.mget("name3{emp3}","age3{emp3}","gender3{emp3}"));
    }
}

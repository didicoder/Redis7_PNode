package jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @projectName: Redis7_PNode
 * @package: jedis
 * @className: JedisTest
 * @author: White
 * @description: 连接linux的redis服务器
 * @date: 2023/1/6 17:13
 * @version: 1.0
 */

/**
 * jedis具有线程安全问题
 */
public class JedisTest {

    private Jedis jedis = new Jedis("192.168.93.130", 6379);

    /**
     * 测试redis连通性
     */
    @Test
    public void testConnection() {
        //使用linux的redis地址创建jedis
        jedis.set("name1", "zhangsan");
        Set<String> set = jedis.keys("*");
        set.forEach(System.out::println);
    }

    /**
     * 测试string型value操作命令
     */
    @Test
    public void testString() {
        jedis.mset("age", "20", "gender", "MALE");

        System.out.println("name1 = " + jedis.get("name1"));
        System.out.println("age = " + jedis.get("age"));
        System.out.println("gender = " + jedis.get("gender"));

        jedis.del("name1", "age", "gender", "name");
    }

    /**
     * 测试Hash型value操作命令
     * hset(String key, Map<String,String> hash) //可以一次存入多个field
     * hmset(String key, Map<String,String> hash) //也可以一次存入多个field
     */
    @Test
    public void testHash() {
        Map<String, String> field = new HashMap<String, String>();
        field.put("name", "lili");
        field.put("age", "18");
        field.put("gender", "female");
        field.put("company", "apple");

        jedis.hset("hashEmp3", field);

        List<String> emp2_list = jedis.hmget("hashEmp3", "name", "age", "gender", "company");
        Map<String, String> emp2 = jedis.hgetAll("hashEmp3");
        System.out.println(emp2.toString() + "\n----------------------------\n");

        emp2_list.forEach(System.out::println);
    }

    /**
     * 测试List型value操作命令
     */
    @Test
    public void testList() {
        long result = jedis.rpush("listcity", "beijing", "tianjin", "tianjin", "shanghai", "shenzhen");

        List<String> cityList = jedis.lrange("listcity", 0, -1);
        cityList.forEach(System.out::println);
    }

    /**
     * 测试Set型value操作命令
     */
    @Test
    public void testSet() {
        jedis.sadd("setuniversity", "清华大学", "北京大学", "天津大学", "复旦大学");

        Set<String> university = jedis.smembers("setuniversity");
        System.out.println(university);
    }

    /**
     * 测试ZSet型value操作命令
     */
    @Test
    public void testZSet() {
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("VB", 0.5);
        map.put("html", 10.0);
        map.put("rust", 50.0);
        map.put("go", 50.0);
        map.put("c", 80.0);
        map.put("c++", 80.0);
        map.put("java", 80.0);
        map.put("python", 100.0);

        jedis.zadd("zset_course", map);
        Set<String> courses = jedis.zrevrange("zset_course", 0, -1);
        System.out.println(courses);

        //获取最受欢迎语言前3
        Set<String> top_3 = jedis.zrevrange("zset_course", 0, 2);
        System.out.println(top_3);
    }
}

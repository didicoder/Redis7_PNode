package redis_08;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication
public class Redis08DisLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(Redis08DisLockApplication.class, args);
    }


    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;


    //设置redisson
    @Bean("redisson")
    public Redisson getRedisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+host+":"+port).setDatabase(0);
        return (Redisson) Redisson.create(config);
    }


    //设置redisson红锁需要的3个主从集群
    @Bean("redisson-1")
    public Redisson getRedisson1() {
        Config config = new Config();
        config.useSentinelServers().setMasterName("mymaster1").addSentinelAddress(host + ":16380",
                host + ":16381", host + ":16382");
        return (Redisson) Redisson.create(config);
    }

    @Bean("redisson-2")
    public Redisson getRedisson2() {
        Config config = new Config();
        config.useSentinelServers().setMasterName("mymaster2").addSentinelAddress(host + ":26380",
                host + ":26381", host + ":26382");
        return (Redisson) Redisson.create(config);
    }

    @Bean("redisson-3")
    public Redisson getRedisson3() {
        Config config = new Config();
        config.useSentinelServers().setMasterName("mymaster3").addSentinelAddress(host + ":36380",
                host + ":36381", host + ":36382");
        return (Redisson) Redisson.create(config);
    }

}

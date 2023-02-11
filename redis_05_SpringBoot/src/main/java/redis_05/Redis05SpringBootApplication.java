package redis_05;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching  //开启缓存功能
@SpringBootApplication
public class Redis05SpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(Redis05SpringBootApplication.class, args);
    }

}

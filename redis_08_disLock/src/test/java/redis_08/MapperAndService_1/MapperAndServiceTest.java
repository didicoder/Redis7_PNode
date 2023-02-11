package redis_08.MapperAndService_1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis_08.mapper.ProductMapper;
import redis_08.pojo.Product;
import redis_08.service.ProductService;

import java.util.List;

/**
 * @projectName: Redis7_PNode
 * @package: redis_05.MapperAndService
 * @className: MapperAndServiceTest
 * @author: White
 * @description: 测试
 * @date: 2023/1/7 16:14
 * @version: 1.0
 */
@SpringBootTest
public class MapperAndServiceTest {
    //使用 自动装配 注入mapper和service
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductService productService;

    /**
     * 测试数据库连接
     */
    @Test
    void testConnection() {
        System.out.println(productService.getById(5));
    }

    /**
     * 测试mp的list方法
     */
    @Test
    void testMybatisPlus(){
        List<Product> list = productService.list();
        List<Product> products = productMapper.selectList(null);
        list.forEach(System.out::println);
        System.out.println("-------------------------------");
        products.forEach(System.out::println);
    }

}

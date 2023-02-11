package redis_05.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import redis_05.mapper.ProductMapper;
import redis_05.pojo.Product;
import redis_05.service.ProductService;

/**
 * @projectName: Redis7_PNode
 * @package: redis_05.service.impl
 * @className: ProductServiceImpl
 * @author: White
 * @description: TODO
 * @date: 2023/1/7 16:02
 * @version: 1.0
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}

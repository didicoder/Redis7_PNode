package redis_05.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import redis_05.pojo.Product;

/**
 * @projectName: Redis7_PNode
 * @package: redis_05.mapper
 * @className: ProductMapper
 * @author: White
 * @description: 使用mybatisplus快速生成mapper接口
 * @date: 2023/1/7 15:57
 * @version: 1.0
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

}

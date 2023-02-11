package redis_08.pojo;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import redis_08.enums.CycleEnum;

import java.io.Serializable;



/**
 * @projectName: Redis7_PNode
 * @package: redis_05.domain
 * @className: Product
 * @author: White
 * @description: TODO
 * @date: 2023/1/7 15:27
 * @version: 1.0
 */

/**
 * SpringBoot使用Redis作为缓存
 *  1. 修改配置文件
 *  2. 实体类实现Serializable 序列化接口
 *  3. 启动类添加 @EnableCaching注解，开启缓存功能
 */
@Data
@TableName("product")
public class Product implements Serializable {

    private Integer id;

    //产品名称
    private String name;

    //年利化率
    private Double rate;

    //募集金额
    private Double amount;

    //已募集金额
    private Double raised;

    //产品周期
    @EnumValue //将注解标识的属性值存到数据库中
    private CycleEnum cycle;

    //产品募集结束时间
    @TableField("endTime")
    private String endTime;

    //乐观锁
    @Version
    private Integer version;

    /**
     * Cannot determine value type 异常解决方案
     *   1.设置无参构造函数
     *   2.设置全参数的构造函数
     */
    public Product(){}

    public Product(String name, Double rate, Double amount, Double raised, CycleEnum cycle, String endTime) {
        this.name = name;
        this.rate = rate;
        this.amount = amount;
        this.raised = raised;
        this.cycle = cycle;
        this.endTime = endTime;
    }
}

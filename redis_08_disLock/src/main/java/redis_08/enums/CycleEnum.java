package redis_08.enums;

import lombok.Getter;

/**
 * 产品周期枚举类
 * 一、枚举类的使用
 * 1.枚举类的理解：类的对象只有有限个
 * 2.当需要定义一组常量是，建议使用枚举类
 * 3.如果枚举类只有一个对象，则可以作为单例模式
 * 二、如何枚举类
 * 1.使用enum关键字定义枚举类
 */
@Getter
public enum CycleEnum {
    //3. 提供当前枚举类的多个对象
    MOUTH(1, "月"), HALFYEAR(2, "半年"), YEAR(3, "年"), LONGYEAR(4, "长期");

    //1. 声明Cycle对象的属性
    private Integer cycle;
    private String cycleName;

    //2. 私有化构造器,并给对象属性赋值
    CycleEnum(Integer cycle, String cycleName) {
        this.cycle = cycle;
        this.cycleName = cycleName;
    }

}

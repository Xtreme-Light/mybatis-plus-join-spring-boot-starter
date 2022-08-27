package com.github.owl.mybatisplus.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *  连表查询描述信息
 * </p>
 *
 * @author light
 * @since 2022/8/15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface JoinTable {

  /**
   * 原始表名，限定长度为2，否则会抛出异常<br>
   * 表名可能会被处理，比如加上scheme
   * @return 两张表
   */
  String[] tables();

  /**
   * 对应两张表连接时的各自字段<br>
   * 比如tablse={"table1","table2"},on={"fieldA","fieldB"}<br>
   * 则结果应形如 LEFT JOIN ${table2} t2 on t1.fieldA = t2.fieldB<br>
   * t1,t2的别名，根据传入的joinTable组，自动赋值<br>
   * @return 各自的字段
   */
  String[] on();
}

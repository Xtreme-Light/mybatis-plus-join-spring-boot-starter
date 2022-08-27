package com.github.owl.mybatisplus.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author light
 * @since 2022-08-27
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface JoinTables {

  /**
   * @return 连表查询描述信息
   */
  JoinTable[] joinTables();
}

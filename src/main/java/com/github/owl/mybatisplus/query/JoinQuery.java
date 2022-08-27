package com.github.owl.mybatisplus.query;

import com.baomidou.mybatisplus.core.conditions.query.Query;

/**
 * <p>
 * 为了解决count查询的问题
 * </p>
 *
 * @author light
 * @since 2022/8/26
 */
public interface JoinQuery<Children, T, R> extends Query<Children, T, R> {

  /**
   * count的条件查询
   *
   * @param field count()对应的列
   * @return 对应的条件wrapper器
   */
  @SuppressWarnings("unchecked")
  Children selectCount(R field);

  /**
   * 仅清除select内容
   */
  void clearSelect();
}
package com.github.owl.mybatisplus.enums;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 连表JOIN 关键字
 * </p>
 *
 * @author light
 * @since 2022/8/8
 */
@AllArgsConstructor
public enum JoinKeywordEnum implements ISqlSegment {
  /**
   * 左连接
   */
  LEFT_JOIN("LEFT JOIN"),
  /**
   * 右连接
   */
  RIGHT_JOIN("RIGHT JOIN"),
  /**
   * 内连接
   */
  INNER_JOIN("INNER JOIN"),
  ;
  private String sqlSegment;

  @Override
  public String getSqlSegment() {
    return this.sqlSegment;
  }

  public void setSqlSegment(String sqlSegment) {
    this.sqlSegment = sqlSegment;
  }
}

package com.github.owl.mybatisplus.enums;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 连表查询关键字,内部使用
 * </p>
 *
 * @author light
 * @since 2022-08-27
 **/
@AllArgsConstructor
public enum JoinSqlKeyword implements ISqlSegment {

  AS("AS"),
  LEFT_JOIN("LEFT JOIN"),
  RIGHT_JOIN("RIGHT JOIN"),
  INNER_JOIN("INNER JOIN"),
  ON("ON");
  private final String keyword;

  @Override
  public String getSqlSegment() {
    return this.keyword;
  }
}


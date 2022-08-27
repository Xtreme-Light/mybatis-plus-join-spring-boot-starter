package com.github.owl.mybatisplus.enums;

import com.baomidou.mybatisplus.core.toolkit.Constants;

/**
 * <p>
 * joinSql方法
 * </p>
 *
 * @author light
 * @since 2022/8/8
 */
public enum JoinSqlMethod implements Constants {

  SELECT_LEFT_JOIN_LIST("selectLeftJoinList", "左连接查询满足条件的多条数据",
      "<script>%s SELECT %s FROM %s %s %s\n</script>"),
  SELECT_LEFT_JOIN_COUNT("selectLeftJoinCount", "左连接查询满足条件总记录数",
      "<script>%s SELECT COUNT(%s) FROM %s %s %s\n</script>"),
  SELECT_LEFT_JOIN_PAGE("selectLeftJoinPage", "左连接,根据 entity 条件，查询全部记录（并翻页）",
      "<script>%s SELECT %s FROM %s %s %s %s\n</script>"),
  SELECT_RIGHT_JOIN_LIST("selectRightJoinList", "右连接查询满足条件的多条数据",
      "<script>%s SELECT %s FROM %s %s %s\n</script>"),
  SELECT_RIGHT_JOIN_COUNT("selectRightJoinCount", "右连接查询满足条件总记录数",
      "<script>%s SELECT COUNT(%s) FROM %s %s %s\n</script>"),
  SELECT_RIGHT_JOIN_PAGE("selectRightJoinPage", "右连接,根据 entity 条件，查询全部记录（并翻页）",
      "<script>%s SELECT %s FROM %s %s %s %s\n</script>"),
  SELECT_INNER_JOIN_LIST("selectInnerJoinList", "查询满足条件的多条数据",
      "<script>%s SELECT %s FROM %s %s %s\n</script>"),
  SELECT_INNER_JOIN_COUNT("selectInnerJoinCount", "查询满足条件总记录数",
      "<script>%s SELECT COUNT(%s) FROM %s %s %s\n</script>"),
  SELECT_INNER_JOIN_PAGE("selectInnerJoinPage", "根据 entity 条件，查询全部记录（并翻页）",
      "<script>%s SELECT %s FROM %s %s %s %s\n</script>"),
  SELECT_JOIN_LIST("selectJoinList", "自定义连接，查询满足条件的多条数据",
      "<script>%s SELECT %s FROM %s %s %s %s\n</script>"),
  SELECT_JOIN_COUNT("selectJoinCount", "自定义连接，查询满足条件总记录数",
      "<script>%s SELECT COUNT(%s) FROM  %s %s %s %s\n</script>"),
  SELECT_JOIN_PAGE("selectJoinPage", "自定义连接，根据 entity 条件，查询全部记录（并翻页）",
      "<script>%s SELECT %s FROM  %s %s %s %s\n</script>"),
  ;


  private final String method;
  private final String desc;
  private final String sql;

  JoinSqlMethod(String method, String desc, String sql) {
    this.method = method;
    this.desc = desc;
    this.sql = sql;
  }

  public String getMethod() {
    return method;
  }

  public String getDesc() {
    return desc;
  }

  public String getSql() {
    return sql;
  }


}

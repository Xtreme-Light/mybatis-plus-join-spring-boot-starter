package com.github.owl.mybatisplus.toolkit.sql;

import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.github.owl.mybatisplus.toolkit.TransferSqlTuple2;
import java.util.List;

/**
 * <p>
 * 增加一些方法
 * </p>
 *
 * @author light
 * @since 2022/8/16
 */
public class ConvertSqlScriptUtils extends SqlScriptUtils {


  /**
   * <p>
   * 生成 choose 标签的脚本
   * </p>
   *
   * @param tuple2List 多个条件 t1 是whentest语句 t2 是 whenSqlScript语句
   * @param otherwise  otherwise 内容
   * @return choose 脚本
   */
  public static String convertChoose(List<TransferSqlTuple2<String, String>> tuple2List,
      final String otherwise) {
    StringBuilder sb = new StringBuilder("<choose>" + NEWLINE);
    for (TransferSqlTuple2<String, String> tuple2 : tuple2List) {
      sb.append("<when test=\"")
          .append(tuple2.getT1())
          .append(QUOTE)
          .append(RIGHT_CHEV)
          .append(NEWLINE)
          .append(tuple2.getT2())
          .append(NEWLINE)
          .append("</when>")
          .append(NEWLINE);
    }
    sb.append("<otherwise>")
        .append(otherwise)
        .append("</otherwise>")
        .append(NEWLINE)
        .append("</choose>");
    return sb.toString();
  }
}

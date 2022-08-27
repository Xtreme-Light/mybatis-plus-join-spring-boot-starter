package com.github.owl.mybatisplus.injector.methods;

import com.github.owl.mybatisplus.enums.JoinSqlMethod;
import com.github.owl.mybatisplus.injector.AbstractJoinMethod;
import com.github.owl.mybatisplus.metadata.JoinTableInfo;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <p>
 *  左连接查询翻页
 * </p>
 *
 * @author light
 * @since 2022/8/16
 */
public class SelectLeftJoinPage extends AbstractJoinMethod {

  public SelectLeftJoinPage() {
    super(JoinSqlMethod.SELECT_LEFT_JOIN_PAGE.getMethod());
  }

  /**
   * @param methodName 方法名
   * @since 3.5.0
   */
  protected SelectLeftJoinPage(String methodName) {
    super(methodName);
  }

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      JoinTableInfo tableInfo) {
    JoinSqlMethod sqlMethod = JoinSqlMethod.SELECT_LEFT_JOIN_PAGE;
    return joinSql(mapperClass, modelClass, tableInfo, sqlMethod);
  }


}

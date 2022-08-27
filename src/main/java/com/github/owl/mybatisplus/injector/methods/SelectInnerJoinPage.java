package com.github.owl.mybatisplus.injector.methods;

import com.github.owl.mybatisplus.enums.JoinSqlMethod;
import com.github.owl.mybatisplus.injector.AbstractJoinMethod;
import com.github.owl.mybatisplus.metadata.JoinTableInfo;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <p>
 * 内连接查询所有满足条件的数据（翻页）
 * </p>
 *
 * @author light
 * @since 2022/8/16
 */
public class SelectInnerJoinPage extends AbstractJoinMethod {

  public SelectInnerJoinPage() {
    super(JoinSqlMethod.SELECT_INNER_JOIN_PAGE.getMethod());
  }

  /**
   * @param methodName 方法名
   * @since 3.5.0
   */
  protected SelectInnerJoinPage(String methodName) {
    super(methodName);
  }

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      JoinTableInfo tableInfo) {
    JoinSqlMethod sqlMethod = JoinSqlMethod.SELECT_INNER_JOIN_PAGE;
    return joinSql(mapperClass, modelClass, tableInfo, sqlMethod);
  }


}

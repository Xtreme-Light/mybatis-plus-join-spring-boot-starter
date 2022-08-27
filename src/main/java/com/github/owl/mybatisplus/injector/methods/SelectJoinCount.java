package com.github.owl.mybatisplus.injector.methods;

import com.github.owl.mybatisplus.enums.JoinSqlMethod;
import com.github.owl.mybatisplus.injector.AbstractJoinMethod;
import com.github.owl.mybatisplus.metadata.JoinTableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <p>
 *  可自定义join类型（left right inner）的join方式，获取count数量
 * </p>
 *
 * @author light
 * @since 2022/8/16
 */
public class SelectJoinCount extends AbstractJoinMethod {

  public SelectJoinCount() {
    super(JoinSqlMethod.SELECT_JOIN_COUNT.getMethod());
  }

  /**
   * @param methodName 方法名
   * @since 3.5.0
   */
  protected SelectJoinCount(String methodName) {
    super(methodName);
  }

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      JoinTableInfo tableInfo) {
    JoinSqlMethod sqlMethod = JoinSqlMethod.SELECT_JOIN_COUNT;
    StringBuilder joinSql = buildCustomerJoin(tableInfo);
    String sql = String.format(sqlMethod.getSql(),
        sqlFirst(), sqlCount(),
        joinSql,
        sqlWhereEntityWrapper(true, tableInfo),
        sqlOrderBy(tableInfo), sqlComment());
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
    return this.addSelectMappedStatementForOther(mapperClass, methodName, sqlSource,
        Long.class);
  }
}


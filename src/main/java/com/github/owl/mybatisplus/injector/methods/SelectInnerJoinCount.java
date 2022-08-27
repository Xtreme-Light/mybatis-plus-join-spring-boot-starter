package com.github.owl.mybatisplus.injector.methods;

import com.github.owl.mybatisplus.enums.JoinSqlKeyword;
import com.github.owl.mybatisplus.enums.JoinSqlMethod;
import com.github.owl.mybatisplus.injector.AbstractJoinMethod;
import com.github.owl.mybatisplus.metadata.JoinTableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <p>
 *  内连接查询满足条件总记录数
 * </p>
 *
 * @author light
 * @since 2022/8/16
 */
public class SelectInnerJoinCount extends AbstractJoinMethod {

  public SelectInnerJoinCount() {
    super(JoinSqlMethod.SELECT_INNER_JOIN_COUNT.getMethod());
  }
  /**
   * @param methodName 方法名
   * @since 3.5.0
   */
  public SelectInnerJoinCount(String methodName) {
    super(methodName);
  }

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      JoinTableInfo tableInfo) {
    JoinSqlMethod sqlMethod = JoinSqlMethod.SELECT_INNER_JOIN_COUNT;
    StringBuilder joinSql = buildJoin(tableInfo, JoinSqlKeyword.INNER_JOIN);
    String sql = String.format(sqlMethod.getSql(), sqlFirst(), sqlCount(), joinSql,
        sqlWhereEntityWrapper(true, tableInfo), sqlComment());
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
    return this.addSelectMappedStatementForOther(mapperClass, super.methodName, sqlSource, Long.class);
  }
}

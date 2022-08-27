package com.github.owl.mybatisplus.injector.methods;

import com.github.owl.mybatisplus.enums.JoinSqlKeyword;
import com.github.owl.mybatisplus.enums.JoinSqlMethod;
import com.github.owl.mybatisplus.injector.AbstractJoinMethod;
import com.github.owl.mybatisplus.metadata.JoinTableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <p>
 *  右连接查询满足条件总记录数
 * </p>
 *
 * @author light
 * @since 2022/8/16
 */
public class SelectRightJoinCount extends AbstractJoinMethod {

  public SelectRightJoinCount() {
    super(JoinSqlMethod.SELECT_RIGHT_JOIN_COUNT.getMethod());
  }
  /**
   * @param methodName 方法名
   * @since 3.5.0
   */
  public SelectRightJoinCount(String methodName) {
    super(methodName);
  }

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      JoinTableInfo tableInfo) {
    JoinSqlMethod sqlMethod = JoinSqlMethod.SELECT_RIGHT_JOIN_COUNT;
    StringBuilder joinSql = buildJoin(tableInfo, JoinSqlKeyword.RIGHT_JOIN);
    String sql = String.format(sqlMethod.getSql(), sqlFirst(), sqlCount(), joinSql,
        sqlWhereEntityWrapper(true, tableInfo), sqlComment());
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
    return this.addSelectMappedStatementForOther(mapperClass, super.methodName, sqlSource, Long.class);
  }
}

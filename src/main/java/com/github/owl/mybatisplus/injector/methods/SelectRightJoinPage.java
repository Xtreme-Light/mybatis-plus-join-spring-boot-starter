package com.github.owl.mybatisplus.injector.methods;

import com.github.owl.mybatisplus.enums.JoinSqlMethod;
import com.github.owl.mybatisplus.injector.AbstractJoinMethod;
import com.github.owl.mybatisplus.metadata.JoinTableInfo;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <p>
 * 右连接查询满足条件数量（翻页）
 * </p>
 *
 * @author light
 * @since 2022/8/16
 */
public class SelectRightJoinPage extends AbstractJoinMethod {

  public SelectRightJoinPage() {
    super(JoinSqlMethod.SELECT_RIGHT_JOIN_PAGE.getMethod());
  }

  /**
   * @param methodName 方法名
   * @since 3.5.0
   */
  protected SelectRightJoinPage(String methodName) {
    super(methodName);
  }

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      JoinTableInfo tableInfo) {
    JoinSqlMethod sqlMethod = JoinSqlMethod.SELECT_RIGHT_JOIN_PAGE;
    return joinSql(mapperClass, modelClass, tableInfo, sqlMethod);
  }


}

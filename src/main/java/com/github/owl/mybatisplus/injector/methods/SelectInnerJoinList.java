package com.github.owl.mybatisplus.injector.methods;

import com.github.owl.mybatisplus.enums.JoinSqlMethod;
import com.github.owl.mybatisplus.injector.AbstractJoinMethod;
import com.github.owl.mybatisplus.metadata.JoinTableInfo;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <p>
 *  内连接查询所有满足条件的数据
 * </p>
 *
 * @author light
 * @since 2022/8/8
 */
public class SelectInnerJoinList extends AbstractJoinMethod {

  public SelectInnerJoinList() {
    super(JoinSqlMethod.SELECT_INNER_JOIN_LIST.getMethod());
  }

  /**
   * @param name 方法名
   * @since 3.5.0
   */
  public SelectInnerJoinList(String name) {
    super(name);
  }

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      JoinTableInfo tableInfo) {
    JoinSqlMethod sqlMethod = JoinSqlMethod.SELECT_INNER_JOIN_LIST;
    return joinSql(mapperClass, modelClass, tableInfo, sqlMethod);
  }




}
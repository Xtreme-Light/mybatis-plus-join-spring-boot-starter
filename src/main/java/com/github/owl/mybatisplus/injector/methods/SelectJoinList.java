package com.github.owl.mybatisplus.injector.methods;

import com.github.owl.mybatisplus.enums.JoinSqlMethod;
import com.github.owl.mybatisplus.injector.AbstractJoinMethod;
import com.github.owl.mybatisplus.metadata.JoinTableInfo;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <p>
 * 自定义join查询，查询所有满足条件的数据
 * </p>
 *
 * @author light
 * @since 2022/8/8
 */
public class SelectJoinList extends AbstractJoinMethod {

  public SelectJoinList() {
    super(JoinSqlMethod.SELECT_JOIN_LIST.getMethod());
  }

  /**
   * @param name 方法名
   * @since 3.5.0
   */
  public SelectJoinList(String name) {
    super(name);
  }

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      JoinTableInfo tableInfo) {
    JoinSqlMethod sqlMethod = JoinSqlMethod.SELECT_INNER_JOIN_LIST;
    return buildCustomerJoinTotal(mapperClass, modelClass, tableInfo, sqlMethod);
  }




}

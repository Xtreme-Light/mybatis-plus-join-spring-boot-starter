package com.github.owl.mybatisplus.query;

import static java.util.stream.Collectors.joining;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.owl.mybatisplus.toolkit.JoinLambdaUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.reflection.property.PropertyNamer;

/**
 * <p>
 *  lambda分隔的查询器
 * </p>
 *
 * @author light
 * @since 2022-08-27
 **/
public abstract class AbstractJoinLambdaWrapper<T, Children extends AbstractJoinLambdaWrapper<T, Children>>
    extends AbstractWrapper<T, SFunction<T, ?>, Children> {

  private Map<String, ColumnCache> columnMap = null;
  private boolean initColumnMap = false;


  @Override
  @SafeVarargs
  protected final String columnsToString(SFunction<T, ?>... columns) {
    return columnsToString(true, columns);
  }

  @SafeVarargs
  protected final String columnsToString(boolean onlyColumn, SFunction<T, ?>... columns) {
    return columnsToString(onlyColumn, Arrays.asList(columns));
  }

  protected final String columnsToString(boolean onlyColumn, List<SFunction<T, ?>> columns) {
    return columns.stream().map(i -> columnToString(i, onlyColumn))
        .collect(joining(StringPool.COMMA));
  }

  @Override
  protected String columnToString(SFunction<T, ?> column) {
    return columnToString(column, true);
  }

  protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
    ColumnCache cache = getColumnCache(column);
    return onlyColumn ? cache.getColumn() : cache.getColumnSelect();
  }

  /**
   * 获取 SerializedLambda 对应的列信息，从 lambda 表达式中推测实体类
   * <p>
   * 如果获取不到列信息，那么本次条件组装将会失败
   *
   * @return 列
   * @throws com.baomidou.mybatisplus.core.exceptions.MybatisPlusException 获取不到列信息时抛出异常
   */
  protected ColumnCache getColumnCache(SFunction<T, ?> column) {
    LambdaMeta meta = JoinLambdaUtils.extract(column);
    String fieldName = PropertyNamer.methodToProperty(meta.getImplMethodName());
    Class<?> instantiatedClass = meta.getInstantiatedClass();
    tryInitCache(instantiatedClass);
    return getColumnCache(fieldName, instantiatedClass);
  }

  private void tryInitCache(Class<?> lambdaClass) {
    if (!initColumnMap) {
      final Class<T> entityClass = getEntityClass();
      if (entityClass != null) {
        lambdaClass = entityClass;
      }
      columnMap = JoinLambdaUtils.getColumnMap(lambdaClass);
      Assert.notNull(columnMap, "can not find lambda cache for this entity [%s]",
          lambdaClass.getName());
      initColumnMap = true;
    }
  }

  private ColumnCache getColumnCache(String fieldName, Class<?> lambdaClass) {
    ColumnCache columnCache = columnMap.get(LambdaUtils.formatKey(fieldName));
    Assert.notNull(columnCache, "can not find lambda cache for this property [%s] of entity [%s]",
        fieldName, lambdaClass.getName());
    return columnCache;
  }
}

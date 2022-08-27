package com.github.owl.mybatisplus.query;

import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * <p>
 *  lambda风格的查询器
 * </p>
 *
 * @author light
 * @since 2022-08-27
 **/
public class JoinLambdaQueryWrapper<T> extends AbstractJoinLambdaWrapper<T, JoinLambdaQueryWrapper<T>>
    implements JoinQuery<JoinLambdaQueryWrapper<T>, T, SFunction<T, ?>> {

  /**
   * 查询字段
   */
  private SharedString sqlSelect = new SharedString();

  public JoinLambdaQueryWrapper() {
    this((T) null);
  }

  @Override
  protected JoinLambdaQueryWrapper<T> instance() {
    return new JoinLambdaQueryWrapper<>(getEntity(), getEntityClass(), null, paramNameSeq,
        paramNameValuePairs,
        new MergeSegments(), paramAlias, SharedString.emptyString(), SharedString.emptyString(),
        SharedString.emptyString());
  }

  public JoinLambdaQueryWrapper(T entity) {
    super.setEntity(entity);
    super.initNeed();
  }

  public JoinLambdaQueryWrapper(Class<T> entityClass) {
    super.setEntityClass(entityClass);
    super.initNeed();
  }

  JoinLambdaQueryWrapper(T entity, Class<T> entityClass, SharedString sqlSelect,
      AtomicInteger paramNameSeq,
      Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
      SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
    super.setEntity(entity);
    super.setEntityClass(entityClass);
    this.paramNameSeq = paramNameSeq;
    this.paramNameValuePairs = paramNameValuePairs;
    this.expression = mergeSegments;
    this.sqlSelect = sqlSelect;
    this.paramAlias = paramAlias;
    this.lastSql = lastSql;
    this.sqlComment = sqlComment;
    this.sqlFirst = sqlFirst;
  }

  @Override
  public JoinLambdaQueryWrapper<T> selectCount(SFunction<T, ?> field) {
    return typedThis;
  }

  @Override
  public void clearSelect() {
    this.sqlSelect.toNull();
  }

  protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
    ColumnCache cache = getColumnCache(column);
    return onlyColumn ? cache.getColumn() : cache.getColumnSelect();
  }

  @SafeVarargs
  @Override
  public final JoinLambdaQueryWrapper<T> select(SFunction<T, ?>... columns) {
    if (ArrayUtils.isNotEmpty(columns)) {
      this.sqlSelect.setStringValue(columnsToString(false, columns));
    }
    return typedThis;
  }


  @Override
  public JoinLambdaQueryWrapper<T> select(Class<T> entityClass,
      Predicate<TableFieldInfo> predicate) {
    return typedThis;
  }

  @Override
  public String getSqlSelect() {
    return sqlSelect.getStringValue();
  }
}

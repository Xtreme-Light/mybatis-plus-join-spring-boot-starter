package com.github.owl.mybatisplus.query;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.owl.mybatisplus.enums.JoinSqlKeyword;
import com.github.owl.mybatisplus.toolkit.JoinStringUtils;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * 连表查询的wrapper
 * </p>
 *
 * @author light
 * @since 2022/8/8
 */
public class JoinQueryWrapper<T> extends AbstractWrapper<T, String, JoinQueryWrapper<T>>
    implements JoinQuery<JoinQueryWrapper<T>, T, String> {

  private final SharedString sqlSelect = new SharedString();

  public JoinQueryWrapper() {
    this(null);
  }

  public JoinQueryWrapper(T entity) {
    super.setEntity(entity);
    super.initNeed();
  }

  public JoinQueryWrapper(T entity, String... columns) {
    super.setEntity(entity);
    super.initNeed();
    this.select(columns);
  }

  /**
   * 获取 columnName 比如t1Name,转成t1.name这种形式
   */
  @Override
  protected String columnToString(String column) {
    return JoinStringUtils.generateColumn(column);
  }

  /**
   * 非对外公开的构造方法,只用于生产嵌套 sql
   *
   * @param entityClass 本不应该需要的
   */
  private JoinQueryWrapper(T entity, Class<T> entityClass, AtomicInteger paramNameSeq,
      Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
      SharedString paramAlias,
      SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
    super.setEntity(entity);
    super.setEntityClass(entityClass);
    this.paramNameSeq = paramNameSeq;
    this.paramNameValuePairs = paramNameValuePairs;
    this.expression = mergeSegments;
    this.paramAlias = paramAlias;
    this.lastSql = lastSql;
    this.sqlComment = sqlComment;
    this.sqlFirst = sqlFirst;
  }

  @Override
  protected JoinQueryWrapper<T> instance() {
    return new JoinQueryWrapper<>(getEntity(), getEntityClass(), paramNameSeq,
        paramNameValuePairs, new MergeSegments(),
        paramAlias, SharedString.emptyString(), SharedString.emptyString(),
        SharedString.emptyString());
  }

  /**
   * 因为是连表 所以转成 t1Name变成 t1.name as t1Name 这种形式，再用逗号拼接
   *
   * @param fields entity中对应的field，这里不采用原mybatis plus 的数据库column
   * @return select的内容
   */
  @Override
  public JoinQueryWrapper<T> select(String... fields) {
    if (ArrayUtils.isNotEmpty(fields)) {
      this.sqlSelect.setStringValue(
          Arrays.stream(fields).map(
              v -> columnToString(v)
                  + StringPool.SPACE
                  + JoinSqlKeyword.AS.getSqlSegment()
                  + StringPool.SPACE + v).collect(
              Collectors.joining(StringPool.COMMA)));
    }
    return typedThis;
  }

  @Override
  public JoinQueryWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
    return new JoinQueryWrapper<>(getEntity(), getEntityClass(), paramNameSeq,
        paramNameValuePairs, new MergeSegments(),
        paramAlias, SharedString.emptyString(), SharedString.emptyString(),
        SharedString.emptyString());
  }

  public String getSqlSelect() {
    return this.sqlSelect.getStringValue();
  }

  @Override
  public void clear() {
    super.clear();
    sqlSelect.toNull();
  }

  @Override
  public JoinQueryWrapper<T> selectCount(String filed) {
    if (StringUtils.isNotEmpty(filed)) {
      this.sqlSelect.setStringValue(columnToString(filed));
    }
    return typedThis;
  }

  @Override
  public void clearSelect() {
    sqlSelect.toNull();
  }

  /**
   * 返回一个支持 lambda 函数写法的 wrapper
   */
  public JoinLambdaQueryWrapper<T> lambda() {
    return new JoinLambdaQueryWrapper<>(getEntity(), getEntityClass(), sqlSelect, paramNameSeq, paramNameValuePairs,
        expression, paramAlias, lastSql, sqlComment, sqlFirst);
  }
}

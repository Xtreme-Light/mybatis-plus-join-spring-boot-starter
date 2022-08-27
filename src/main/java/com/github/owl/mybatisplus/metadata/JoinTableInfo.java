package com.github.owl.mybatisplus.metadata;

import static java.util.stream.Collectors.joining;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.owl.mybatisplus.exceptions.MybatisJoinConfigException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.session.Configuration;

/**
 * <p>
 * 数据库表反射信息
 * </p>
 *
 * @author light
 * @since 2022/8/9
 */
@Data
@Setter(AccessLevel.PACKAGE)
@Accessors(chain = true)
public class JoinTableInfo implements Constants {

  /**
   * 实体类型
   */
  private Class<?> entityType;

  /**
   * 连表信息
   */
  private JoinInfo[] joinInfos;
  /**
   * 表名称
   */
  private String[] tableNames;

  /**
   * 核心表
   */
  private String mainTable;



  /**
   * 表别名映射
   */
  private Map<String, String> tableAlias;

  /**
   * 保存原始的信息
   */
  @Getter
  public static final class JoinInfo{
    private final String[] tables;
    private final String[] on;
    public JoinInfo(String[] tables, String[] on) {
      Objects.requireNonNull(tables);
      Objects.requireNonNull(on);
      if (tables.length != 2) {
        throw new MybatisJoinConfigException("@JoinTable配置的tables长度不为2");
      }
      if (on.length != 2) {
        throw new MybatisJoinConfigException("@JoinTable配置的on连接字符数量不为2");
      }
      this.tables = tables;
      this.on = on;
    }
  }

  /**
   * 表映射结果集
   */
  private String resultMap;

  /**
   * 是否是需要自动生成的 resultMap
   */
  private boolean autoInitResultMap;

  /**
   * 表字段信息列表
   */
  private List<JoinTableFieldInfo> fieldList;

  /**
   * 命名空间 (对应的 mapper 接口的全类名)
   */
  private String currentNamespace;
  /**
   * MybatisConfiguration 标记 (Configuration内存地址值)
   */
  @Getter
  private Configuration configuration;

  /**
   * 是否开启下划线转驼峰
   * <p>
   * 未注解指定字段名的情况下,用于自动从 property 推算 column 的命名
   */
  private boolean underCamel;

  /**
   * 缓存包含主键及字段的 sql select
   */
  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  private String allSqlSelect;

  /**
   * 排序列表
   */
  @Setter
  private List<JoinTableFieldInfo> orderByFields;

  /**
   * @since 3.4.4
   */
  @Getter
  private Reflector reflector;


  /**
   * 表字段是否启用了逻辑删除
   *
   * @since 3.4.0
   */
  @Getter
  @Setter(AccessLevel.NONE)
  private boolean withLogicDelete;


  /**
   * 逻辑删除字段
   *
   * @since 3.4.0
   */
  @Getter
  @Setter(AccessLevel.NONE)
  private List<JoinTableFieldInfo> logicDeleteFieldInfoList = new ArrayList<>();

  /**
   * @param configuration 配置对象
   * @param entityType    实体类型
   * @since 3.4.4
   */
  public JoinTableInfo(Configuration configuration, Class<?> entityType) {
    this.configuration = configuration;
    this.entityType = entityType;
    this.reflector = configuration.getReflectorFactory().findForClass(entityType);
    this.underCamel = configuration.isMapUnderscoreToCamelCase();
  }

  /**
   * 获取所有字段的 select sql 片段
   *
   * @return sql 片段
   */
  public String getAllSqlSelect() {
    if (allSqlSelect != null) {
      return allSqlSelect;
    }
    allSqlSelect = chooseSelect(JoinTableFieldInfo::isSelect);
    return allSqlSelect;
  }

  /**
   * 获取需要进行查询的 select sql 片段
   *
   * @param predicate 过滤条件
   * @return sql 片段
   */
  public String chooseSelect(Predicate<JoinTableFieldInfo> predicate) {
    String fieldsSqlSelect = fieldList.stream().filter(predicate)
        .map(JoinTableFieldInfo::getSqlSelect).collect(joining(COMMA));
    if (StringUtils.isNotBlank(fieldsSqlSelect)) {
      return COMMA + fieldsSqlSelect;
    } else if (StringUtils.isNotBlank(fieldsSqlSelect)) {
      return fieldsSqlSelect;
    }
    return fieldsSqlSelect;
  }

  /**
   * 获取所有的查询的 sql 片段
   *
   * @param ignoreLogicDelFiled 是否过滤掉逻辑删除字段
   * @param prefix              前缀
   * @return sql 脚本片段
   */
  public String getAllSqlWhere(boolean ignoreLogicDelFiled, final String prefix) {
    final String newPrefix = prefix == null ? EMPTY : prefix;
    String filedSqlScript = fieldList.stream()
        .filter(i -> {
          if (ignoreLogicDelFiled) {
            return !(isWithLogicDelete() && i.isLogicDelete());
          }
          return true;
        })
        .map(i -> i.getSqlWhere(newPrefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
    return NEWLINE + filedSqlScript;
  }


  /**
   * 自动构建 resultMap 并注入(如果条件符合的话)
   */
  void initResultMapIfNeed() {
    if (autoInitResultMap && null == resultMap) {
      String id = currentNamespace + DOT + MYBATIS_PLUS + UNDERSCORE + entityType.getSimpleName();
      List<ResultMapping> resultMappings = new ArrayList<>();
      if (CollectionUtils.isNotEmpty(fieldList)) {
        fieldList.forEach(i -> resultMappings.add(i.getResultMapping(configuration)));
      }
      ResultMap resultMap = new ResultMap.Builder(configuration, id, entityType, resultMappings).build();
      configuration.addResultMap(resultMap);
      this.resultMap = id;
    }
  }

  /**
   * 获取逻辑删除字段的 sql 脚本
   *
   * @param startWithAnd 是否以 and 开头
   * @param isWhere      是否需要的是逻辑删除值
   * @return sql 脚本
   */
  public String getLogicDeleteSql(boolean startWithAnd, boolean isWhere) {
    if (withLogicDelete) {
      String logicDeleteSql = formatLogicDeleteSql(isWhere);
      if (startWithAnd) {
        logicDeleteSql = " AND " + logicDeleteSql;
      }
      return logicDeleteSql;
    }
    return EMPTY;
  }

  /**
   * format logic delete SQL, can be overrided by subclass
   * github #1386
   *
   * @param isWhere true: logicDeleteValue, false: logicNotDeleteValue
   * @return sql
   */
  protected String formatLogicDeleteSql(boolean isWhere) {
//    final String value = isWhere ? this.logicNotDeleteValue : this.logicDeleteValue;
    if (isWhere) {
      return logicDeleteFieldInfoList.stream().map(
          v -> {
            if (NULL.equalsIgnoreCase(v.getLogicNotDeleteValue())) {
              return v.getColumn() + " IS NULL";
            } else {
              return v.getColumn() + EQUALS + String.format(v.isCharSequence() ? "'%s'" : "%s",
                  v.getLogicNotDeleteValue());
            }
          }
      ).collect(joining(" AND "));
    }
    return logicDeleteFieldInfoList.stream()
        .map(v -> {
          final String targetStr = v.getColumn() + EQUALS;
          if (NULL.equalsIgnoreCase(v.getLogicDeleteValue())) {
            return targetStr + NULL;
          } else {
            return targetStr + String.format(
                v.isCharSequence() ? "'%s'" : "%s", v.getLogicDeleteValue());
          }
        }).collect(joining(" AND "));

  }

  /**
   * 根据给出的字段，打标记，比如逻辑删除
   *
   * @param fieldList 字段列表
   */
  public void setFieldList(List<JoinTableFieldInfo> fieldList) {
    this.fieldList = fieldList;
    fieldList.forEach(v -> {
      if (v.isLogicDelete()) {
        this.withLogicDelete = true;
        this.logicDeleteFieldInfoList.add(v);
      }
    });
  }
}

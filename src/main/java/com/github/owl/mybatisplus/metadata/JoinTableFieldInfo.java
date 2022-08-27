package com.github.owl.mybatisplus.metadata;

import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.github.owl.mybatisplus.toolkit.JoinStringUtils;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.type.UnknownTypeHandler;

/**
 * <p>
 * JoinTable的字段信息
 * </p>
 *
 * @author light
 * @since 2022/8/9
 */
@Getter
@ToString
@EqualsAndHashCode
public class JoinTableFieldInfo implements Constants {

  private static final Pattern joinDeleted = Pattern.compile("t\\d+(\\w+)");
  /**
   * 是否进行 select 查询
   * <p>大字段可设置为 false 不加入 select 查询范围</p>
   */
  private final boolean select = true;
  /**
   * 属性
   *
   * @since 3.3.1
   */
  private final Field field;
  /**
   * 字段名
   */
  private final String column;
  /**
   * 属性名
   */
  private final String property;
  /**
   * 属性表达式#{property}, 可以指定jdbcType, typeHandler等
   */
  private final String el;
  /**
   * jdbcType, typeHandler等部分
   */
  private final String mapping;
  /**
   * 属性类型
   */
  private final Class<?> propertyType;
  /**
   * 是否是基本数据类型
   *
   * @since 3.4.0 @2020-6-19
   */
  private final boolean isPrimitive;
  /**
   * 属性是否是 CharSequence 类型
   */
  private final boolean isCharSequence;

  /**
   * 是否是逻辑删除字段
   */
  @Getter
  private boolean logicDelete = false;

  /**
   * 逻辑删除值
   */
  private String logicDeleteValue;
  /**
   * 逻辑未删除值
   */
  private String logicNotDeleteValue;

  /**
   * where 字段比较条件
   */
  private String condition = SqlCondition.EQUAL;

  /**
   * 缓存 sql select
   */
  @Setter(AccessLevel.NONE)
  private String sqlSelect;

  /**
   * JDBC类型
   *
   * @since 3.1.2
   */
  private JdbcType jdbcType;
  /**
   * 类型处理器
   *
   * @since 3.1.2
   */
  private Class<? extends TypeHandler<?>> typeHandler;


  /**
   * 是否存在OrderBy注解
   */
  private boolean isOrderBy;
  /**
   * 排序类型
   */
  private String orderByType;
  /**
   * 排序顺序
   */
  private short orderBySort;

  /**
   * 全新的 存在 TableField 注解时使用的构造函数
   *
   * @param dbConfig        数据库配置
   * @param tableInfo       连表的表信息
   * @param field           字段信息
   * @param tableField      表字段注解
   * @param reflector       反射持有类
   * @param isOrderBy       是否有排序
   */
  public JoinTableFieldInfo(GlobalConfig.DbConfig dbConfig, JoinTableInfo tableInfo, Field field,
      TableField tableField,
      Reflector reflector, boolean isOrderBy) {
    this(dbConfig, tableInfo, field, tableField, reflector);
    this.isOrderBy = isOrderBy;
    if (isOrderBy) {
      initOrderBy(field);
    }
  }

  /**
   * 不存在 TableField 注解时, 使用的构造函数
   *
   * @param dbConfig        数据库配置
   * @param tableInfo       连表信息
   * @param field           字段信息
   * @param reflector       反射信息
   */
  public JoinTableFieldInfo(GlobalConfig.DbConfig dbConfig, JoinTableInfo tableInfo, Field field,
      Reflector reflector) {
    field.setAccessible(true);
    this.field = field;
    this.property = field.getName();
    this.propertyType = reflector.getGetterType(this.property);
    this.isPrimitive = this.propertyType.isPrimitive();
    this.isCharSequence = StringUtils.isCharSequence(this.propertyType);
    this.el = this.property;
    this.mapping = null;
    this.initLogicDelete(dbConfig, field);

    String column = this.property;
    if (tableInfo.isUnderCamel()) {
      /* 开启字段下划线申明 */
      column = StringUtils.camelToUnderline(column);
    }
    if (dbConfig.isCapitalMode()) {
      /* 开启字段全大写申明 */
      column = column.toUpperCase();
    }
    column = JoinStringUtils.generateColumn(column);
    /* 按照约定，进行转换 */
    String columnFormat = dbConfig.getColumnFormat();
    if (StringUtils.isNotBlank(columnFormat)) {
      column = String.format(columnFormat, column);
    }

    this.column = column;
    this.sqlSelect = column + " AS " + property;

  }

  /**
   * 不存在 TableField 注解时, 使用的构造函数
   *
   * @param dbConfig        数据库配置
   * @param tableInfo       连表信息
   * @param field           字段信息
   * @param reflector       反射信息
   * @param isOrderBy       是否排序
   */
  public JoinTableFieldInfo(GlobalConfig.DbConfig dbConfig, JoinTableInfo tableInfo, Field field,
      Reflector reflector,boolean isOrderBy) {
    this(dbConfig, tableInfo, field, reflector);
    this.isOrderBy = isOrderBy;
    if (isOrderBy) {
      initOrderBy(field);
    }
  }

  /**
   * 全新的 存在 TableField 注解时使用的构造函数
   *
   * @param dbConfig        数据库配置
   * @param tableInfo       连表信息
   * @param field           字段信息
   * @param tableField      注解字段信息
   * @param reflector       反射信息
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public JoinTableFieldInfo(GlobalConfig.DbConfig dbConfig, JoinTableInfo tableInfo, Field field,
      TableField tableField,
      Reflector reflector) {
    field.setAccessible(true);
    this.field = field;
    this.property = field.getName();
    this.propertyType = reflector.getGetterType(this.property);
    this.isPrimitive = this.propertyType.isPrimitive();
    this.isCharSequence = StringUtils.isCharSequence(this.propertyType);
    JdbcType jdbcType = tableField.jdbcType();
    final Class<? extends TypeHandler> typeHandler = tableField.typeHandler();
    final String numericScale = tableField.numericScale();
    boolean needAs = false;
    String el = this.property;
    if (StringUtils.isNotBlank(tableField.property())) {
      el = tableField.property();
      needAs = true;
    }
    if (JdbcType.UNDEFINED != jdbcType) {
      this.jdbcType = jdbcType;
      el += (COMMA + SqlScriptUtils.mappingJdbcType(jdbcType));
    }
    if (UnknownTypeHandler.class != typeHandler) {
      this.typeHandler = (Class<? extends TypeHandler<?>>) typeHandler;
      if (tableField.javaType()) {
        String javaType = null;
        TypeAliasRegistry registry = tableInfo.getConfiguration().getTypeAliasRegistry();
        Map<String, Class<?>> typeAliases = registry.getTypeAliases();
        for (Map.Entry<String, Class<?>> entry : typeAliases.entrySet()) {
          if (entry.getValue().equals(propertyType)) {
            javaType = entry.getKey();
            break;
          }
        }
        if (javaType == null) {
          javaType = propertyType.getName();
          registry.registerAlias(javaType, propertyType);
        }
        el += (COMMA + "javaType=" + javaType);
      }
      el += (COMMA + SqlScriptUtils.mappingTypeHandler(this.typeHandler));
    }
    if (StringUtils.isNotBlank(numericScale)) {
      el += (COMMA + SqlScriptUtils.mappingNumericScale(Integer.valueOf(numericScale)));
    }
    this.el = el;
    int index = el.indexOf(COMMA);
    this.mapping = index > 0 ? el.substring(++index) : null;
    this.initLogicDelete(dbConfig, field);

    String column = tableField.value();
    if (StringUtils.isBlank(column)) {
      column = this.property;
      if (tableInfo.isUnderCamel()) {
        /* 开启字段下划线申明 */
        column = StringUtils.camelToUnderline(column);
      }
      if (dbConfig.isCapitalMode()) {
        /* 开启字段全大写申明 */
        column = column.toUpperCase();
      }
    }
    String columnFormat = dbConfig.getColumnFormat();
    if (StringUtils.isNotBlank(columnFormat) && tableField.keepGlobalFormat()) {
      column = String.format(columnFormat, column);
    }

    this.column = column;
    this.sqlSelect = column;
    if (needAs) {
      // 存在指定转换属性
      String propertyFormat = dbConfig.getPropertyFormat();
      if (StringUtils.isBlank(propertyFormat)) {
        propertyFormat = "%s";
      }
      this.sqlSelect += (AS + String.format(propertyFormat, tableField.property()));
    } else if (tableInfo.getResultMap() == null && !tableInfo.isAutoInitResultMap() &&
        TableInfoHelper.checkRelated(tableInfo.isUnderCamel(), this.property, this.column)) {
      /* 未设置 resultMap 也未开启自动构建 resultMap, 字段规则又不符合 mybatis 的自动封装规则 */
      String propertyFormat = dbConfig.getPropertyFormat();
      String asProperty = this.property;
      if (StringUtils.isNotBlank(propertyFormat)) {
        asProperty = String.format(propertyFormat, this.property);
      }
      this.sqlSelect += (AS + asProperty);
    }

    if (StringUtils.isNotBlank(tableField.condition())) {
      // 细粒度条件控制
      this.condition = tableField.condition();
    }

  }

  /**
   * 排序初始化
   *
   * @param field 字段
   */
  private void initOrderBy(Field field) {
    OrderBy orderBy = field.getAnnotation(OrderBy.class);
    if (null != orderBy) {
      this.isOrderBy = true;
      this.orderBySort = orderBy.sort();
      String _orderBy = Constants.DESC;
      if (orderBy.asc() || !orderBy.isDesc()) {
        _orderBy = Constants.ASC;
      }
      this.orderByType = _orderBy;
    } else {
      this.isOrderBy = false;
    }
  }

  /**
   * 逻辑删除初始化
   *
   * @param dbConfig 数据库全局配置
   * @param field    字段属性对象
   */
  private void initLogicDelete(GlobalConfig.DbConfig dbConfig, Field field) {
    /* 获取注解属性，逻辑处理字段 */
    TableLogic tableLogic = field.getAnnotation(TableLogic.class);
    if (null != tableLogic) {
      if (StringUtils.isNotBlank(tableLogic.value())) {
        this.logicNotDeleteValue = tableLogic.value();
      } else {
        this.logicNotDeleteValue = dbConfig.getLogicNotDeleteValue();
      }
      if (StringUtils.isNotBlank(tableLogic.delval())) {
        this.logicDeleteValue = tableLogic.delval();
      } else {
        this.logicDeleteValue = dbConfig.getLogicDeleteValue();
      }
      this.logicDelete = true;
    } else {
      String deleteField = dbConfig.getLogicDeleteField();
      if (StringUtils.isNotBlank(deleteField)) {
        Matcher matcher = joinDeleted.matcher(this.property);
        if (matcher.find()) {
          String realField = matcher.group(1);
          realField = StringUtils.firstToLowerCase(realField);
          if (realField.equals(deleteField)) {
            this.logicNotDeleteValue = dbConfig.getLogicNotDeleteValue();
            this.logicDeleteValue = dbConfig.getLogicDeleteValue();
            this.logicDelete = true;
          }
        }
      }
    }
  }

  /**
   * 获取 ResultMapping
   *
   * @param configuration MybatisConfiguration
   * @return ResultMapping
   */
  ResultMapping getResultMapping(final Configuration configuration) {
    ResultMapping.Builder builder = new ResultMapping.Builder(configuration, property,
        StringUtils.getTargetColumn(column), propertyType);
    TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
    if (jdbcType != null && jdbcType != JdbcType.UNDEFINED) {
      builder.jdbcType(jdbcType);
    }
    if (typeHandler != null && typeHandler != UnknownTypeHandler.class) {
      TypeHandler<?> typeHandler = registry.getMappingTypeHandler(this.typeHandler);
      if (typeHandler == null) {
        typeHandler = registry.getInstance(propertyType, this.typeHandler);
      }
      builder.typeHandler(typeHandler);
    }
    return builder.build();
  }

  /**
   * 获取 查询的 sql 片段
   *
   * @param prefix 前缀
   * @return sql 脚本片段
   */
  public String getSqlWhere(final String prefix) {
    final String newPrefix = prefix == null ? EMPTY : prefix;
    // 默认:  AND column=#{prefix + el}
    String sqlScript = " AND " + String.format(condition, column, newPrefix + el);
    // 查询的时候只判非空
    return convertIf(sqlScript, convertIfProperty(newPrefix, property));
  }

  /**
   * 转换成 if 标签的脚本片段
   *
   * @param sqlScript sql 脚本片段
   * @param property  字段名
   * @return if 脚本片段
   */
  private String convertIf(final String sqlScript, final String property) {
    return SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", property), false);
  }

  private String convertIfProperty(String prefix, String property) {
    return StringUtils.isNotBlank(prefix) ? prefix.substring(0, prefix.length() - 1) + "['"
        + property + "']" : property;
  }
}

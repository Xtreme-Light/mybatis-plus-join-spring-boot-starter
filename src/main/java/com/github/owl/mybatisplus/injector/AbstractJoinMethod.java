package com.github.owl.mybatisplus.injector;

/**
 * <p>
 *
 * </p>
 *
 * @author light
 * @since 2022-08-27
 **/

import static java.util.stream.Collectors.joining;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.github.owl.mybatisplus.toolkit.TransferSqlTuple2;
import com.github.owl.mybatisplus.enums.JoinSqlKeyword;
import com.github.owl.mybatisplus.enums.JoinSqlMethod;
import com.github.owl.mybatisplus.metadata.JoinTableFieldInfo;
import com.github.owl.mybatisplus.metadata.JoinTableInfo;
import com.github.owl.mybatisplus.metadata.JoinTableInfo.JoinInfo;
import com.github.owl.mybatisplus.toolkit.sql.ConvertSqlScriptUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.jetbrains.annotations.NotNull;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;


/**
 * <p>
 * 抽象的注入方法
 * </p>
 *
 * @author light
 * @since 2022/8/8
 */
public abstract class AbstractJoinMethod implements Constants {

  protected static final Log logger = LogFactory.getLog(AbstractJoinMethod.class);


  protected Configuration configuration;
  protected LanguageDriver languageDriver;
  protected MapperBuilderAssistant builderAssistant;
  /**
   * 方法名称
   *
   * @since 3.5.0
   */
  protected final String methodName;

  /**
   * @param methodName 方法名
   * @since 3.5.0
   */
  protected AbstractJoinMethod(String methodName) {
    Assert.notNull(methodName, "方法名不能为空");
    this.methodName = methodName;
  }

  /**
   * 是否已经存在MappedStatement
   *
   * @param mappedStatement MappedStatement
   * @return true or false
   */
  private boolean hasMappedStatement(String mappedStatement) {
    return configuration.hasStatement(mappedStatement, false);
  }

  /**
   * SQL 注释
   *
   * @return sql
   */
  protected String sqlFirst() {
    return convertIfEwParam(Q_WRAPPER_SQL_FIRST, true);
  }


  protected String convertIfEwParam(final String param, final boolean newLine) {
    return SqlScriptUtils.convertIf(SqlScriptUtils.unSafeParam(param),
        String.format("%s != null and %s != null", WRAPPER, param), newLine);
  }

  /**
   * SQL 查询所有表字段
   *
   * @param table        表信息
   * @param queryWrapper 是否为使用 queryWrapper 查询
   * @return sql 脚本
   */
  protected String sqlSelectColumns(JoinTableInfo table, boolean queryWrapper) {
    /* 假设存在用户自定义的 resultMap 映射返回 */
    // 改成全量的filed
    List<JoinTableFieldInfo> fieldList = table.getFieldList();
    StringBuilder sb = new StringBuilder();
    for (JoinTableFieldInfo joinTableFieldInfo : fieldList) {
      sb.append(joinTableFieldInfo.getColumn()).append(" as ")
          .append(joinTableFieldInfo.getProperty()).append(",");
    }
    String selectColumns = sb.substring(0, sb.length() - 1);
    return convertChooseEwSelect(selectColumns);
  }


  protected String convertChooseEwSelect(final String otherwise) {
    return SqlScriptUtils.convertChoose(
        String.format("%s != null and %s != null", WRAPPER, Q_WRAPPER_SQL_SELECT),
        SqlScriptUtils.unSafeParam(Q_WRAPPER_SQL_SELECT), otherwise);
  }

  /**
   * 注入自定义方法
   *
   * @param builderAssistant 构建助手
   * @param mapperClass      mapper的类
   * @param modelClass       model的类
   * @param tableInfo        连表信息
   */
  public void inject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass,
      Class<?> modelClass, JoinTableInfo tableInfo) {
    this.configuration = builderAssistant.getConfiguration();
    this.builderAssistant = builderAssistant;
    this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
    /* 注入自定义方法 */
    injectMappedStatement(mapperClass, modelClass, tableInfo);
  }


  /**
   * 注入自定义 MappedStatement
   *
   * @param mapperClass mapper 接口
   * @param modelClass  mapper 泛型
   * @param tableInfo   数据库表反射信息
   * @return MappedStatement
   */
  public abstract MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      JoinTableInfo tableInfo);

  /**
   * EntityWrapper方式获取select where
   *
   * @param newLine 是否提到下一行
   * @param table   表信息
   * @return String
   */
  protected String sqlWhereEntityWrapper(boolean newLine, JoinTableInfo table) {
    String sqlScript;
    if (table.isWithLogicDelete()) {
      sqlScript = table.getAllSqlWhere(true, WRAPPER_ENTITY_DOT);
      sqlScript = SqlScriptUtils.convertIf(sqlScript,
          String.format("%s != null", WRAPPER_ENTITY),
          true);
      sqlScript += (NEWLINE + table.getLogicDeleteSql(true, true) + NEWLINE);
      String normalSqlScript = SqlScriptUtils.convertIf(
          String.format("AND ${%s}", WRAPPER_SQLSEGMENT),
          String.format("%s != null and %s != '' and %s", WRAPPER_SQLSEGMENT,
              WRAPPER_SQLSEGMENT,
              WRAPPER_NONEMPTYOFNORMAL), true);
      normalSqlScript += NEWLINE;
      normalSqlScript += SqlScriptUtils.convertIf(String.format(" ${%s}", WRAPPER_SQLSEGMENT),
          String.format("%s != null and %s != '' and %s", WRAPPER_SQLSEGMENT,
              WRAPPER_SQLSEGMENT,
              WRAPPER_EMPTYOFNORMAL), true);
      sqlScript += normalSqlScript;
      sqlScript = SqlScriptUtils.convertChoose(String.format("%s != null", WRAPPER),
          sqlScript,
          table.getLogicDeleteSql(false, true));
      sqlScript = SqlScriptUtils.convertWhere(sqlScript);
    } else {
      sqlScript = table.getAllSqlWhere(false, WRAPPER_ENTITY_DOT);
      sqlScript = SqlScriptUtils.convertIf(sqlScript,
          String.format("%s != null", WRAPPER_ENTITY), true);
      sqlScript += NEWLINE;
      sqlScript += SqlScriptUtils.convertIf(String.format(SqlScriptUtils.convertIf(" AND",
              String.format("%s and %s", WRAPPER_NONEMPTYOFENTITY, WRAPPER_NONEMPTYOFNORMAL),
              false) + " ${%s}", WRAPPER_SQLSEGMENT),
          String.format("%s != null and %s != '' and %s", WRAPPER_SQLSEGMENT,
              WRAPPER_SQLSEGMENT,
              WRAPPER_NONEMPTYOFWHERE), true);
      sqlScript = SqlScriptUtils.convertWhere(sqlScript) + NEWLINE;
      sqlScript += SqlScriptUtils.convertIf(String.format(" ${%s}", WRAPPER_SQLSEGMENT),
          String.format("%s != null and %s != '' and %s", WRAPPER_SQLSEGMENT,
              WRAPPER_SQLSEGMENT,
              WRAPPER_EMPTYOFWHERE), true);
      sqlScript = SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", WRAPPER),
          true);
    }
    return newLine ? NEWLINE + sqlScript : sqlScript;
  }


  protected String sqlOrderBy(JoinTableInfo tableInfo) {
    /* 不存在排序字段，直接返回空 */
    List<JoinTableFieldInfo> orderByFields = tableInfo.getOrderByFields();
    if (CollectionUtils.isEmpty(orderByFields)) {
      return StringPool.EMPTY;
    }
    orderByFields.sort(Comparator.comparingInt(JoinTableFieldInfo::getOrderBySort));
    String sql = NEWLINE + " ORDER BY "
        + orderByFields.stream().map(tfi -> String.format("%s %s", tfi.getColumn(),
        tfi.getOrderByType())).collect(joining(","));
    /* 当wrapper中传递了orderBy属性，@orderBy注解失效 */
    return SqlScriptUtils.convertIf(sql, String.format("%s == null or %s", WRAPPER,
        WRAPPER_EXPRESSION_ORDER), true);
  }

  /**
   * SQL 注释
   *
   * @return sql
   */
  protected String sqlComment() {
    return convertIfEwParam(Q_WRAPPER_SQL_COMMENT, true);
  }


  /**
   * 添加映射statement
   *
   * @param mapperClass mapper接口类
   * @param id          唯一标识
   * @param sqlSource   sql内容
   * @param table       连表信息
   * @return 映射的statement
   */
  protected MappedStatement addSelectMappedStatementForTable(Class<?> mapperClass, String id,
      SqlSource sqlSource,
      JoinTableInfo table) {
    String resultMap = table.getResultMap();
    if (null != resultMap) {
      /* 返回 resultMap 映射结果集 */
      return addMappedStatement(mapperClass, id, sqlSource,
          resultMap, null);
    } else {
      /* 普通查询 */
      return addSelectMappedStatementForOther(mapperClass, id, sqlSource,
          table.getEntityType());
    }
  }


  /**
   * 添加 MappedStatement 到 Mybatis 容器
   *
   * @param mapperClass mapper接口类
   * @param id          唯一标识
   * @param sqlSource   sql内容
   * @param resultMap   结果映射
   * @param resultType  返回结果类型
   * @return 映射的statement
   */
  protected MappedStatement addMappedStatement(Class<?> mapperClass, String id,
      SqlSource sqlSource,
      String resultMap, Class<?> resultType) {
    String statementName = mapperClass.getName() + DOT + id;
    if (hasMappedStatement(statementName)) {
      logger.warn(LEFT_SQ_BRACKET + statementName
          + "] Has been loaded by XML or SqlProvider or Mybatis's Annotation, so ignoring this injection for ["
          + getClass() + RIGHT_SQ_BRACKET);
      return null;
    }
    /* 缓存逻辑处理 */
//        boolean isSelect = SqlCommandType.SELECT == SqlCommandType.SELECT;
    return builderAssistant.addMappedStatement(id, sqlSource, StatementType.PREPARED,
        SqlCommandType.SELECT,
        null, null, null, null, resultMap, resultType,
        null, false, true, false, NoKeyGenerator.INSTANCE, null, null,
        configuration.getDatabaseId(), languageDriver, null);
  }

  /**
   * @param mapperClass mapper接口类
   * @param id          唯一标识
   * @param sqlSource   sql内容
   * @param resultType  返回结果类型
   * @return 映射的statement
   */
  protected MappedStatement addSelectMappedStatementForOther(Class<?> mapperClass, String id,
      SqlSource sqlSource,
      Class<?> resultType) {
    return addMappedStatement(mapperClass, id, sqlSource,
        null, resultType);
  }

  /**
   * 构建 on连接 语句
   *
   * @param joinSql 需要拼接的sql
   * @param tables  表名
   * @param on      连接信息
   * @param t2      别名
   * @param t1      别名
   */
  protected void buildOn(StringBuilder joinSql, String[] tables, String[] on, String t2,
      String t1) {
    joinSql
        .append(StringPool.SPACE)
        .append(tables[1])
        .append(StringPool.SPACE)
        .append(t2)
        .append(StringPool.SPACE)
        .append(JoinSqlKeyword.ON.getSqlSegment())
        .append(StringPool.SPACE)
        .append(t1)
        .append(StringPool.DOT)
        .append(on[0])
        .append(StringPool.SPACE)
        .append(SqlKeyword.EQ.getSqlSegment())
        .append(StringPool.SPACE)
        .append(t2)
        .append(StringPool.DOT)
        .append(on[1])
        .append(StringPool.SPACE);
  }

  /**
   * 构件join语句，所有的join类型相同的场景
   *
   * @param tableInfo      表信息
   * @param joinSqlKeyword join关键字
   * @return 字符
   */
  @NotNull
  protected StringBuilder buildJoin(JoinTableInfo tableInfo, JoinSqlKeyword joinSqlKeyword) {
    String mainTable = tableInfo.getMainTable();
    Map<String, String> tableAlias = tableInfo.getTableAlias();
    JoinInfo[] joinInfos = tableInfo.getJoinInfos();
    // left join 所有的表和表之间的on语句
    StringBuilder joinSql = new StringBuilder(mainTable);
    joinSql
        .append(StringPool.SPACE)
        .append(tableAlias.get(mainTable));
    for (JoinInfo joinInfo : joinInfos) {
      String[] tables = joinInfo.getTables();
      String[] on = joinInfo.getOn();
      String t2 = tableAlias.get(tables[1]);
      String t1 = tableAlias.get(tables[0]);
      joinSql.append(StringPool.SPACE)
          .append(joinSqlKeyword.getSqlSegment());
      buildOn(joinSql, tables, on, t2, t1);
    }
    return joinSql;
  }

  /**
   * SQL 查询记录行数
   *
   * @return count sql 脚本
   */
  protected String sqlCount() {
    return convertChooseEwSelect(ASTERISK);
  }

  protected MappedStatement joinSql(Class<?> mapperClass, Class<?> modelClass,
      JoinTableInfo tableInfo, JoinSqlMethod sqlMethod) {
    StringBuilder joinSql = buildJoin(tableInfo, JoinSqlKeyword.LEFT_JOIN);
    String sql = String.format(sqlMethod.getSql(),
        sqlFirst(), sqlSelectColumns(tableInfo, true),
        joinSql,
        sqlWhereEntityWrapper(true, tableInfo),
        sqlOrderBy(tableInfo), sqlComment());
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
    return this.addSelectMappedStatementForTable(mapperClass, methodName, sqlSource,
        tableInfo);
  }


  @NotNull
  protected StringBuilder buildCustomerJoin(JoinTableInfo tableInfo) {
    String mainTable = tableInfo.getMainTable();
    Map<String, String> tableAlias = tableInfo.getTableAlias();
    JoinInfo[] joinInfos = tableInfo.getJoinInfos();
    // left join 所有的表和表之间的on语句
    StringBuilder joinSql = new StringBuilder(mainTable);
    joinSql
        .append(StringPool.SPACE)
        .append(tableAlias.get(mainTable));

    for (int i = 0; i < joinInfos.length; i++) {
      JoinInfo joinInfo = joinInfos[i];
      String[] tables = joinInfo.getTables();
      String[] on = joinInfo.getOn();
      String t2 = tableAlias.get(tables[1]);
      String t1 = tableAlias.get(tables[0]);
      final List<TransferSqlTuple2<String, String>> lists = new ArrayList<>();
      lists.add(
          new TransferSqlTuple2<>(
              "items[" + i + "].sqlSegment == '" + JoinSqlKeyword.LEFT_JOIN.getSqlSegment()
                  + "'",
              JoinSqlKeyword.LEFT_JOIN.getSqlSegment()
          ));
      lists.add(new TransferSqlTuple2<>(
          "items[" + i + "].sqlSegment == '" + JoinSqlKeyword.RIGHT_JOIN.getSqlSegment()
              + "'",
          JoinSqlKeyword.RIGHT_JOIN.getSqlSegment()));
      lists.add(new TransferSqlTuple2<>(
          "items[" + i + "].sqlSegment == '" + JoinSqlKeyword.INNER_JOIN.getSqlSegment()
              + "'",
          JoinSqlKeyword.INNER_JOIN.getSqlSegment()
      ));
      joinSql.append(StringPool.SPACE)
          .append(ConvertSqlScriptUtils.convertChoose(
              lists, JoinSqlKeyword.LEFT_JOIN.getSqlSegment()))
      ;
      buildOn(joinSql, tables, on, t2, t1);
    }
    return joinSql;
  }


  protected MappedStatement buildCustomerJoinTotal(Class<?> mapperClass, Class<?> modelClass,
      JoinTableInfo tableInfo, JoinSqlMethod sqlMethod) {
    StringBuilder joinSql = buildCustomerJoin(tableInfo);
    String sql = String.format(sqlMethod.getSql(),
        sqlFirst(), sqlSelectColumns(tableInfo, true),
        joinSql,
        sqlWhereEntityWrapper(true, tableInfo),
        sqlOrderBy(tableInfo), sqlComment());
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
    return this.addSelectMappedStatementForTable(mapperClass, methodName, sqlSource,
        tableInfo);
  }
}

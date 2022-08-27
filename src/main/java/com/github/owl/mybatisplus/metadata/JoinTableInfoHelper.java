package com.github.owl.mybatisplus.metadata;

import static java.util.stream.Collectors.toList;

import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.config.GlobalConfig.DbConfig;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.owl.mybatisplus.annotations.JoinTable;
import com.github.owl.mybatisplus.annotations.JoinTables;
import com.github.owl.mybatisplus.exceptions.MybatisJoinConfigException;
import com.github.owl.mybatisplus.metadata.JoinTableInfo.JoinInfo;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.SimpleTypeRegistry;

/**
 * <p>
 * 实体类反射表辅助类
 * </p>
 *
 * @author light
 * @since 2022/8/9
 */
public class JoinTableInfoHelper {

  private static final Log logger = LogFactory.getLog(TableInfoHelper.class);
  /**
   * 储存反射类表信息
   */
  private static final Map<Class<?>, JoinTableInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>();

  /**
   * <p>
   * 获取实体映射表信息
   * </p>
   *
   * @param clazz 反射实体类
   * @return 数据库表反射信息
   */
  public static JoinTableInfo getTableInfos(Class<?> clazz) {
    if (clazz == null || clazz.isPrimitive() || SimpleTypeRegistry.isSimpleType(clazz)
        || clazz.isInterface()) {
      return null;
    }
    Class<?> targetClass = ClassUtils.getUserClass(clazz);
    JoinTableInfo tableInfo = TABLE_INFO_CACHE.get(targetClass);
    if (null != tableInfo) {
      return tableInfo;
    }
    //尝试获取父类缓存
    Class<?> currentClass = clazz;
    while (null == tableInfo && Object.class != currentClass) {
      currentClass = currentClass.getSuperclass();
      tableInfo = TABLE_INFO_CACHE.get(ClassUtils.getUserClass(currentClass));
    }

    //把父类的移到子类中来
    if (tableInfo != null) {
      TABLE_INFO_CACHE.put(targetClass, tableInfo);
    }
    return tableInfo;
  }


  /**
   * <p>
   * 通过一个clazz 和建造助手，来完善所需的所有信息
   * </p>
   *
   * @param builderAssistant 构件助手
   * @param clazz 反射实体类
   * @return 数据库表反射信息
   */
  public synchronized static JoinTableInfo initTableInfo(MapperBuilderAssistant builderAssistant,
      Class<?> clazz) {
    JoinTableInfo targetTableInfo = TABLE_INFO_CACHE.get(clazz);
    if (targetTableInfo != null) {
      return targetTableInfo;
    }
    return initTableInfo(builderAssistant.getConfiguration(),
        builderAssistant.getCurrentNamespace(), clazz);
  }

  /**
   * @param configuration    配置
   * @param currentNamespace 空间 由于TableInfo有对应的命名空间，那么意味着不同mapper接口的Table不能复用了
   * @param clazz            entity对象，类对象，通过他反射获取到对应的表信息
   * @return 发射得到的数据库表信息
   */
  private synchronized static JoinTableInfo initTableInfo(Configuration configuration,
      String currentNamespace, Class<?> clazz) {
    /* 没有获取到缓存信息,则初始化 */
    JoinTableInfo tableInfo = new JoinTableInfo(configuration, clazz);
    tableInfo.setCurrentNamespace(currentNamespace);
    GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);

    /* 采集注解信息 */
    gatherAnnotationInfo(clazz,globalConfig,tableInfo);
    /* 初始化字段相关 */
    initTableFields(clazz, globalConfig, tableInfo);
    /* 自动构建 resultMap */
    tableInfo.initResultMapIfNeed();

    TABLE_INFO_CACHE.put(clazz, tableInfo);
    /* 缓存 lambda */
//        LambdaUtils.installCache(tableInfo);
    return tableInfo;
  }


  /**
   * 采集注解信息
   * @param clazz 对应的entity
   * @param globalConfig 全局配置
   * @param tableInfo 需要组织的表信息
   */
  private static void gatherAnnotationInfo(Class<?> clazz, GlobalConfig globalConfig,
      JoinTableInfo tableInfo) {
    JoinTables joinTables = clazz.getAnnotation(JoinTables.class);
    DbConfig dbConfig = globalConfig.getDbConfig();
    List<String> tableNames = new ArrayList<>();
    String mainTable = null;
    Map<String, String> tableAlias = new HashMap<>();
    int index = 1;
    if (joinTables != null) {
      JoinTable[] joinTablesArray = joinTables.joinTables();
      if (joinTablesArray != null && joinTablesArray.length > 0) {
        String tablePrefix = dbConfig.getTablePrefix();
        String schema = dbConfig.getSchema();
        List<JoinInfo> joinInfos = new ArrayList<>();
        for (JoinTable joinTable : joinTablesArray) {
          String[] tables = joinTable.tables();
          if (tables == null || tables.length != 2) {
            throw new MybatisJoinConfigException("@JoinTable配置的tables长度不为2");
          }
          String[] on = joinTable.on();
          if (on == null || on.length != 2) {
            throw new MybatisJoinConfigException("@JoinTable配置的on连接字符数量不为2");
          }
          for (String table : tables) {
            String usedTableName = (StringUtils.isBlank(schema) ? "" : (schema + "."))
                +
                (StringUtils.isBlank(tablePrefix) ? "" : tablePrefix) + table;
            tableNames.add(usedTableName);
            String alias = tableAlias.get(usedTableName);
            if (StringUtils.isBlank(alias)) {
              tableAlias.put(usedTableName, "t" + index++);
            }
            if (mainTable == null) {
              mainTable = usedTableName;
              tableInfo.setMainTable(mainTable);
            }
          }
          joinInfos.add(new JoinInfo(tables, on));
        }
        tableInfo.setTableAlias(tableAlias);
        tableInfo.setJoinInfos(joinInfos.toArray(new JoinInfo[]{}));
        tableInfo.setTableNames(tableNames.toArray(new String[]{}));
      } else {
        throw new MybatisJoinConfigException("@JoinTable注解配置内容为空");
      }
    } else {
      throw new MybatisJoinConfigException("@JoinTable注解未配置");
    }
  }

  /**
   * <p>
   * 初始化 表主键,表字段
   * </p>
   *
   * @param clazz        实体类
   * @param globalConfig 全局配置
   * @param tableInfo    数据库表反射信息
   */
  private static void initTableFields(Class<?> clazz, GlobalConfig globalConfig,
      JoinTableInfo tableInfo) {
    /* 数据库全局配置 */
    GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
    Reflector reflector = tableInfo.getReflector();
    List<Field> list = getAllFields(clazz);
    // 是否存在 @TableLogic 注解
//    boolean existTableLogic = isExistTableLogic(list);
    List<JoinTableFieldInfo> fieldList = new ArrayList<>(list.size());
    for (Field field : list) {
      boolean isOrderBy = field.getAnnotation(OrderBy.class) != null;
      final TableField tableField = field.getAnnotation(TableField.class);
      /* 有 @TableField 注解的字段初始化 */
      if (tableField != null) {
        fieldList.add(
            new JoinTableFieldInfo(dbConfig, tableInfo, field, tableField, reflector, isOrderBy));
        continue;
      }
      /* 无 @TableField  注解的字段初始化 */
      fieldList.add(
          new JoinTableFieldInfo(dbConfig, tableInfo, field, reflector,isOrderBy));
    }
    /* 字段列表 */
    tableInfo.setFieldList(fieldList);

  }

  /**
   * <p>
   * 判断逻辑删除注解是否存在
   * </p>
   *
   * @param list 字段列表
   * @return true 为存在 {@link TableLogic} 注解;
   */
  public static boolean isExistTableLogic(List<Field> list) {
    return list.stream().anyMatch(field -> field.isAnnotationPresent(TableLogic.class));
  }


  /**
   * <p>
   * 获取该类的所有属性列表
   * </p>
   *
   * @param clazz 反射类
   * @return 属性集合
   */
  public static List<Field> getAllFields(Class<?> clazz) {
    List<Field> fieldList = ReflectionKit.getFieldList(ClassUtils.getUserClass(clazz));
    return fieldList.stream()
        .filter(field -> {
          /* 过滤注解非表字段属性 */
          TableField tableField = field.getAnnotation(TableField.class);
          return (tableField == null || tableField.exist());
        }).collect(toList());
  }

  /**
   * 根据 DbConfig 初始化 表名
   *
   * @param className 类名
   * @param dbConfig  DbConfig
   * @return 表名
   */
  private static String initTableNameWithDbConfig(String className,
      GlobalConfig.DbConfig dbConfig) {
    String tableName = className;
    // 开启表名下划线申明
    if (dbConfig.isTableUnderline()) {
      tableName = StringUtils.camelToUnderline(tableName);
    }
    // 大写命名判断
    if (dbConfig.isCapitalMode()) {
      tableName = tableName.toUpperCase();
    } else {
      // 首字母小写
      tableName = StringUtils.firstToLowerCase(tableName);
    }
    return tableName;
  }

}


package com.github.owl.mybatisplus.injector;


import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.github.owl.mybatisplus.mapper.JoinMapper;
import com.github.owl.mybatisplus.metadata.JoinTableInfo;
import com.github.owl.mybatisplus.metadata.JoinTableInfoHelper;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * <p>
 * join SQL自动注入器,只能在这里兼容利旧的AbstractSqlInjector，因为不能有两个。DefaultSqlInjector应该也持有
 * </p>
 *
 * @see com.baomidou.mybatisplus.core.injector.AbstractSqlInjector
 * @author light
 * @since 2022/8/10
 */
public abstract class AbstractJoinSqlInjector implements ISqlInjector {

  protected final Log logger = LogFactory.getLog(this.getClass());
  /**
   * 原SqlInject，默认持有原来的Sql注入器
   */
  protected ISqlInjector defaultSqlInjector = new DefaultSqlInjector();

  /**
   * 如果已经有其他的注入器，可以通过该方法注入后配合使用
   * @param defaultSqlInjector 提供的sql注入器
   */
  public void setDefaultSqlInjector(ISqlInjector defaultSqlInjector) {
    this.defaultSqlInjector = defaultSqlInjector;
  }

  @Override
  public void inspectInject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
    logger.trace("传入" + mapperClass);
    if (JoinMapper.class.isAssignableFrom(mapperClass)) {
      Class<?> modelClass = ReflectionKit.getSuperClassGenericType(mapperClass, Mapper.class, 0);
      if (modelClass != null) {
        String className = mapperClass.toString();
        Set<String> mapperRegistryCache = GlobalConfigUtils.getMapperRegistryCache(
            builderAssistant.getConfiguration());
        if (!mapperRegistryCache.contains(className)) {
          JoinTableInfo tableInfo = JoinTableInfoHelper.initTableInfo(builderAssistant,
              modelClass);
          List<AbstractJoinMethod> methodList = this.getMethodList(mapperClass, tableInfo);
          if (CollectionUtils.isNotEmpty(methodList)) {
            // 循环注入自定义方法
            methodList.forEach(
                m -> m.inject(builderAssistant, mapperClass, modelClass, tableInfo));
          } else {
            logger.debug(mapperClass + ", No effective injection method was found.");
          }
          mapperRegistryCache.add(className);
        }
      }
    } else {
      defaultSqlInjector.inspectInject(builderAssistant, mapperClass);

    }

  }

  /**
   * <p>
   * 获取 注入的方法
   * </p>
   *
   * @param mapperClass 当前mapper
   * @param tableInfo 连表信息
   * @return 注入的方法集合
   * @since 3.1.2 add  mapperClass
   */
  public abstract List<AbstractJoinMethod> getMethodList(Class<?> mapperClass,
      JoinTableInfo tableInfo);
}


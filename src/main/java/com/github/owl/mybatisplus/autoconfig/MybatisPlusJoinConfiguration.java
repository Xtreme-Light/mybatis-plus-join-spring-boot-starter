package com.github.owl.mybatisplus.autoconfig;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.github.owl.mybatisplus.injector.JoinSqlInjector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *  自动配置
 * </p>
 *
 * @author light
 * @since 2022-08-27
 **/
@Configuration(proxyBeanMethods = false)
public class MybatisPlusJoinConfiguration {


  @Bean
  @ConditionalOnMissingBean
  public ISqlInjector joinSqlInjector() {
    return new JoinSqlInjector();
  }
}

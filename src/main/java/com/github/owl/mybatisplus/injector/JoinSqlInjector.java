package com.github.owl.mybatisplus.injector;

import static java.util.stream.Collectors.toList;

import com.github.owl.mybatisplus.injector.methods.SelectInnerJoinCount;
import com.github.owl.mybatisplus.injector.methods.SelectInnerJoinList;
import com.github.owl.mybatisplus.injector.methods.SelectInnerJoinPage;
import com.github.owl.mybatisplus.injector.methods.SelectJoinCount;
import com.github.owl.mybatisplus.injector.methods.SelectJoinList;
import com.github.owl.mybatisplus.injector.methods.SelectJoinPage;
import com.github.owl.mybatisplus.injector.methods.SelectLeftJoinCount;
import com.github.owl.mybatisplus.injector.methods.SelectLeftJoinList;
import com.github.owl.mybatisplus.injector.methods.SelectLeftJoinPage;
import com.github.owl.mybatisplus.injector.methods.SelectRightJoinCount;
import com.github.owl.mybatisplus.injector.methods.SelectRightJoinList;
import com.github.owl.mybatisplus.injector.methods.SelectRightJoinPage;
import com.github.owl.mybatisplus.metadata.JoinTableInfo;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

/**
 * <p>
 * 为连表查询注入方法
 * </p>
 *
 * @author light
 * @since 2022/8/8
 */
public class JoinSqlInjector extends AbstractJoinSqlInjector {

  @Override
  public List<AbstractJoinMethod> getMethodList(Class<?> mapperClass, JoinTableInfo tableInfo) {
    Builder<AbstractJoinMethod> builder = Stream.<AbstractJoinMethod>builder()
        .add(new SelectLeftJoinList())
        .add(new SelectLeftJoinCount())
        .add(new SelectLeftJoinPage())
        .add(new SelectRightJoinList())
        .add(new SelectRightJoinCount())
        .add(new SelectRightJoinPage())
        .add(new SelectInnerJoinList())
        .add(new SelectInnerJoinCount())
        .add(new SelectInnerJoinPage())
        .add(new SelectJoinList())
        .add(new SelectJoinCount())
        .add(new SelectJoinPage())
        ;
    return builder.build().collect(toList());
  }
}

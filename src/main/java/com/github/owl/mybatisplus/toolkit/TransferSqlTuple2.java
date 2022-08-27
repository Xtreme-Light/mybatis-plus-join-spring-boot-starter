package com.github.owl.mybatisplus.toolkit;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 2元组工具类
 * </p>
 *
 * @author light
 * @since 2022-08-27
 **/
@Getter
@AllArgsConstructor
public class TransferSqlTuple2<T1, T2> {

  private final T1 t1;

  private final T2 t2;

}

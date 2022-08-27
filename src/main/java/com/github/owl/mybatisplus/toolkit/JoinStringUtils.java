package com.github.owl.mybatisplus.toolkit;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.github.owl.mybatisplus.exceptions.MybatisJoinConfigException;

/**
 * <p>
 * String工具类
 * </p>
 *
 * @author light
 * @since 2022/8/15
 */
public final class JoinStringUtils {

  private static final String DEFAULT_SUFFIX = "t";

  private JoinStringUtils() {

  }

  /**
   *
   * @param field 字段名字，形如 t1Name
   * @return 转为t1.name的sql接受的形式
   */
  public static String generateColumn(String field) {
    if (field.startsWith(DEFAULT_SUFFIX) && field.length() > 2) {
      char[] chars1 = field.toCharArray();
      if (Character.isDigit(chars1[1])) {
        String _columnName = field.substring(2);
        char[] chars = _columnName.toCharArray();
        if (chars[0] >= 65 && chars[0] <= 90) {
          chars[0] = (char) (chars[0] + 32);
        }
        _columnName = new String(chars);
        String _symbol = field.substring(0, 2);
        return _symbol + StringPool.DOT + _columnName;
      }
    }
    throw new MybatisJoinConfigException("字段名应该形如t1Name");
  }

}

package com.github.hanmz.util;

import org.junit.Test;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
public class FormatStringInnerUtilsTest {
  @Test
  public void formatString() throws Exception {
    String res = FormatStringUtils.formatString("han{},{}ming{}{}", 1, 2, 3, 4);
    System.out.println(res);

    res = FormatStringUtils.formatString("han{},{}ming{}{}", 1, 2, 3);
    System.out.println(res);

    res = FormatStringUtils.formatString("han{},{}ming{}{}", 1, 2, 3, 4, new Exception("123"));
    System.out.println(res);
  }

}
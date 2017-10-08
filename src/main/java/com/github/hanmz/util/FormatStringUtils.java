package com.github.hanmz.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
public class FormatStringUtils {
  private FormatStringUtils() {
  }

  public static String formatString(String format, Object... objects) {
    if (InnerUtils.isNullOrEmpty(objects)) {
      return format;
    }

    StringBuilder sb = new StringBuilder(format.length());

    int length = objects.length;

    int i = 0;
    int pos;
    int last = 0;
    while (i < length) {
      pos = format.indexOf("{}", last);
      if (pos < 0) {
        sb.append(format.substring(last));
        break;
      }
      sb.append(format.substring(last, pos)).append(objects[i++]);
      last = pos + 2;
    }

    while (i < length) {
      sb.append("\n").append(getMessage(objects[i++]));
    }

    return sb.toString();
  }

  public static String getMessage(Object obj) {
    if (obj == null) {
      return "";
    }

    if (obj instanceof Throwable) {
      StringWriter str = new StringWriter();
      PrintWriter pw = new PrintWriter(str);
      ((Throwable) obj).printStackTrace(pw);
      return str.toString();
    }
    return obj.toString();
  }
}

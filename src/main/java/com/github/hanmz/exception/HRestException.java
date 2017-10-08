package com.github.hanmz.exception;


import com.github.hanmz.util.FormatStringUtils;

import static com.github.hanmz.util.FormatStringUtils.formatString;


/**
 * *
 * Created by hanmz on 2017/9/10.
 */
public class HRestException extends RuntimeException {
  private HRestException(String errorMessage) {
    super(errorMessage);
  }

  private HRestException(String errorMessage, Throwable cause) {
    super(FormatStringUtils.getMessage(errorMessage) + " - " + FormatStringUtils.getMessage(cause), cause);
  }

  public static HRestException asHRestException(String message, Object... objects) {
    return new HRestException(formatString(message, objects));
  }


  public static HRestException asHRestException(Throwable cause) {
    if (cause instanceof HRestException) {
      return (HRestException) cause;
    }
    return new HRestException(FormatStringUtils.getMessage(cause), cause);
  }


}

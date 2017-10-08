package com.github.hanmz.util;

import com.github.hanmz.exception.HRestException;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * *
 * Created by hanmz on 2017/9/10.
 */
@UtilityClass
public class InnerUtils {
  /**
   * 对象null，集合empty
   */
  public static boolean isNullOrEmpty(Object i) {
    if (i == null) {
      return true;
    }
    Class<?> clz = i.getClass();
    if (clz.isArray()) { // Array
      return Array.getLength(i) == 0;
    } else if (Collection.class.isAssignableFrom(clz)) {  // Collection
      return ((Collection<?>) i).isEmpty();
    } else if (Map.class.isAssignableFrom(clz)) { // Map
      return ((Map<?, ?>) i).isEmpty();
    } else if (String.class.isAssignableFrom(clz)) { // String
      return ((String) i).isEmpty();
    }
    return false;
  }

  public static <T> T checkNotFoundHRestApi(T value) {
    if (value == null) {
      throw HRestException.asHRestException("Not found annotation HRestApi");
    }
    return value;
  }

  public <T> T checkNotNull(T value, String message) {
    if (value == null) {
      throw new NullPointerException(message);
    }
    return value;
  }

  public <T> T checkNotNull(T value) {
    if (value == null) {
      throw new NullPointerException();
    }
    return value;
  }

  public static boolean isAsync(Type type) {
    return type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().getTypeName().equals(CompletableFuture.class.getTypeName());
  }

  public static Type getType(Type type) {
    return isAsync(type) ? getParameterUpperBound(0, (ParameterizedType) type) : type;
  }

  public static Type getParameterUpperBound(int index, ParameterizedType type) {
    Type[] types = type.getActualTypeArguments();
    if (index < 0 || index >= types.length) {
      throw new IllegalArgumentException("Index " + index + " not in range [0," + types.length + ") for " + type);
    }
    Type paramType = types[index];
    if (paramType instanceof WildcardType) {
      return ((WildcardType) paramType).getUpperBounds()[0];
    }
    return paramType;
  }
}

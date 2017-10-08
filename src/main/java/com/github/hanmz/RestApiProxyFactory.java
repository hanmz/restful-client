package com.github.hanmz;

import com.github.hanmz.anno.HRestApi;
import com.github.hanmz.exception.HRestException;
import com.github.hanmz.retrofit.RetrofitBean;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.github.hanmz.util.InnerUtils.checkNotFoundHRestApi;
import static com.google.common.reflect.Reflection.newProxy;

/**
 * *
 * Created by hanmz on 2017/9/10.
 */
@Slf4j
public class RestApiProxyFactory {

  public static RestApiProxyFactory getInstance = LazyHolder.instance;
  private Map<Class<?>, Field> proxyFieldCache = Maps.newConcurrentMap();

  public <T> T create(final Class<T> clazz) {
    checkNotFoundHRestApi(clazz.getAnnotation(HRestApi.class));

    RetrofitBean.addApi(clazz);

    return newProxy(clazz, (Object proxy, Method method, Object[] args) -> {

      // If the method is a method from Object then defer to normal invocation.
      if (method.getDeclaringClass() == Object.class) {
        return method.invoke(this, args);
      }

      T apiObj = RetrofitBean.getApi(clazz);

      return proxyRetrofitExecute(method, args, apiObj);
    });
  }

  // TODO: hanmz 2017/9/10 最最核心的部分 ******
  private <T> Object proxyRetrofitExecute(Method method, Object[] args, T apiObj) throws Throwable {
    Field h = proxyFieldCache.computeIfAbsent(apiObj.getClass().getSuperclass(), key -> {
      try {
        Field field = key.getDeclaredField("h");
        field.setAccessible(true);
        return field;
      } catch (NoSuchFieldException e) {
        log.error("Reflect RetrofitMethodProxy Exception,{}", apiObj.getClass().getSimpleName(), e);
      }
      return null;
    });

    // 异步调用
    if (method.getReturnType().getTypeName().equals(CompletableFuture.class.getTypeName())) {
      // invoke的时候执行adapt方法
      return ((InvocationHandler) h.get(apiObj)).invoke(apiObj, method, args);
    }

    // TODO: hanmz 2017/9/10 获取call对象
    Call call = (Call) ((InvocationHandler) h.get(apiObj)).invoke(apiObj, method, args);
    return execute(call).body();
  }

  /**
   * 执行Retrofit方法
   */
  private <T> Response<T> execute(Call<T> call) {
    Response<T> response;
    try {
      response = call.execute();
    } catch (IOException e) {
      log.error("Request IO Exception:{}", call.request().url(), e);
      throw HRestException.asHRestException(e.getMessage());
    }

    if (!response.isSuccessful()) {
      try {
        log.error("Request is {}, Response code {}, message {}, errorBody {}", call.request().url(), response.code(), response.message(),
          response.errorBody() == null ? "" : response.errorBody().string());
      } catch (IOException e) {
        log.error("Response IO Exception:{}", call.request().url(), e);
        throw HRestException.asHRestException(e.getMessage());
      }
    }
    return response;
  }


  private static class LazyHolder {
    private static final RestApiProxyFactory instance = doCreate();

    private static RestApiProxyFactory doCreate() {
      return new RestApiProxyFactory();
    }
  }
}

package com.github.hanmz.interceptor;

import com.google.common.net.HttpHeaders;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 自定义interceptor，添加request header User-Agent
 * Created by hanmz on 2017/10/8.
 */
public class CustomInterceptor implements Interceptor {
  public static CustomInterceptor create() {
    return new CustomInterceptor();
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Request.Builder builder = request.newBuilder();
    builder.header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

    return chain.proceed(builder.build());
  }
}

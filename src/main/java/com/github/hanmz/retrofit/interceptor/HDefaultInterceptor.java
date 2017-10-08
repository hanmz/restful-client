package com.github.hanmz.retrofit.interceptor;

import com.google.common.net.HttpHeaders;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

/**
 * *
 * Created by hanmz on 2017/9/10.
 */
public class HDefaultInterceptor implements Interceptor {
  public static HDefaultInterceptor create() {
    return new HDefaultInterceptor();
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Request.Builder builder = request.newBuilder();
    builder.addHeader(HttpHeaders.CONTENT_TYPE, Objects.toString(request.header(HttpHeaders.CONTENT_TYPE), "application/json"))
           .addHeader(HttpHeaders.ACCEPT, Objects.toString(request.header(HttpHeaders.ACCEPT), "application/json"));

    return chain.proceed(builder.build());
  }
}

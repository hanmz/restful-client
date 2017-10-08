package com.github.hanmz.retrofit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hanmz.adapter.HCallAdapterFactory;
import com.github.hanmz.anno.HRestApi;
import com.github.hanmz.converter.HJacksonConverterFactory;
import com.github.hanmz.converter.HRestJacksonMapperBean;
import com.github.hanmz.exception.HRestException;
import com.github.hanmz.util.Configuration;
import com.github.hanmz.util.SslUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.hanmz.util.Constant.REST_ADDRESS;
import static com.github.hanmz.util.Constant.REST_CONNECTION_TIMEOUT;
import static com.github.hanmz.util.Constant.REST_KEEP_ALIVE_DURATION;
import static com.github.hanmz.util.Constant.REST_MAX_IDLE_CONNECTIONS;
import static com.github.hanmz.util.Constant.REST_MAX_REQUESTS;
import static com.github.hanmz.util.Constant.REST_MAX_REQUESTS_PER_HOST;
import static com.github.hanmz.util.Constant.REST_READ_TIMEOUT;
import static com.github.hanmz.util.Constant.REST_RETRY;
import static com.github.hanmz.util.Constant.REST_WRITE_TIMEOUT;

/**
 * *
 * Created by hanmz on 2017/9/10.
 */
@Slf4j
public class RetrofitBean {
  /**
   * 配置缓存
   */
  private static Map<String, ServiceConfig> configServiceCache = Maps.newConcurrentMap();
  /**
   * 配置ObjectMapper cache
   */
  private static Map<String, ObjectMapper> objectMapperCache = Maps.newConcurrentMap();
  /**
   * 客户端缓存
   */
  private static Map<Class<?>, Object> apiCache = Maps.newHashMap();

  private RetrofitBean() {
  }

  public static void addApi(final Class<?> service) {
    apiCache.put(service, getApiObj(service));
  }

  public static <T> T getApi(final Class<T> service) {
    return (T) apiCache.get(service);
  }

  private static OkHttpClient getClient(ServiceConfig config) {
    Dispatcher dispatcher = new Dispatcher();
    dispatcher.setMaxRequests(config.getMaxRequests());
    dispatcher.setMaxRequestsPerHost(config.getMaxRequestsPerHost());
    ConnectionPool connectionPool = new ConnectionPool(config.getMaxIdleConnections(), config.getKeepAliveDuration(), TimeUnit.SECONDS);
    OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(config.getConnectTimeout(), TimeUnit.SECONDS)
                                                             .readTimeout(config.getReadTimeout(), TimeUnit.SECONDS)
                                                             .writeTimeout(config.getWriteTimeout(), TimeUnit.SECONDS)
                                                             .retryOnConnectionFailure(config.isRetry())
                                                             .dispatcher(dispatcher)
                                                             .connectionPool(connectionPool);

    // ssl认证
    if (!Strings.isNullOrEmpty(config.getCertificate())) {
      try {
        File f = new File(config.getCertificate());
        builder.sslSocketFactory(SslUtils.createSSLSocketFactory(new FileInputStream(f))).hostnameVerifier((hostname, session) -> true);
      } catch (FileNotFoundException e) {
        throw HRestException.asHRestException("ssl证书没找到,{}", config.getCertificate());
      }
    } else {
      builder.sslSocketFactory(SslUtils.defaultSSLSocketFactory()).hostnameVerifier((hostname, session) -> true);
    }

    // 添加自定义拦截器等
    List<Interceptor> interceptors = config.getInterceptors();
    if (interceptors != null) {
      interceptors.parallelStream().forEach(builder::addInterceptor);
    }
    List<Interceptor> networkInterceptors = config.getNetworkInterceptors();
    if (networkInterceptors != null) {
      networkInterceptors.parallelStream().forEach(builder::addNetworkInterceptor);
    }
    return builder.build();
  }

  private static Object getApiObj(final Class<?> service) {
    String configName = service.getAnnotation(HRestApi.class).name();

    // build retrofit
    Retrofit retrofit = new Retrofit.Builder().client(getClient(configServiceCache.get(configName)))
                                              .addCallAdapterFactory(HCallAdapterFactory.create())
                                              .addConverterFactory(HJacksonConverterFactory.create(objectMapperCache.get(configName)))
                                              .baseUrl(configServiceCache.get(configName).getAddress())
                                              .build();
    return retrofit.create(service);
  }

  /**
   * 配置缓存
   */
  public static void buildConfigCache() throws HRestException {
    String content = "";

    try {
      Map<String, ServiceConfig> map = JSON.parseObject(content, new TypeReference<Map<String, ServiceConfig>>() {
      });
      configServiceCache.putAll(map);
    } catch (Exception e) {
      throw HRestException.asHRestException("config parse error " + e);
    }
  }

  public static ServiceConfig addConfig(String name, ServiceConfig config) {
    return configServiceCache.put(name, config);
  }

  /**
   * 从本地配置文件加载
   */
  public static ServiceConfig addLocalConfig(String name, Interceptor... interceptors) {
    Configuration configuration = Configuration.initFromLocal(name);
    ObjectMapper objectMapper = HRestJacksonMapperBean.getObject(name, configuration);
    objectMapperCache.put(name, objectMapper);

    ServiceConfig config = ServiceConfig.builder()
                                        .address(configuration.getString(REST_ADDRESS))
                                        .retry(configuration.getBool(REST_RETRY, true))
                                        .connectTimeout(configuration.getInt(REST_CONNECTION_TIMEOUT, 5))
                                        .writeTimeout(configuration.getInt(REST_WRITE_TIMEOUT, 30))
                                        .readTimeout(configuration.getInt(REST_READ_TIMEOUT, 30))
                                        .maxRequests(configuration.getInt(REST_MAX_REQUESTS, 64))
                                        .maxRequestsPerHost(configuration.getInt(REST_MAX_REQUESTS_PER_HOST, 5))
                                        .maxIdleConnections(configuration.getInt(REST_MAX_IDLE_CONNECTIONS, 64))
                                        .keepAliveDuration(configuration.getInt(REST_KEEP_ALIVE_DURATION, 5))
                                        .addInterceptors(Lists.newArrayList(interceptors))
                                        .build();
    configServiceCache.put(name, config);
    return config;
  }

  /**
   * 从自定义配置加载
   */
  public static ServiceConfig addConfig(String name, Configuration configuration) {
    ObjectMapper objectMapper = HRestJacksonMapperBean.getObject(name, configuration);
    objectMapperCache.put(name, objectMapper);

    ServiceConfig config = ServiceConfig.builder().address(configuration.getString("address")).build();
    return configServiceCache.put(name, config);
  }

  /**
   * 配置缓存
   */
  public static void buildObjectMapperCache() throws HRestException {
    String content = "";

    try {
      Map<String, ObjectMapper> map = JSON.parseObject(content, new TypeReference<Map<String, ObjectMapper>>() {
      });
      objectMapperCache.putAll(map);
    } catch (Exception e) {
      throw HRestException.asHRestException("config parse error " + e);
    }
  }

  public static void addObjectMapper(String name, ObjectMapper objectMapper) {
    objectMapperCache.put(name, objectMapper);
  }
}

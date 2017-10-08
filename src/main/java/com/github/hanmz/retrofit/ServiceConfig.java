package com.github.hanmz.retrofit;

import com.github.hanmz.retrofit.interceptor.HDefaultInterceptor;
import com.github.hanmz.util.InnerUtils;
import com.google.common.collect.Lists;
import lombok.Data;
import okhttp3.Interceptor;

import java.util.List;

/**
 * *
 * Created by hanmz on 2017/9/10.
 */
@Data
public class ServiceConfig {
  /**
   * 服务端地址
   */
  private String address;
  /**
   * 是否重试，默认开启
   */
  private boolean retry;
  /**
   * 连接、读、写超时
   */
  private int connectTimeout;
  private int readTimeout;
  private int writeTimeout;
  /**
   * Asynchronous http request
   * Dispatcher 配置
   */
  private int maxRequests;
  private int maxRequestsPerHost;
  /**
   * Synchronous http request
   * ConnectionPool 配置
   */
  private int maxIdleConnections;
  // keepAliveDuration要小于被请求服务端的长连接时间,
  // 否则会报eof异常(被请求服务端可能配置会在一定时间内关闭与客户端的空闲连接)
  private int keepAliveDuration;

  /**
   * ssl证书
   */
  private String certificate;

  /**
   * 拦截器
   */
  private List<Interceptor> interceptors = Lists.newArrayList();

  private List<Interceptor> networkInterceptors = Lists.newArrayList();

  public static Builder builder() {
    return new Builder();
  }

  public ServiceConfig addInterceptor(Interceptor interceptor) {
    interceptors.add(interceptor);
    return this;
  }

  public ServiceConfig addInterceptors(Interceptor... interceptors) {
    if (!InnerUtils.isNullOrEmpty(interceptors)) {
      this.interceptors.addAll(Lists.newArrayList(interceptors));
    }
    return this;
  }

  public ServiceConfig interceptor(Interceptor interceptor) {
    interceptors = Lists.newArrayList(interceptor);
    return this;
  }

  public ServiceConfig interceptor(List<Interceptor> interceptors) {
    this.interceptors = interceptors;
    return this;
  }

  public ServiceConfig addNetworkInterceptor(Interceptor interceptor) {
    networkInterceptors.add(interceptor);
    return this;
  }

  public ServiceConfig addNetworkInterceptor(List<Interceptor> interceptors) {
    networkInterceptors.addAll(interceptors);
    return this;
  }


  public static class Builder {
    private String address;
    /**
     * 默认添加重试
     */
    private boolean retry = true;
    private int connectTimeout = 5;
    private int readTimeout = 30;
    private int writeTimeout = 30;
    private int maxRequests = 64;
    private int maxRequestsPerHost = 5;
    private int maxIdleConnections = 5;
    private int keepAliveDuration = 5;
    private String certificate;
    private List<Interceptor> interceptors = Lists.newArrayList(HDefaultInterceptor.create());
    private List<Interceptor> networkInterceptors = Lists.newArrayList();

    public Builder address(String address) {
      this.address = address;
      return this;
    }

    public Builder retry(boolean retry) {
      this.retry = retry;
      return this;
    }

    public Builder connectTimeout(int connectTimeout) {
      this.connectTimeout = connectTimeout;
      return this;
    }

    public Builder readTimeout(int readTimeout) {
      this.readTimeout = readTimeout;
      return this;
    }

    public Builder writeTimeout(int writeTimeout) {
      this.writeTimeout = writeTimeout;
      return this;
    }

    public Builder maxRequests(int maxRequests) {
      this.maxRequests = maxRequests;
      return this;
    }

    public Builder maxRequestsPerHost(int maxRequestsPerHost) {
      this.maxRequestsPerHost = maxRequestsPerHost;
      return this;
    }

    public Builder maxIdleConnections(int maxIdleConnections) {
      this.maxIdleConnections = maxIdleConnections;
      return this;
    }

    public Builder keepAliveDuration(int keepAliveDuration) {
      this.keepAliveDuration = keepAliveDuration;
      return this;
    }

    public Builder certificate(String certificate) {
      this.certificate = certificate;
      return this;
    }

    public Builder addInterceptor(Interceptor interceptor) {
      interceptors.add(interceptor);
      return this;
    }

    public Builder addInterceptors(List<Interceptor> interceptors) {
      this.interceptors.addAll(interceptors);
      return this;
    }

    public Builder interceptor(Interceptor interceptor) {
      interceptors = Lists.newArrayList(interceptor);
      return this;
    }

    public Builder interceptor(List<Interceptor> interceptors) {
      this.interceptors = interceptors;
      return this;
    }

    public Builder addNetworkInterceptor(Interceptor interceptor) {
      networkInterceptors.add(interceptor);
      return this;
    }

    public Builder addNetworkInterceptor(List<Interceptor> interceptors) {
      networkInterceptors.addAll(interceptors);
      return this;
    }

    public ServiceConfig build() {
      ServiceConfig serviceConfig = new ServiceConfig();
      serviceConfig.setAddress(address);
      serviceConfig.setRetry(retry);
      serviceConfig.setConnectTimeout(connectTimeout);
      serviceConfig.setReadTimeout(readTimeout);
      serviceConfig.setWriteTimeout(writeTimeout);
      serviceConfig.setMaxIdleConnections(maxIdleConnections);
      serviceConfig.setMaxRequests(maxRequests);
      serviceConfig.setMaxRequestsPerHost(maxRequestsPerHost);
      serviceConfig.setKeepAliveDuration(keepAliveDuration);
      serviceConfig.setCertificate(certificate);
      serviceConfig.setInterceptors(interceptors);
      serviceConfig.setNetworkInterceptors(networkInterceptors);

      return serviceConfig;
    }
  }
}

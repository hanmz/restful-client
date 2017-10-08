package com.github.hanmz.util;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
public abstract class Constant {
  /**
   * OkHttpclient配置
   */
  public static final String REST_ADDRESS = "rest.address";
  public static final String REST_RETRY = "rest.retry";
  public static final String REST_READ_TIMEOUT = "rest.readTimeout";
  public static final String REST_WRITE_TIMEOUT = "rest.writeTimeout";
  public static final String REST_CONNECTION_TIMEOUT = "rest.connectTimeout";
  /**
   * 异步请求
   */
  public static final String REST_MAX_REQUESTS = "rest.maxRequests";
  public static final String REST_MAX_REQUESTS_PER_HOST = "rest.maxRequestsPerHost";

  /**
   * 连接池配置
   */
  public static final String REST_MAX_IDLE_CONNECTIONS = "rest.maxIdleConnections";
  public static final String REST_KEEP_ALIVE_DURATION = "rest.keepAliveDuration";

  /**
   * ssl证书
   */
  public static final String REST_CERTIFICATE = "rest.certificate";


  /**
   * 序列化请求相应实体配置
   */
  public static final String SERIALIZE_PROPERTY_NAMING_STRATEGY = "serialize.propertyNamingStrategy";
  public static final String SERIALIZE_SERIALIAZTION_FRATURE = "serialize.serializationFeature";
  public static final String SERIALIZE_DESERIALIAZTION_FRATURE = "serialize.deserializationFeature";

  private Constant() {
  }
}

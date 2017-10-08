package com.github.hanmz.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.hanmz.util.InnerUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.RequestBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * *
 * Created by hanmz on 2017/9/10.
 */
@Slf4j
public class HJacksonConverterFactory extends Converter.Factory {

  private final ObjectMapper mapper;

  private HJacksonConverterFactory(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * Create an instance using a default {@link ObjectMapper} instance for conversion.
   */
  public static HJacksonConverterFactory create() {
    return create(new ObjectMapper());
  }

  /**
   * Create an instance using {@code mapper} for conversion.
   */
  @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
  public static HJacksonConverterFactory create(ObjectMapper mapper) {
    if (mapper == null) {
      log.warn("mapper is null");
      return new HJacksonConverterFactory(new ObjectMapper());
    }
    return new HJacksonConverterFactory(mapper);
  }

  @Override
  public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
    JavaType javaType = mapper.getTypeFactory().constructType(type);
    ObjectWriter writer = mapper.writerFor(javaType);
    return new HJacksonRequestBodyConverter<>(writer);
  }

  @Override
  public HJacksonResponseBodyConverter responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
    Type innerType = InnerUtils.getType(type);
    JavaType javaType = mapper.getTypeFactory().constructType(innerType);
    ObjectReader reader = mapper.readerFor(javaType);
    return new HJacksonResponseBodyConverter<>(reader);
  }
}

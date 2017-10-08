package com.github.hanmz.converter;

import com.fasterxml.jackson.databind.ObjectWriter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

import java.io.IOException;

/**
 * *
 * Created by hanmz on 2017/9/10.
 */
public class HJacksonRequestBodyConverter<T> implements Converter<T, RequestBody> {
  private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");


  private ObjectWriter reader;

  HJacksonRequestBodyConverter(ObjectWriter reader) {
    this.reader = reader;
  }

  @Override
  public RequestBody convert(T value) throws IOException {
    byte[] bytes = reader.writeValueAsBytes(value);
    return RequestBody.create(MEDIA_TYPE, bytes);
  }
}

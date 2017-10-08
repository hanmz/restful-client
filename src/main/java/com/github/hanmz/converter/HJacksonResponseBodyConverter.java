package com.github.hanmz.converter;

import com.fasterxml.jackson.databind.ObjectReader;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.IOException;

/**
 * *
 * Created by hanmz on 2017/9/10.
 */
public class HJacksonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
  private final ObjectReader adapter;

  HJacksonResponseBodyConverter(ObjectReader adapter) {
    this.adapter = adapter;
  }

  @Override
  public T convert(ResponseBody body) throws IOException {
    // 返回值为null
    if (body.contentLength() == 0) {
      return null;
    }

    try {
      return adapter.readValue(body.charStream());
    } finally {
      body.close();
    }
  }
}

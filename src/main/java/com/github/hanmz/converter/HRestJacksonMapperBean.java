package com.github.hanmz.converter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.hanmz.exception.HRestException;
import com.github.hanmz.util.Configuration;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.EnumUtils;

import java.util.Map;
import java.util.Objects;

import static com.github.hanmz.util.Constant.SERIALIZE_DESERIALIAZTION_FRATURE;
import static com.github.hanmz.util.Constant.SERIALIZE_PROPERTY_NAMING_STRATEGY;
import static com.github.hanmz.util.Constant.SERIALIZE_SERIALIAZTION_FRATURE;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
public class HRestJacksonMapperBean {

  private static final Map<String, PropertyNamingStrategy> propertyNamingStrategyBaseMap = Maps.newConcurrentMap();
  private static final Map<String, ObjectMapper> serializableConfigObjCache = Maps.newConcurrentMap();

  static {
    propertyNamingStrategyBaseMap.put("SNAKE_CASE", PropertyNamingStrategy.SNAKE_CASE);
    propertyNamingStrategyBaseMap.put("UPPER_CAMEL_CASE", PropertyNamingStrategy.UPPER_CAMEL_CASE);
    propertyNamingStrategyBaseMap.put("LOWER_CAMEL_CASE", PropertyNamingStrategy.LOWER_CAMEL_CASE);
    propertyNamingStrategyBaseMap.put("LOWER_CASE", PropertyNamingStrategy.LOWER_CASE);
    propertyNamingStrategyBaseMap.put("KEBAB_CASE", PropertyNamingStrategy.KEBAB_CASE);
  }

  private HRestJacksonMapperBean() {
  }

  public static ObjectMapper getObject(String serializableConfigName, Configuration configuration) {
    return serializableConfigObjCache.computeIfAbsent(Objects.toString(serializableConfigName, "DEFAULT"), key -> {
      ObjectMapper defaultInstance = new ObjectMapper();
      defaultInstance.findAndRegisterModules();
      defaultInstance.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
      defaultInstance.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
      defaultInstance.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      defaultInstance.configure(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING, true);
      defaultInstance.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      defaultInstance.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      afterPropertiesSet(defaultInstance, configuration);
      return defaultInstance;
    });
  }

  private static void afterPropertiesSet(ObjectMapper objectMapper, Configuration configuration) {

    String propertyNamingStrategy = configuration.getString(SERIALIZE_PROPERTY_NAMING_STRATEGY, "SNAKE_CASE");
    if (!propertyNamingStrategyBaseMap.containsKey(propertyNamingStrategy)) {
      throw HRestException.asHRestException("property_naming_strategy  value invalid,support list: {}" + propertyNamingStrategyBaseMap.keySet().toString());
    }
    objectMapper.setPropertyNamingStrategy(propertyNamingStrategyBaseMap.get(propertyNamingStrategy));

    Map<String, String> serializationFeatureMap = Splitter.on(',')
                                                          .omitEmptyStrings()
                                                          .trimResults()
                                                          .withKeyValueSeparator("=")
                                                          .split(configuration.getString(SERIALIZE_SERIALIAZTION_FRATURE, ""));
    serializationFeatureMap.forEach((key, value) -> {
      if (EnumUtils.isValidEnum(SerializationFeature.class, key)) {
        if (!Objects.equals(value.toLowerCase(), "true") && !Objects.equals(value.toLowerCase(), "false")) {
          throw HRestException.asHRestException(key + " invalid value,Must Boolean Type");
        }
        objectMapper.configure(EnumUtils.getEnum(SerializationFeature.class, key), Boolean.valueOf(value));
        return;
      }
      throw HRestException.asHRestException(key + " invalid,jackson not support");
    });

    Map<String, String> deSerializationFeatureMap = Splitter.on(',')
                                                            .omitEmptyStrings()
                                                            .trimResults()
                                                            .withKeyValueSeparator("=")
                                                            .split(configuration.getString(SERIALIZE_DESERIALIAZTION_FRATURE, ""));
    deSerializationFeatureMap.forEach((key, value) -> {
      if (EnumUtils.isValidEnum(DeserializationFeature.class, key)) {
        if (!Objects.equals(value.toLowerCase(), "true") && !Objects.equals(value.toLowerCase(), "false")) {
          throw HRestException.asHRestException(key + " invalid value,Must Boolean Type");
        }
        objectMapper.configure(EnumUtils.getEnum(DeserializationFeature.class, key), Boolean.valueOf(value));
        return;
      }
      throw HRestException.asHRestException(" {} invalid,jackson not support", key);
    });

  }
}

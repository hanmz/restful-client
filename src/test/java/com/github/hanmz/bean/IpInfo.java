package com.github.hanmz.bean;

import lombok.Data;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
@Data
public class IpInfo {
  private int code;
  private Data data;

  @lombok.Data
  private class Data {
    private String country;
    private String countryId;
    private String area;
    private String areaId;
  }
}

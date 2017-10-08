package com.github.hanmz.api;

import com.github.hanmz.anno.HRestApi;
import com.github.hanmz.bean.IpInfo;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.concurrent.CompletableFuture;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
@HRestApi(name = "ip-info.json")
public interface IpInfoApi {
  @GET("getIpInfo.php")
  IpInfo find(@Query("ip") String ip);

  @GET("getIpInfo.php")
  CompletableFuture<IpInfo> findFuture(@Query("ip") String ip);
}

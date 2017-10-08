package com.github.hanmz;

import com.github.hanmz.api.IpInfoApi;
import com.github.hanmz.bean.IpInfo;
import com.github.hanmz.interceptor.CustomInterceptor;
import com.github.hanmz.retrofit.RetrofitBean;
import com.google.common.util.concurrent.Uninterruptibles;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * *
 * Created by hanmz on 2017/10/8.
 */
@Slf4j
public class IpInfoApiTest {
  @Before
  public void init() {
    RetrofitBean.addLocalConfig("ip-info.json", CustomInterceptor.create());
  }

  @Test
  public void syncTest() throws Exception {
    IpInfoApi api = RestApiProxyFactory.getInstance.create(IpInfoApi.class);

    long start = System.currentTimeMillis();
    IpInfo info = api.find("63.223.108.42");
    log.info("执行时间：{}", System.currentTimeMillis() - start);

    assert info.getCode() == 0;
  }

  @Test
  public void asyncTest() throws Exception {
    IpInfoApi api = RestApiProxyFactory.getInstance.create(IpInfoApi.class);

    long start = System.currentTimeMillis();
    CompletableFuture<IpInfo> futureInfo = api.findFuture("63.223.108.42");
    log.info("执行时间：{}", System.currentTimeMillis() - start);


    IpInfo info = new IpInfo();
    info.setCode(-1);
    assert futureInfo.getNow(info).getCode() == -1;

    Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);

    assert futureInfo.get().getCode() == 0;
  }
}

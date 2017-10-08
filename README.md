### Restful Client

## 说明
对retrofit 2.0的封装，提供一套更加符合java rpc调用方式的http client。
支持同步和异步请求。
使用简单，扩展性强。

## 使用

    // 同步
    IpInfoApi api = RestApiProxyFactory.getInstance.create(IpInfoApi.class);
    IpInfo info = api.find("63.223.108.42");

    // 异步
    CompletableFuture<IpInfo> futureInfo = api.findFuture("63.223.108.42");

详情参见IpInfoApiTest
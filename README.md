# ipsdk_java_sample

用于永安在线ip画像sdk，读取数据库规则打分，并输出结果。
该项目需要依赖算分规则jar包，请联系商务，售后获取。

## 部署流程

### 1. 下载打分规则

[样例代码](https://github.com/threathunterX/ipsdk_java_sample/blob/master/src/main/java/com/yazx/demo/GetIpScoreService.java)

### 2. 初始化查库实现类

样例：
- [ipv4实时查询 mysql版本](https://github.com/threathunterX/ipsdk_java_sample/blob/master/src/main/java/com/yazx/demo/IpRealtimeDataFinderMysqlImpl.java)
- [ipv4历史查询 mysql版本](https://github.com/threathunterX/ipsdk_java_sample/blob/master/src/main/java/com/yazx/demo/IpRealtimeDataFinderMysqlImpl.java)
- [ipv4实时查询 redis版本](https://github.com/threathunterX/ipsdk_java_sample/blob/master/src/main/java/com/yazx/demo/IpRealtimeDataFinderMysqlImpl.java)
- [ipv6查询 redis版本](https://github.com/threathunterX/ipsdk_java_sample/blob/master/src/main/java/com/yazx/demo/IpRealtimeDataFinderMysqlImpl.java)

### 3. 创建ip查询服务
样例：
ipv4:
```java
IpCheckService ipCheckService = new IpCheckServiceImpl(scoreConf, ipRealtimeFinder,
                finderMysql,
                ipBaseFinder);

```
ipv6:
```java
Ipv6CheckService ipv6CheckService = new Ipv6CheckServiceImpl(scoreConf, finder, finder);
```

### 4. 查询ip 
```
IpData data = ipCheckService.checkRealtimeIp("183.197.198.69", true);
            System.out.println(data);
            data = ipCheckService.checkHistoryIp("36.104.211.44", 1670282889, true);
            System.out.println(ipData);
            Ipv6Data check = ipv6CheckService.check("240e:0320:0d03", "1977", 1667889809);
            System.out.println(check);
            check = ipv6CheckService.check("240e:0320:0d03", "1977", System.currentTimeMillis() / 1000);
            System.out.println(check);
```

### 完整查询样例：
[查询样例](https://github.com/threathunterX/ipsdk_java_sample/blob/master/src/main/java/com/yazx/Main.java)


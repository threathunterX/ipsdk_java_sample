package com.yazx;

import com.yazx.dao.IIpHistoryDataFinder;
import com.yazx.dao.IIpRealtimeDataFinder;
import com.yazx.dao.IpBaseInfoFinder;
import com.yazx.demo.GetIpScoreService;
import com.yazx.demo.IpHistoryDataFinderMysqlImpl;
import com.yazx.demo.IpRealtimeDataFinderSimpleRedisImpl;
import com.yazx.demo.Ipv6DataFinderRedisImpl;
import com.yazx.model.*;
import com.yazx.service.IpCheckService;
import com.yazx.service.Ipv6CheckService;
import com.yazx.service.impl.IpCheckServiceImpl;
import com.yazx.service.impl.Ipv6CheckServiceImpl;
import com.yazx.utils.Ipv6Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 1. 获取打分规则
        GetIpScoreService scoreService = new GetIpScoreService("snuser", "snkey");
        ScoreConf scoreConf = scoreService.reqScoreConf();

        // 2. 初始化数据库 以及相关实现类
        IpRealtimeDataFinderSimpleRedisImpl simpleRedis = new IpRealtimeDataFinderSimpleRedisImpl(new RedisConf(
                new String[]{"localhost:6379"}, null, "123456"
        ));

        IIpHistoryDataFinder finderMysql = new IpHistoryDataFinderMysqlImpl(new MysqlConf(
                "",
                "jdbc:mysql://localhost:3306/ipv4?useUnicode=true&characterEncoding=utf-8&useSSL=false",
                "root",
                "123450"
        ), scoreConf.getMaxExpireTime());

        //3. 创建查询service
        IpCheckService ipCheckService = new IpCheckServiceImpl(scoreConf, simpleRedis,
                finderMysql,
                simpleRedis);

        Ipv6DataFinderRedisImpl finder = new Ipv6DataFinderRedisImpl(new RedisConf(
                new String[]{"localhost:6379/1"}, null, "123456"
        ));
        Ipv6CheckService ipv6CheckService = new Ipv6CheckServiceImpl(scoreConf, finder, finder);

        // 4. 查询数据
        String extendIpv6 = Ipv6Util.extendIpv6("240e:0320:0d03:1977::");
        String prefix = Ipv6Util.getPrefix(extendIpv6);
        String subnet = Ipv6Util.getSubnet(extendIpv6);
        try {
            Ipv6Data check = ipv6CheckService.check(prefix, subnet, 1667889809);
            System.out.println(check);
            check = ipv6CheckService.check(prefix, subnet, System.currentTimeMillis() / 1000);
            System.out.println(check);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
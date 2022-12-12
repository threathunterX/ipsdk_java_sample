package com.yazx.demo;

import com.yazx.dao.IIpv6BaseFinder;
import com.yazx.dao.IIpv6RiskFinder;
import com.yazx.model.IpLocation;
import com.yazx.model.Ipv6RiskData;
import com.yazx.model.RedisConf;
import io.lettuce.core.*;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yazx
 *
 * 用于cluster集群模式redis查询ipv6的实现类，
 */
public class Ipv6DataFinderRedisClusterImpl implements IIpv6BaseFinder, IIpv6RiskFinder {
    private RedisAdvancedClusterCommands<String, String> redisCli;

    public Ipv6DataFinderRedisClusterImpl(RedisConf conf) {
        RedisClusterClient client;
        try {
            client = RedisClusterClient.create(Arrays.stream(conf.getHosts()).map(item -> {
                RedisURI uri = RedisURI.create(item);
                uri.setCredentialsProvider(new StaticCredentialsProvider(RedisCredentials.just(conf.getUsername(), conf.getPassword())));
                return uri;
            }).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        this.redisCli = client.connect().sync();
    }

    @Override
    public IpLocation findIpBaseData(String prefix) throws Exception {
        String s = redisCli.get("ipv6_base_" + prefix);
        return decodeIpv6Location(s);
    }

    @Override
    public ArrayList<Ipv6RiskData> findIpOrderedData(String prefix, String subnet, long collectTime, int validTimeMin) throws Exception {
        return redisCli.zrevrangebyscore("ipv6_order_" + prefix, Range.create(collectTime - validTimeMin * 60L, collectTime))
                .stream().filter(Objects::nonNull).filter(it -> {
                    String[] split = it.split("_");
                    return split.length == 2;
                }).map(it -> {
                    String[] split = it.split("_");
                    return new Ipv6RiskData(prefix, split[0], Integer.parseInt(split[1]));
                }).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Ipv6RiskData findIpOrderedRightData(String prefix, String subnet, long collectTime) throws Exception {
        return redisCli.zrangebyscore("ipv6_order_" + prefix, Range.create(collectTime, Integer.MAX_VALUE), Limit.from(1))
                .stream().filter(Objects::nonNull).filter(it -> {
                    String[] split = it.split("_");
                    return split.length == 2;
                }).findAny().map(it -> {
                    String[] split = it.split("_");
                    return new Ipv6RiskData(prefix, split[0], Integer.parseInt(split[1]));
                }).orElse(null);
    }

    @Override
    public ArrayList<Ipv6RiskData> findIpRandomData(String prefix, String subnet, long collectTime, int validTimeMin) throws Exception {
        return redisCli.zrevrangebyscore("ipv6_random_" + prefix, Range.create(collectTime - validTimeMin * 60L, collectTime))
                .stream().filter(Objects::nonNull).filter(it -> {
                    String[] split = it.split("_");
                    return split.length == 2;
                }).map(it -> {
                    String[] split = it.split("_");
                    return new Ipv6RiskData(prefix, split[0], Integer.parseInt(split[1]));
                }).collect(Collectors.toCollection(ArrayList::new));
    }
}

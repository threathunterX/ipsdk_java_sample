package com.yazx.demo;

import com.yazx.dao.IpBaseInfoFinder;
import com.yazx.model.IpLocation;
import com.yazx.model.IpRiskData;
import com.yazx.model.RedisConf;
import com.yazx.simple.IIpRealtimeDataFinderRedis;
import io.lettuce.core.RedisCredentials;
import io.lettuce.core.RedisURI;
import io.lettuce.core.StaticCredentialsProvider;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author yazx
 *
 * 用于cluster集群模式redis实时查询ip的实现类，
 */
public final class IpRealtimeDataFinderRedisClusterImpl implements IIpRealtimeDataFinderRedis, IpBaseInfoFinder {
    private RedisAdvancedClusterCommands<String, String> redisCli;

    public IpRealtimeDataFinderRedisClusterImpl(RedisConf conf) {
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
    public IpRiskData findIpRiskData(String ip, long collectTime) throws Exception {
        String riskStr = redisCli.get("ipv4_risk_" + ip);
        if (riskStr == null) {
            return null;
        }
        return decodeIpRiskAndEnvData(riskStr, ip);
    }

    @Override
    public IpRiskData findIpEnvData(String ip, long collectTime) throws Exception {
        String riskStr = redisCli.get("ipv4_env_" + ip);
        if (riskStr == null) {
            return null;
        }
        return decodeIpRiskAndEnvData(riskStr, ip);
    }

    @Override
    public IpRiskData findIpOpenData(String ip, long collectTime) throws Exception {
        String riskStr = redisCli.get("ipv4_open_" + ip);
        if (riskStr == null) {
            return null;
        }
        return decodeIpOpenData(riskStr, ip);
    }

    @Override
    public IpLocation findIpBaseData(String ip) throws Exception {
        String baseStr = redisCli.get("ipv4_base_" + ip);
        return decodeIpLocation(baseStr);
    }
}
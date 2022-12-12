package com.yazx.demo;

import com.yazx.dao.IpBaseInfoFinder;
import com.yazx.model.IpLocation;
import com.yazx.model.IpRiskData;
import com.yazx.model.RedisConf;
import com.yazx.simple.IIpRealtimeDataFinderRedis;
import io.lettuce.core.*;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * @author yazx
 *
 * 用于单节点模式redis实时查询ip的实现类
 */
public final class IpRealtimeDataFinderRedisStandaloneImpl implements IpBaseInfoFinder, IIpRealtimeDataFinderRedis {
    private RedisCommands<String, String> redisCli;

    public IpRealtimeDataFinderRedisStandaloneImpl(RedisConf conf) {
        if (conf.getHosts().length > 1) {
            throw new RuntimeException("conf.hosts.size > 1 cannot use Standalone mode");
        }
        try {

            RedisURI redisURI = RedisURI.create(conf.getHosts()[0]);
            if (conf.getPassword() != null && !conf.getPassword().isEmpty()) {
                RedisCredentialsProvider credentialsProvider = new StaticCredentialsProvider(RedisCredentials.just(conf.getUsername(), conf.getPassword()));
                redisURI.setCredentialsProvider(credentialsProvider);
            }

            redisCli = RedisClient.create(redisURI).connect().sync();
            System.out.println(redisCli.ping());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
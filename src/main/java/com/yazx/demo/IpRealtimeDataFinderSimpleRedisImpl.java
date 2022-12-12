package com.yazx.demo;

import com.yazx.dao.IpBaseInfoFinder;
import com.yazx.model.IpLocation;
import com.yazx.model.IpRiskData;
import com.yazx.model.RedisConf;
import com.yazx.simple.IIpRealtimeDataFinderRedis;

/**
 * @author yazx
 *
 * 用于redis实时查询ip的实现类，兼容了单机和集群模式
 * @see IpRealtimeDataFinderRedisStandaloneImpl 单机模式
 * @see IpRealtimeDataFinderRedisClusterImpl 集群模式
 */
public class IpRealtimeDataFinderSimpleRedisImpl implements IIpRealtimeDataFinderRedis, IpBaseInfoFinder {

    private IIpRealtimeDataFinderRedis redisCli;

    public IpRealtimeDataFinderSimpleRedisImpl(RedisConf conf) {
        if (conf == null) {
            return;
        }

        if (conf.getHosts().length == 0) {
            return;
        }
        if (conf.getHosts().length == 1) {
            redisCli = new IpRealtimeDataFinderRedisStandaloneImpl(conf);
        } else {
            redisCli = new IpRealtimeDataFinderRedisClusterImpl(conf);
        }
    }

    @Override
    public IpRiskData findIpRiskData(String ip, long collectTime) throws Exception {
        return redisCli.findIpRiskData(ip, collectTime);
    }

    @Override
    public IpRiskData findIpEnvData(String ip, long collectTime) throws Exception {
        return redisCli.findIpEnvData(ip, collectTime);
    }

    @Override
    public IpRiskData findIpOpenData(String ip, long collectTime) throws Exception {
        return redisCli.findIpOpenData(ip, collectTime);
    }

    @Override
    public IpLocation findIpBaseData(String ip) throws Exception {
        return redisCli.findIpBaseData(ip);
    }
}
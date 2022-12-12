package com.yazx.demo;

import com.yazx.dao.IIpv6BaseFinder;
import com.yazx.dao.IIpv6RiskFinder;
import com.yazx.model.IpLocation;
import com.yazx.model.Ipv6RiskData;
import com.yazx.model.RedisConf;

import java.util.ArrayList;

/**
 * @author yazx
 *
 * 用于redis实时查询ipv6的实现类，兼容了单机和集群模式
 * @see Ipv6DataFinderRedisStandaloneImpl 单机模式
 * @see Ipv6DataFinderRedisClusterImpl 集群模式
 */
public class Ipv6DataFinderRedisImpl implements IIpv6BaseFinder, IIpv6RiskFinder {

    private IIpv6RiskFinder mRedisRiskCli;
    private IIpv6BaseFinder mRedisBaseCli;

    public Ipv6DataFinderRedisImpl(RedisConf conf) {
        if (conf == null) {
            return;
        }

        if (conf.getHosts().length == 0) {
            return;
        }
        if (conf.getHosts().length == 1) {
            Ipv6DataFinderRedisStandaloneImpl redisRiskCli = new Ipv6DataFinderRedisStandaloneImpl(conf);
            mRedisRiskCli = redisRiskCli;
            mRedisBaseCli = redisRiskCli;
        } else {
            Ipv6DataFinderRedisClusterImpl redisCluster = new Ipv6DataFinderRedisClusterImpl(conf);
            mRedisRiskCli = redisCluster;
            mRedisBaseCli = redisCluster;
        }
    }

    @Override
    public IpLocation findIpBaseData(String prefix) throws Exception {
        return mRedisBaseCli.findIpBaseData(prefix);
    }

    @Override
    public ArrayList<Ipv6RiskData> findIpOrderedData(String prefix, String subnet, long collectTime, int validTime) throws Exception {
        return mRedisRiskCli.findIpOrderedData(prefix, subnet, collectTime, validTime);
    }

    @Override
    public Ipv6RiskData findIpOrderedRightData(String prefix, String subnet, long collectTime) throws Exception {
        return mRedisRiskCli.findIpOrderedRightData(prefix, subnet, collectTime);
    }

    @Override
    public ArrayList<Ipv6RiskData> findIpRandomData(String prefix, String subnet, long collectTime, int validTimeMin) throws Exception {
        return mRedisRiskCli.findIpRandomData(prefix, subnet, collectTime, validTimeMin);
    }
}

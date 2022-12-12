package com.yazx.demo;

import com.yazx.dao.IIpHistoryDataFinder;
import com.yazx.model.IpAttackType;
import com.yazx.model.IpRiskData;
import com.yazx.model.IpType;
import com.yazx.model.MysqlConf;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author yazx
 * @desc ip历史数据查询 mysql实现类
 */
public class IpHistoryDataFinderMysqlImpl implements IIpHistoryDataFinder {

    private Connection conn;
    private int mValidSec;

    public IpHistoryDataFinderMysqlImpl(MysqlConf mysqlConf, int validMin) {
        this.mValidSec = validMin * 60;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
//            String url = "jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding=utf-8&useSSL=false";
            String url = mysqlConf.getUrl();

            // 数据库的用户名与密码，需要根据自己的设置
            String username = mysqlConf.getUsername();
            String password = mysqlConf.getPassword(); //密码未贴出

            conn = DriverManager.getConnection(url, username, password);
            if (conn == null) {
                String failed = "connecting to the Database failed";
                System.out.println(failed);
                throw new RuntimeException(failed);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<IpRiskData> findIpRiskData(String ip, long collectTime) throws SQLException {
        return findIpData(
                "select INET_NTOA(ip), ip_type, tag_name, unix_timestamp(attack_time) from `ip_risk_info` where ip in (INET_ATON('" + ip + "')) and tag_name in (0, 1) and attack_time > from_unixtime(" + (collectTime-mValidSec) + ") and attack_time <= from_unixtime(" + (collectTime+mValidSec) + ") order by attack_time;",
                ip
        );

    }

    @Override
    public ArrayList<IpRiskData> findIpEnvData(String ip, long collectTime) throws SQLException {
        return findIpData(
                "select INET_NTOA(ip), ip_type, tag_name, unix_timestamp(attack_time) from `ip_risk_info` where ip in (INET_ATON('" + ip + "')) and tag_name in (2, 3, 4, 5, 6) and attack_time > from_unixtime(" + (collectTime-mValidSec) + ") and attack_time <= from_unixtime(" + (collectTime+mValidSec) + ") order by attack_time;",
                ip
        );
    }

    @Override
    public ArrayList<IpRiskData> findIpOpenData(String ip, long collectTime) throws SQLException {
        return findIpData(
                "select INET_NTOA(ip), ip_type, tag_name, unix_timestamp(attack_time) from `ip_risk_info` where ip in (INET_ATON('" + ip + "')) and tag_name in (101, 102, 103, 104, 105, 106, 107) and attack_time > from_unixtime(" + (collectTime-mValidSec) + ") and attack_time <= from_unixtime(" + (collectTime+mValidSec) + ") order by attack_time;",
                ip
        );
    }

    @Override
    public ArrayList<IpRiskData> findAllData(String ip, long collectTime, int limit) throws SQLException {
        return findIpData(
                "select INET_NTOA(ip), ip_type, tag_name, unix_timestamp(attack_time) from `ip_risk_info` where ip in (INET_ATON('" + ip + "')) and attack_time <= from_unixtime(" + (collectTime+mValidSec) + ") order by attack_time limit " + limit + ";",
                ip
        );
    }

    private ArrayList<IpRiskData> findIpData(String sql, String ip) throws SQLException {
        ArrayList<IpRiskData> ipDataList;
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            ipDataList = new ArrayList<>();
            while (rs.next()) {
                IpType ipType = IpType.fromInt(rs.getInt(2));
                IpAttackType ipAttackType = IpAttackType.fromInt(rs.getInt(3));
                long attackTime = rs.getLong(4);
                IpRiskData ipData = new IpRiskData(ip, ipType, attackTime, ipAttackType);
                ipDataList.add(ipData);
            }

            return ipDataList;
        }
    }
}

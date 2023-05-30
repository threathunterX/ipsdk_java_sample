package com.yazx.model;

import java.util.Arrays;

public final class RedisConf {

    private String[] hosts;
    private String username;

    private String password;

    public RedisConf(String[] hosts, String username, String password) {
        this.username = username;
        this.password = password;
        this.hosts = Arrays.stream(hosts).map(it -> it.startsWith("redis://") ? it : "redis://" + it).toArray(String[]::new);
    }

    public String[] getHosts() {
        return hosts;
    }

    public void setHosts(String[] hosts) {
        this.hosts = hosts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
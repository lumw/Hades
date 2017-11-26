package com.danyun.hades.connection.container;


import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class RestConnectionMap {

    private ConcurrentHashMap<String, Channel> restConnectionList = new ConcurrentHashMap<String, Channel>();

    private static RestConnectionMap instance = new RestConnectionMap();

    private RestConnectionMap() {
    }

    public static RestConnectionMap getInstance() {
        if (instance == null) {
            instance = new RestConnectionMap();
        }
        return instance;
    }

    public void put(String catcherId, Channel channel) {
        restConnectionList.put(catcherId, channel);
    }

    public void remove(String catcherId) {
        restConnectionList.remove(catcherId);
    }

    public boolean contains(String catcherId) {
        return restConnectionList.containsKey(catcherId);
    }

    public ConcurrentHashMap<String, Channel> getMap() {
        return restConnectionList;
    }
}

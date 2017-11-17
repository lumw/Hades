package com.danyun.hades.common.model;


import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class CatcherSocketMap {

    private ConcurrentHashMap<String, Channel> catcherSocketList = new ConcurrentHashMap<String, Channel>();

    private static CatcherSocketMap instance = new CatcherSocketMap();

    private CatcherSocketMap() {
    }

    public static CatcherSocketMap getInstance() {
        if (instance == null) {
            instance = new CatcherSocketMap();
        }
        return instance;
    }

    public void put(String catcherId, Channel channel) {
        catcherSocketList.put(catcherId, channel);
    }

    public void remove(String catcherId) {
        catcherSocketList.remove(catcherId);
    }

    public boolean contains(String catcherId){
        return catcherSocketList.containsKey(catcherId);
    }

    public ConcurrentHashMap<String, Channel> getMap() {
        return catcherSocketList;
    }
}

package com.danyun.hades.connection.container;


import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class SocketConnectionMap {

    //用来保存和娃娃机的连接
    private ConcurrentHashMap<String, Channel> catcherSocketList = new ConcurrentHashMap<String, Channel>();

    private static SocketConnectionMap instance = new SocketConnectionMap();

    private SocketConnectionMap() {
    }

    public static SocketConnectionMap getInstance() {
        if (instance == null) {
            instance = new SocketConnectionMap();
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

package com.danyun.hades.connection.container;


import io.netty.channel.Channel;

import java.util.Iterator;
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

    public String removeByValue(Channel channel){

        String catcherId = "";
        Iterator iterator = catcherSocketList.entrySet().iterator();
        while (iterator.hasNext()){
            ConcurrentHashMap.Entry entry = (ConcurrentHashMap.Entry) iterator.next();
            catcherId = entry.getKey().toString();
            Channel value = (Channel) entry.getValue();
            System.out.println("catcherId:" + catcherId + "channel ：" + channel);
            if (value == channel){
                catcherSocketList.remove(catcherId);
            }
        }
        return catcherId;
    }

    public ConcurrentHashMap<String, Channel> getMap() {
        return catcherSocketList;
    }
}

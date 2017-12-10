package com.danyun.hades;


import com.danyun.hades.restserver.RestServerThread;
import com.danyun.hades.sockserve.CatcherServerThread;
import io.netty.channel.unix.Socket;

import java.util.HashMap;

public class ApplicationRun {

    public static HashMap<String, Socket> catcherSocketList = new HashMap<String, Socket>();

    public static void main(String[] args){

        //ApplicationContext context = new ClassPathXmlApplicationContext("classpath:hades-beans.xml");

        Thread catcherServerThread = new CatcherServerThread();
        catcherServerThread.start();

        Thread httpServerThread = new RestServerThread();
        httpServerThread.start();

    }
}
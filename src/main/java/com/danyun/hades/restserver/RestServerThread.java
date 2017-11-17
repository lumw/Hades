package com.danyun.hades.restserver;


import com.danyun.hades.util.PropertyUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RestServerThread extends Thread{

    public  void run()  {
        httpServerStart();
    }

    public void httpServerStart(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new RestServerInitializer());

            int portNumber = Integer.parseInt(PropertyUtil.getProperty("Netty_Rest_Listen_Port"));
            Channel ch = b.bind(portNumber).sync().channel();
            System.out.println("Rest服务器开始运行,监听端口:" + portNumber + '.');
            ch.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}

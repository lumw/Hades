package com.danyun.hades.sockserver.catcher;


import com.danyun.hades.util.PropertyUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class CatcherServerThread extends Thread{

    public void run(){

        catcherServerStart();
    }

    public void catcherServerStart(){

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new CatcherServerInitializer());

            // 服务器绑定端口监听
            int portNumber = Integer.parseInt(PropertyUtil.getProperty("Netty_Listen_Port"));
            ChannelFuture f = b.bind(portNumber).sync();

            System.out.println("Socket服务器开始运行,监听端口:" + portNumber + '.');
            // 监听服务器关闭监听
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            System.out.println("------出异常了");
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

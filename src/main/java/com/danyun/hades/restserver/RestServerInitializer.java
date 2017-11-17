package com.danyun.hades.restserver;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class RestServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline cPipeline = ch.pipeline();

        cPipeline.addLast(new HttpServerCodec());/*HTTP 服务的解码器*/
        cPipeline.addLast(new HttpObjectAggregator(2048));/*HTTP 消息的合并处理*/
        cPipeline.addLast(new RestServerOutBoundHandler());
        cPipeline.addLast(new RestServerInBoundHandler()); /*自己写的服务器逻辑处理*/

    }
}

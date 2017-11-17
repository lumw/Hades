package com.danyun.hades.restserver;


import com.danyun.hades.common.model.CatcherSocketMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class RestServerOutBoundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg,
                      ChannelPromise promise) throws Exception {

        String catcherId = msg.toString().substring(0, 4);

        System.out.println("发送给娃娃机的指令:" + msg);
        CatcherSocketMap.getInstance().getMap().get(catcherId).writeAndFlush(msg);
    }
}

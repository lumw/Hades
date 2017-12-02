package com.danyun.hades.restserver;


import com.danyun.hades.connection.container.SocketConnectionMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class RestServerOutBoundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg,
                      ChannelPromise promise) throws Exception {

        String catcherId = msg.toString().substring(0, 4);

        if(!SocketConnectionMap.getInstance().contains(catcherId)){
            System.out.println("与娃娃机[" + catcherId + "]的链接不存在");
            return;
        }

        System.out.println("发送给娃娃机的指令:" + msg);
        SocketConnectionMap.getInstance().getMap().get(catcherId).writeAndFlush(msg);
    }
}

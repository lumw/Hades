package com.danyun.hades.restserver;


import com.danyun.hades.connection.container.SocketConnectionMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RestServerOutBoundHandler extends ChannelOutboundHandlerAdapter {

    private static Logger logger = LogManager.getLogger(RestServerOutBoundHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg,
                      ChannelPromise promise) throws Exception {

        String catcherId = msg.toString().substring(0, 4);
        if(!SocketConnectionMap.getInstance().contains(catcherId)){
            logger.error("与娃娃机[" + catcherId + "]的链接不存在");
            return;
        }

        logger.info("发送给娃娃机的指令:" + msg);
        SocketConnectionMap.getInstance().getMap().get(catcherId).writeAndFlush(msg);
    }
}

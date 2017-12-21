package com.danyun.hades.restserver;


import com.danyun.hades.connection.container.SocketConnectionMap;
import io.netty.channel.*;
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

        //判断链接是否有效
        Channel currChannel = SocketConnectionMap.getInstance().getMap().get(catcherId);
        if (!currChannel.isActive()) {
            logger.error("isActive, 准备发送请求，但是发现链接已经无效了------");
            return;
        }
        if (!currChannel.isWritable()) {
            logger.error("isWritable, 准备发送请求，但是发现链接已经无效了------");
            return;
        }
        if (!currChannel.isOpen()) {
            logger.error("isOpen, 准备发送请求，但是发现链接已经无效了------");
            return;
        }
        if (!currChannel.isRegistered()) {
            logger.error("isRegistered, 准备发送请求，但是发现链接已经无效了------");
            return;
        }

        logger.info("发送给娃娃机的指令:" + msg);
        ChannelFuture channelFuture = currChannel.writeAndFlush(msg);
    }
}

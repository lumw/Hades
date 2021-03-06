package com.danyun.hades.sockserve;

import com.danyun.hades.util.PropertyUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;


public class CatcherServerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        // 以("\n")为结尾分割的 解码器
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));

        // 字符串解码 和 编码
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());

        //超时处理(当规定时间内没有数据写进来的时候断开连接)
        pipeline.addLast("ping", new IdleStateHandler(Integer.parseInt(PropertyUtil.getProperty("Netty_READ_WAIT_SECONDS")),0,0, TimeUnit.SECONDS));
        //逻辑Handler
        pipeline.addLast("handler", new CatcherServerHandler());
    }
}

package com.danyun.hades.sockserve;


import com.danyun.hades.common.model.redis.UfoCatcher;
import com.danyun.hades.connection.container.RestConnectionMap;
import com.danyun.hades.connection.container.SocketConnectionMap;
import com.danyun.hades.constant.ConstantString;
import com.danyun.hades.redis.dao.UfoCatcherDao;
import com.danyun.hades.redis.dao.impl.UfoCatcherRedisDaoImpl;
import com.danyun.hades.restserver.RestServerOutBoundHandler;
import com.danyun.hades.sockserve.service.CatcherService;
import com.danyun.hades.util.DateUtil;
import com.danyun.hades.util.SpringContainer;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.AsciiString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class CatcherServerHandler extends SimpleChannelInboundHandler<String> {

    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");

    @Autowired
    private UfoCatcherDao ufoCatcherDao;

    private CatcherService catcherService;

    private static Logger logger = LogManager.getLogger(CatcherServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msgfromCatcher) throws Exception {

        //ApplicationContext context = new ClassPathXmlApplicationContext("classpath:hades-beans.xml");
        //UfoCatcherRedisDaoImpl ufoCatcherDao = (UfoCatcherRedisDaoImpl) context.getBean("userDao");

        UfoCatcherRedisDaoImpl ufoCatcherDao = (UfoCatcherRedisDaoImpl)SpringContainer.getInstance().getBean("userDao");

        System.out.println("接收到来自 " + ctx.channel().remoteAddress() + " 的指令 : " + msgfromCatcher);

        String catcherId = msgfromCatcher.substring(0, 4);
        String actionCode = msgfromCatcher.substring(4, 8);

        System.out.println("catcherId = " + catcherId + ", actionCode = " + actionCode);
        String rspStrToCatcher;

        if("0100".equals(actionCode)){

            //将娃娃机与服务器链接注册进内存中
            if(SocketConnectionMap.getInstance().contains(catcherId)){
                SocketConnectionMap.getInstance().remove(catcherId);
            }
            SocketConnectionMap.getInstance().put(catcherId, ctx.channel());

            System.out.println("娃娃机[" + catcherId + "]从 " + ctx.channel().remoteAddress() + " 注册进入系统");

            //将娃娃机注册到Redis中
            UfoCatcher ufoCatcher = new UfoCatcher();
            ufoCatcher.setUFOCatcherId(catcherId);
            ufoCatcher.setOnlineStatus(ConstantString.Catcher_Online);
            ufoCatcher.setGameState(ConstantString.Catcher_Status_Free);
            String currenDtTm = DateUtil.getCurrentDtTm();
            ufoCatcher.setLoginTmDt(currenDtTm);
            ufoCatcher.setLastUpdateTmDt(currenDtTm);
            ufoCatcherDao.catcherRegist(ufoCatcher);

            rspStrToCatcher = catcherId + actionCode + "0000" + "\n";
            System.out.println("应答娃娃机登录信息" + rspStrToCatcher);
            ctx.writeAndFlush(rspStrToCatcher);

        }else if("9999".equals(actionCode)){
            rspStrToCatcher = catcherId + actionCode + "\n";
            System.out.println("应答心跳信息" + rspStrToCatcher);
            ctx.writeAndFlush(rspStrToCatcher);

        }else if("0101".equals(actionCode)){

            String operationId = msgfromCatcher.substring(8, 12);
            String gameResult = msgfromCatcher.substring(12, 13);
            System.out.println("游戏结果" + gameResult);
            rspStrToCatcher = catcherId + actionCode + operationId + "0000" + "\n";
            System.out.println("应答游戏结果通知" + rspStrToCatcher);

            //解锁娃娃机，设置状态为空闲
            UfoCatcher ufoCatcher = ufoCatcherDao.get(catcherId);
            ufoCatcher.setGameState(ConstantString.Catcher_Status_Free);
            String currentDtTm = DateUtil.getCurrentDtTm();
            ufoCatcher.setLastUpdateTmDt(currentDtTm);
            ufoCatcherDao.catcherRegist(ufoCatcher);

            //发送游戏结果到服务器
            catcherService = (CatcherService) SpringContainer.getInstance().getBean("catcherService");
            boolean result = catcherService.notifyResult(operationId, catcherId, Integer.parseInt(gameResult));
            System.out.println("游戏结果通知结果 ： " + result);
            //失败处理
            if(!result){
                logger.error("发送游戏结果通知失败");
            }

            ctx.writeAndFlush(rspStrToCatcher);

        }else if ( "0001".equals(actionCode) ){

            System.out.println("收到娃娃机响应信息: " + msgfromCatcher);

            String recordId = msgfromCatcher.substring(8, 16);
            String resultCode = msgfromCatcher.substring(16, 20);

            JSONObject rspJson = new JSONObject();
            rspJson.put("recordId", recordId);
            rspJson.put("resultCode", resultCode);

            RestConnectionMap.getInstance().getMap().get(catcherId).pipeline().remove(RestServerOutBoundHandler.class);


            byte[] jsonByteByte = rspJson.toString().getBytes();
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(jsonByteByte));
            response.headers().set(CONTENT_TYPE, "application/json");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(CONNECTION, KEEP_ALIVE);


            Channel channel = RestConnectionMap.getInstance().getMap().get(catcherId);
            channel.writeAndFlush(response);
            channel.close();
            RestConnectionMap.getInstance().remove(catcherId);

        } else { //其他应答指令

            System.out.println("收到操作应答指令: " + msgfromCatcher);

            String resultCode = msgfromCatcher.substring(8, 12);
            JSONObject rspJson = new JSONObject();
            rspJson.put("resultCode", resultCode);


            RestConnectionMap.getInstance().getMap().get(catcherId).pipeline().remove(RestServerOutBoundHandler.class);
            byte[] jsonByteByte = rspJson.toString().getBytes();
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(jsonByteByte));
            response.headers().set(CONTENT_TYPE, "application/json");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(CONNECTION, KEEP_ALIVE);

            Channel channel = RestConnectionMap.getInstance().getMap().get(catcherId);
            channel.writeAndFlush(response);
            channel.close();
            RestConnectionMap.getInstance().remove(catcherId);

        }
    }

    /*
     *
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     *
     * channelActive 和 channelInActive 在后面的内容中讲述，这里先不做详细的描述
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");

        //ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{

        System.out.println("连接已经失效.........");
        super.channelInactive(ctx);
    }

    public void setUfoCatcherDao(UfoCatcherDao ufoCatcherDao) {
        this.ufoCatcherDao = ufoCatcherDao;
    }

    public void setCatcherService(CatcherService catcherService) {
        this.catcherService = catcherService;
    }
}

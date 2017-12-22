package com.danyun.hades.sockserve;


import com.danyun.hades.common.model.redis.UfoCatcherRedis;
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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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

        UfoCatcherRedisDaoImpl ufoCatcherDao = (UfoCatcherRedisDaoImpl)SpringContainer.getInstance().getBean("userDao");

        logger.info("接收到来自 " + ctx.channel().remoteAddress() + " 的指令 : " + msgfromCatcher);

        String catcherId = msgfromCatcher.substring(0, 4);
        String actionCode = msgfromCatcher.substring(4, 8);

        logger.info("catcherId = " + catcherId + ", actionCode = " + actionCode);
        String rspStrToCatcher;

        //娃娃机注册
        if("0100".equals(actionCode)){

            //将娃娃机与服务器链接注册进内存中
            if(SocketConnectionMap.getInstance().contains(catcherId)){
                SocketConnectionMap.getInstance().remove(catcherId);
                logger.info("娃娃机与服务器连接已经存在于连接管理器中，删除失效连接，替换为新连接");
            }
            SocketConnectionMap.getInstance().put(catcherId, ctx.channel());

            logger.info("娃娃机[" + catcherId + "]从 " + ctx.channel().remoteAddress() + " 注册进入系统");

            //将娃娃机注册到Redis中
            initUfoCatcherRedis(catcherId);

            logger.info("已将娃娃机[" + catcherId + "]注册进入Redis中");
            rspStrToCatcher = catcherId + actionCode + "0000" + "\n";
            logger.info("应答娃娃机登录信息" + rspStrToCatcher);
            ctx.writeAndFlush(rspStrToCatcher);
        }
        //心跳
        else if("9999".equals(actionCode)){

            rspStrToCatcher = catcherId + actionCode + "\n";

            //将娃娃机与服务器链接注册进内存中(加这段代码是因为娃娃机的重连机制:娃娃机如果连续三次没有收到
            // 心跳包的应答的话，则会触发重连，但是如果在三次以内重新连上服务器后会继续发心跳，不会重新发上线请求)
            if (!SocketConnectionMap.getInstance().contains(catcherId)) {
                SocketConnectionMap.getInstance().put(catcherId, ctx.channel());
                logger.info("重新将连接写入连接管理器中, 娃娃机编号" + catcherId);
            }
            if (!ufoCatcherDao.isUFOCatcherRegister(catcherId)) {
                initUfoCatcherRedis(catcherId);
                logger.info("重新将初始化娃娃机信息到Redis中, 娃娃机编号" + catcherId);
            }
            ChannelFuture channelFuture = ctx.writeAndFlush(rspStrToCatcher);
            logger.info("应答心跳信息" + rspStrToCatcher);
        }
        //结果通知
        else if("0101".equals(actionCode)){

            String operationId = msgfromCatcher.substring(8, 16);
            String gameResult = msgfromCatcher.substring(16, 17);
            logger.info("收到游戏结果反馈, " + "operationId : " + operationId + ", 游戏结果: " + gameResult);

            //解锁娃娃机，设置状态设置为玩家临时占用 (因为要给玩家时间考虑是否继续游戏)
            UfoCatcherRedis ufoCatcher = ufoCatcherDao.get(catcherId);
            ufoCatcher.setGameStatus(ConstantString.Catcher_Status_Own);
            ufoCatcher.setLastUpdateTmDt(DateUtil.getCurrentTimeMillis());
            ufoCatcher.setLastGameEndDtTm(DateUtil.getCurrentTimeMillis());
            ufoCatcherDao.catcherRegist(ufoCatcher);
            logger.info("改变娃娃机状态成功,当前娃娃机状态:" + ConstantString.Catcher_Status_Own);

            //发送游戏结果到服务器
            catcherService = (CatcherService) SpringContainer.getInstance().getBean("catcherService");
            boolean result = catcherService.notifyResult(operationId, catcherId, Integer.parseInt(gameResult));
            //失败处理
            if(!result){
                logger.error("反馈游戏结果失败");
                rspStrToCatcher = catcherId + actionCode + operationId + "9999" + "\n";
            }else {
                logger.info("反馈游戏结果成功");
                rspStrToCatcher = catcherId + actionCode + operationId + "0000" + "\n";
            }
            ctx.writeAndFlush(rspStrToCatcher);
        }
        //上机操作应答
        else if ( "0001".equals(actionCode) ){
            logger.info("收到上机响应信息: " + msgfromCatcher);
            String recordId = msgfromCatcher.substring(8, 16);
            String resultCode = msgfromCatcher.substring(16, 20);

            JSONObject rspJson = new JSONObject();
            rspJson.put("recordId", recordId);
            rspJson.put("resultCode", resultCode);

            //上机成功，更新redis中娃娃机信息
            if ("0000".equals(resultCode)){
                UfoCatcherRedis ufoCatcherRedis = ufoCatcherDao.get(catcherId);
                ufoCatcherRedis.setGameStatus(ConstantString.Catcher_Status_Using);
                ufoCatcherRedis.setLastUpdateTmDt(DateUtil.getCurrentTimeMillis());
                ufoCatcherDao.update(ufoCatcherRedis);
            }
            if (responseToRestServer(rspJson, catcherId)) {
                logger.info("成功发送应答数据到服务器");
            } else {
                logger.error("未能成功发送应答数据到服务器\"");
            }
        } else { //其他应答指令
            logger.info("收到操作应答指令: " + msgfromCatcher);
            String resultCode = msgfromCatcher.substring(8, 12);
            JSONObject rspJson = new JSONObject();
            rspJson.put("resultCode", resultCode);
            if (responseToRestServer(rspJson, catcherId)){
                logger.info("成功发送应答数据到服务器");
            }else {
                logger.error("未能成功发送应答数据到服务器\"");
            }
        }
    }

    public boolean responseToRestServer(JSONObject rspJson, String ufoCatcherId) {
        Channel channel = RestConnectionMap.getInstance().getMap().get(ufoCatcherId);
        if (null != channel) {
            RestConnectionMap.getInstance().getMap().get(ufoCatcherId).pipeline().remove(RestServerOutBoundHandler.class);
            byte[] jsonByteByte = rspJson.toString().getBytes();
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(jsonByteByte));
            response.headers().set(CONTENT_TYPE, "application/json");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(CONNECTION, KEEP_ALIVE);
            channel.writeAndFlush(response);
            channel.close();
            RestConnectionMap.getInstance().remove(ufoCatcherId);
            return true;
        } else {
            logger.error("channel = null ....");
        }

        return false;
    }
    /*
     *
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     *
     * channelActive 和 channelInActive 在后面的内容中讲述，这里先不做详细的描述
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{

        String ufoCatcherId = SocketConnectionMap.getInstance().removeByValue(ctx.channel());
        logger.error("channelInactive + 与娃娃机" + ufoCatcherId + "的连接已经失效,删除对应的Channel");
        UfoCatcherRedisDaoImpl ufoCatcherDao = (UfoCatcherRedisDaoImpl) SpringContainer.getInstance().getBean("userDao");
        ufoCatcherDao.delete(ufoCatcherId);
        logger.error("channelInactive + 与娃娃机" + ufoCatcherId + "的连接已经失效,删除Redis中的记录");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        logger.error("exceptionCaught...." + cause.getMessage());
        //String ufoCatcherId = SocketConnectionMap.getInstance().removeByValue(ctx.channel());
        //logger.error("与娃娃机" + ufoCatcherId + "的连接已经失效,删除对应的Channel");

        //UfoCatcherRedisDaoImpl ufoCatcherDao = (UfoCatcherRedisDaoImpl) SpringContainer.getInstance().getBean("userDao");
        //ufoCatcherDao.delete(ufoCatcherId);
        //logger.error("与娃娃机" + ufoCatcherId + "的连接已经失效,删除Redis中的记录");

        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt){

        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE){
                logger.error("===服务端===(READER_IDLE 读超时)");
                ctx.close();
                //ctx.channel().close();
            }else if (event.state() == IdleState.WRITER_IDLE){
                logger.error("===服务端===(WRITER_IDLE  写超时)");
                ctx.close();
                //ctx.channel().close();
            }

        }
    }

    public void initUfoCatcherRedis(String ufoCatcherId){
        UfoCatcherRedisDaoImpl ufoCatcherDao = (UfoCatcherRedisDaoImpl) SpringContainer.getInstance().getBean("userDao");

        UfoCatcherRedis ufoCatcherRedis = new UfoCatcherRedis();
        ufoCatcherRedis.setUFOCatcherId(ufoCatcherId);
        ufoCatcherRedis.setOnlineStatus(ConstantString.Catcher_Online);
        ufoCatcherRedis.setGameStatus(ConstantString.Catcher_Status_Free);
        ufoCatcherRedis.setLoginTmDt(DateUtil.getCurrentTimeMillis());
        ufoCatcherRedis.setLastUpdateTmDt(DateUtil.getCurrentTimeMillis());
        ufoCatcherDao.catcherRegist(ufoCatcherRedis);
    }


    public void setUfoCatcherDao(UfoCatcherDao ufoCatcherDao) {
        this.ufoCatcherDao = ufoCatcherDao;
    }

    public void setCatcherService(CatcherService catcherService) {
        this.catcherService = catcherService;
    }
}
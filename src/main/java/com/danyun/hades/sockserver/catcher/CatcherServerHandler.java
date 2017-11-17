package com.danyun.hades.sockserver.catcher;


import com.danyun.hades.common.model.CatcherSocketMap;
import com.danyun.hades.common.model.redis.UfoCatcher;
import com.danyun.hades.constant.ConstantString;
import com.danyun.hades.redis.dao.UfoCatcherDao;
import com.danyun.hades.redis.dao.impl.UfoCatcherRedisDaoImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CatcherServerHandler extends SimpleChannelInboundHandler<String> {

    @Autowired
    private UfoCatcherDao ufoCatcherDao;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msgfromCatcher) throws Exception {

        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:hades-beans.xml");
        UfoCatcherRedisDaoImpl ufoCatcherDao = (UfoCatcherRedisDaoImpl) context.getBean("userDao");

        System.out.println("接收到来自 " + ctx.channel().remoteAddress() + " 的指令 : " + msgfromCatcher);

        String catcherId = msgfromCatcher.substring(0, 4);
        String actionCode = msgfromCatcher.substring(4, 8);

        System.out.println("catcherId = " + catcherId);
        System.out.println("actionCode = " + actionCode);

        String rspStrToCatcher;

        if("0100".equals(actionCode)){

            //将娃娃机与服务器链接注册进内存中
            if(!CatcherSocketMap.getInstance().contains(catcherId)){
                CatcherSocketMap.getInstance().put(catcherId, ctx.channel());
            }

            System.out.println("编号为:" + catcherId + "的娃娃机从" + ctx.channel().remoteAddress() + " 注册进入系统");

//            UfoCatcher ufoCatcher = new UfoCatcher();
//            ufoCatcher.setUFOCatcherId(catcherId);
//            ufoCatcher.setUfoCatcherStatus(ConstantString.Catcher_Status_Free);

            ufoCatcherDao.catcherRegist(new UfoCatcher(catcherId, ConstantString.Catcher_Status_Free));

            rspStrToCatcher = catcherId + actionCode + "0000";
            System.out.println("发送给娃娃机的响应信息" + rspStrToCatcher);
            ctx.writeAndFlush(rspStrToCatcher);

        }else if("9999".equals(actionCode)){
            rspStrToCatcher = catcherId + actionCode;
            System.out.println("发送给娃娃机的响应信息" + rspStrToCatcher);
            ctx.writeAndFlush(rspStrToCatcher);

        }else if("0101".equals(actionCode)){

            String operationId = msgfromCatcher.substring(8, 12);
            String gameResult = msgfromCatcher.substring(12, 13);
            System.out.println("游戏结果" + gameResult);
            rspStrToCatcher = catcherId + actionCode + operationId + "0000";
            System.out.println("发送给娃娃机的响应信息" + rspStrToCatcher);

            //解锁娃娃机，设置状态为空闲
            ufoCatcherDao.catcherRegist(new UfoCatcher(catcherId, ConstantString.Catcher_Status_Free));

            ctx.writeAndFlush(rspStrToCatcher);

        }else if("0001".equals(actionCode) || "0002".equals(actionCode) || "0003".equals(actionCode) || "0004".equals(actionCode) || "0005".equals(actionCode)
                || "0006".equals(actionCode) || "0007".equals(actionCode)){
            System.out.println("收到娃娃机响应信息: " + msgfromCatcher);
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

    public void setUfoCatcherDao(UfoCatcherDao ufoCatcherDao) {
        this.ufoCatcherDao = ufoCatcherDao;
    }
}

package com.danyun.hades.restserver;

import com.danyun.hades.common.model.redis.UfoCatcher;
import com.danyun.hades.connection.container.RestConnectionMap;
import com.danyun.hades.constant.ConstantString;
import com.danyun.hades.redis.dao.UfoCatcherDao;
import com.danyun.hades.redis.dao.impl.UfoCatcherRedisDaoImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class RestServerInBoundHandler extends ChannelInboundHandlerAdapter {

    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Autowired
    private UfoCatcherDao ufoCatcherDao;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:hades-beans.xml");
        UfoCatcherRedisDaoImpl ufoCatcherDao = (UfoCatcherRedisDaoImpl) context.getBean("userDao");

        StringBuilder toCatcherSockMessage = new StringBuilder();

        String catcherId;

        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;//客户端的请求对象
            JSONObject responseJson = new JSONObject();//新建一个返回消息的Json对象

            //把客户端的请求数据格式化为Json对象
            JSONObject requestJson = null;
            try {
                requestJson = new JSONObject(parseJosnRequest(req));
            } catch (Exception e) {
                ResponseJson(ctx, req, new String("error json"));
                return;
            }

            catcherId = requestJson.getString("catcherId");
            RestConnectionMap.getInstance().put(catcherId, ctx.channel());

            //获取客户端的URL
            String uri = req.uri();

            //根据不同的请求API做不同的处理(路由分发)，只处理POST方法
            if (req.method() == HttpMethod.POST) {

                if (uri.equals("/operation")) {

                    System.out.println("收到操作娃娃机指令json : " + requestJson.toString());

                    String actionCode = requestJson.getString("actionCode");
                    if(ConstantString.Catcher_Operation_PalyGame.equals(actionCode)){
                        ufoCatcherDao.update(new UfoCatcher(catcherId, ConstantString.Catcher_Status_Using));
                        String recordId = requestJson.getString("recordId");
                        toCatcherSockMessage.append(catcherId).append(actionCode).append(recordId);
                    }else{
                        toCatcherSockMessage.append(catcherId).append(actionCode);
                    }

                } else if (req.uri().equals("/checkstatus")) {

                    //String catcherId = requestJson.getString("catcherId");
                    String actionCode = requestJson.getString("actionCode");

                    System.out.println("娃娃机编号:" + catcherId + ", 操作代码: " + actionCode);

                    toCatcherSockMessage.append(catcherId).append(actionCode);

                } else if (req.uri().equals("/heartbreak")){

                    //String catcherId = requestJson.getString("catcherId");
                    String actionCode = requestJson.getString("actionCode");

                    System.out.println("娃娃机编号:" + catcherId + ", 操作代码: " + actionCode);

                    toCatcherSockMessage.append(catcherId).append(actionCode);

                } else {
                    //错误处理
                    responseJson.put("error", "404 Not Find");
                }

            } else {
                //错误处理
                responseJson.put("error", "404 Not Find");
            }

            //将数据发送到UFOCatcher
            ctx.write(toCatcherSockMessage.toString() + "\n");
        }
    }

    /**
     * 响应HTTP的请求
     *
     * @param ctx
     * @param req
     * @param jsonStr
     */
    private void ResponseJson(ChannelHandlerContext ctx, FullHttpRequest req, String jsonStr) {

        boolean keepAlive = HttpUtil.isKeepAlive(req);
        byte[] jsonByteByte = jsonStr.getBytes();
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(jsonByteByte));
        response.headers().set(CONTENT_TYPE, "text/json");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.write(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 获取请求的内容
     *
     * @param request
     * @return
     */
    private String parseJosnRequest(FullHttpRequest request) {
        ByteBuf jsonBuf = request.content();
        String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
        return jsonStr;
    }



}

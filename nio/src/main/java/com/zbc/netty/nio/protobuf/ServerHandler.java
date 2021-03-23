package com.zbc.netty.nio.protobuf;

import com.google.protobuf.TextFormat;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerInvoker;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <H3>  </H3>
 * <p>
 * create: 2021/3/22 <br/>
 * email: bingco.zn@gmail.com <br/>
 *
 * @author zhan_bingcong
 * @version 1.0
 * @since jdk8+
 */
public class ServerHandler extends ChannelHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ObjectBeanSerializer.ObjectBean ob = (ObjectBeanSerializer.ObjectBean) msg;
        log.debug("接收到数据:\r\n" + TextFormat.printToUnicodeString(ob));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常断开连接: " + cause.getMessage());
        ctx.close();
    }
}

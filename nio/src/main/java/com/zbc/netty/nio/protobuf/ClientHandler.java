package com.zbc.netty.nio.protobuf;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
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
public class ClientHandler extends ChannelHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接成功!发送实体");
        ObjectBeanSerializer.ObjectBean ob = ObjectBeanSerializer.ObjectBean.newBuilder()
                .setNum(1000)
                .setName("李白不太白!")
                .build();
        ctx.writeAndFlush(ob);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常断开连接： " + cause.getMessage());
        ctx.close();
    }
}

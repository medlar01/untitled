package com.zbc.netty.nio.msgpack;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class MsgpackClientHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 100; i++) {
            ObjectBean oBean = new ObjectBean();
            oBean.setNum(100000 + i);
            oBean.setName("Hello world java!");
            oBean.setClazz(MsgpackClientHandler.class);
            ctx.write(oBean);
        }
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

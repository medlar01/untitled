package com.zbc.netty.nio;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * <h3>使用换行符解决粘包/分包问题
 * <p>
 * create: 2020/9/24 <br/>
 * email: bingco.zn@gmail.com <br/>
 *
 * @author zhan_bingcong
 * @version 1.0
 * @since jdk8+
 */
@ChannelHandler.Sharable
public class EchoServerLineSymbolHandler extends ChannelHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(EchoServerLineSymbolHandler.class);
    private String msg;
    private int count = 1;

    /**
     * 接收数据时会触发
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将消息记录到控制台
        this.msg = (String) msg;
        log.debug("Server received: " + this.msg);
        log.debug("service received count: " + count ++);
        ctx.writeAndFlush(Unpooled.copiedBuffer(this.msg + System.getProperty("line.separator"), StandardCharsets.UTF_8));
    }

    /**
     * 建立新连接时会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Server listen socket");
    }

    /**
     * 当数据流读取完时会触发
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if ("done".equals(msg)) {
            ChannelFuture future = ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 读取数据流期间发生异常时触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

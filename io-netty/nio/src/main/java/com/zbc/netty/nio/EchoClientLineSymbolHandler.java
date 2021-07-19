package com.zbc.netty.nio;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * <h3>使用换行符解决粘包/分包问题
 * <p>
 * create: 2020/9/25 <br/>
 * email: bingco.zn@gmail.com <br/>
 *
 * @author zhan_bingcong
 * @version 1.0
 * @since jdk8+
 */
@ChannelHandler.Sharable
public class EchoClientLineSymbolHandler extends ChannelHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(EchoClientLineSymbolHandler.class);
    private Thread td;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        td = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String msg = scanner.nextLine();
                for (int i = 0; i < 100; i++) {
                    ctx.writeAndFlush(Unpooled.copiedBuffer(msg + System.getProperty("line.separator"), StandardCharsets.UTF_8));
                }
            }
        });
        td.start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("Client received: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        td.interrupt();
        ctx.close();
    }
}

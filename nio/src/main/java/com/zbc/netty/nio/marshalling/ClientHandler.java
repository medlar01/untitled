package com.zbc.netty.nio.marshalling;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <H3>  </H3>
 * <p>
 * create: 2021/3/23 <br/>
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
        log.debug("服务端连接成功!");
        for (int i = 0; i < 100; i++) {
            SubscribeReq sr = SubscribeReq.builder()
                    .address("中山市小榄镇")
                    .phoneNumber(10086)
                    .subReqId(i + 1)
                    .productName("JBoss marshalling")
                    .username("zhan_bingcong")
                    .build();
            ctx.write(sr);
        }
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常: " + cause.getMessage());
        ctx.close();
    }
}

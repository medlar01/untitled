package com.zbc.protocol.hdl;

import com.zbc.protocol.msg.Header;
import com.zbc.protocol.msg.NettyMsg;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 心跳响应处理
 */
public class HeartBeatRespHandler extends ChannelHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(HeartBeatRespHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMsg nMsg = (NettyMsg) msg;
        if (nMsg.hasHeader() && nMsg.getHeader().getType() == HeaderTypeConstant.HEARTBEAT_REQ) {
            String address = ctx.channel()
                    .remoteAddress()
                    .toString();
            log.debug(address + " | receive heart beat message : " + nMsg);
            Header header = Header.newBuilder()
                    .setType(HeaderTypeConstant.HEARTBEAT_RESP)
                    .build();
            NettyMsg newMsg = NettyMsg.newBuilder()
                    .setHeader(header)
                    .build();
            ctx.writeAndFlush(newMsg);
        } else ctx.fireChannelRead(msg);
    }
}

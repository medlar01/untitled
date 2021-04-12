package com.zbc.protocol.hdl;

import com.zbc.protocol.msg.Header;
import com.zbc.protocol.msg.NettyMsg;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 认证请求处理
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(LoginAuthReqHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Header header = Header.newBuilder()
                .setType(HeaderTypeConstant.LOGIN_AUTH_REQ)
                .build();
        NettyMsg msg = NettyMsg.newBuilder()
                .setHeader(header)
                .build();
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMsg nMsg = (NettyMsg) msg;
        if (nMsg.hasHeader() && nMsg.getHeader().getType() == HeaderTypeConstant.LOGIN_AUTH_RESP) {
            if (nMsg.getBody().isEmpty()) ctx.close();
            else {
                String address = ctx.channel()
                        .remoteAddress()
                        .toString();
                log.debug(address + " | Login is ok: " + nMsg.getBody());
                ctx.fireChannelRead(msg);
            }
        } else ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}

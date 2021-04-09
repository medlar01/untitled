package com.zbc.protocol.hdl;

import com.zbc.protocol.msg.Header;
import com.zbc.protocol.msg.NettyMsg;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class HeartBeatReqHandler extends ChannelHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(HeartBeatReqHandler.class);
    private volatile ScheduledFuture<?> heartBeatScheduledFuture;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMsg nMsg = (NettyMsg) msg;
        if (!nMsg.hasHeader()) {
            ctx.fireChannelRead(msg);
        }
        else
        if (nMsg.getHeader().getType() == HeaderTypeConstant.LOGIN_AUTH_RESP) {
            heartBeatScheduledFuture = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
        }
        else
        if (nMsg.getHeader().getType() == HeaderTypeConstant.HEARTBEAT_RESP) {
            log.debug("receive server heart beat message : " + nMsg.getBody());
        }
        else ctx.fireChannelRead(msg);
    }

    public static final class HeartBeatTask implements Runnable {
        private final ChannelHandlerContext ctx;
        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            Header header = Header.newBuilder()
                    .setType(HeaderTypeConstant.HEARTBEAT_REQ)
                    .build();
            NettyMsg msg = NettyMsg.newBuilder()
                    .setHeader(header)
                    .build();
            log.debug("send heart beat message to server : " + msg);
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeatScheduledFuture != null) {
            heartBeatScheduledFuture.cancel(true);
            heartBeatScheduledFuture = null;
        }
        ctx.fireExceptionCaught(cause);
    }
}

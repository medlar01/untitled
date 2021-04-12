package com.zbc.protocol.hdl;

import com.google.protobuf.ByteString;
import com.zbc.protocol.msg.Header;
import com.zbc.protocol.msg.NettyMsg;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证响应处理
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(LoginAuthRespHandler.class);
    private static final Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();
    private static final String[] whiteList = {"127.0.0.1", "192.168.190.65"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMsg nMsg = (NettyMsg) msg;
        if (nMsg.hasHeader() && nMsg.getHeader().getType() == HeaderTypeConstant.LOGIN_AUTH_REQ) {
            Header header = Header.newBuilder()
                    .setType(HeaderTypeConstant.LOGIN_AUTH_RESP)
                    .build();
            NettyMsg newMsg;
            String ra = ctx.channel()
                    .remoteAddress()
                    .toString();
            if (nodeCheck.containsKey(ra))
                newMsg = NettyMsg.newBuilder()
                        .setBody(ByteString.copyFromUtf8("-1"))
                        .build();
            else {
                boolean contains;
                newMsg = (contains = Arrays.asList(whiteList).contains(ra)) ?
                        NettyMsg.newBuilder()
                                .setHeader(header)
                                .setBody(ByteString.copyFromUtf8("0"))
                                .build()
                        :
                        NettyMsg.newBuilder()
                                .setHeader(header)
                                .setBody(ByteString.copyFromUtf8("-1"))
                                .build();
                if (!contains) nodeCheck.put(ra, true);
            }

            log.debug(ra + " | Login response is : " + newMsg);
            ctx.writeAndFlush(newMsg);
        } else ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel()
                .remoteAddress()
                .toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}

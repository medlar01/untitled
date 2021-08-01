package com.zbc.netty.nio.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpMethod.POST;

public class HttpXmlHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger log = LoggerFactory.getLogger(HttpXmlHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if (msg.method() != POST) {
            sendError(ctx);
            return;
        }

        if (!isXml(msg)) {
            sendError(ctx);
            return;
        }

        String content = msg.content().toString(StandardCharsets.UTF_8);
        log.debug("接收到报文: \r\n" + content);
        JAXBContext instance = JAXBContext.newInstance(Student.class);
        Unmarshaller unmarshaller = instance.createUnmarshaller();
        Student student = (Student) unmarshaller.unmarshal(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        log.debug("转换对象: " + student);
        sendOk(ctx);
    }

    private void sendOk(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer("success", StandardCharsets.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=utf-8");
        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }

    private boolean isXml(HttpRequest req) {
        HttpHeaders headers = req.headers();
        if (!headers.contains(CONTENT_TYPE))
            return false;
        return (headers.get(CONTENT_TYPE).toString().startsWith("text/xml"));
    }

    private void sendError(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST,
                Unpooled.copiedBuffer("Failure", StandardCharsets.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=utf-8");
        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }
}

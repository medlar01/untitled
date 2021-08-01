package com.zbc.netty.nio.http.socket;

import com.zbc.netty.nio.http.FreemarkerUtils;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.HOST;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServerHandler.class);
    private WebSocketServerHandshaker handShaker;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            http(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            socket(ctx, (WebSocketFrame) msg);
        }
    }

    private void http(ChannelHandlerContext ctx, HttpRequest msg) throws IOException, TemplateException {
        if (!msg.decoderResult().isSuccess()) {
            sendResponse(ctx, msg, HttpResponseStatus.BAD_REQUEST, "bad request");
            return;
        }

        String uri = msg.uri();
        if ("/favicon.ico".equals(uri)) {
            sendResponse(ctx, msg, HttpResponseStatus.NOT_FOUND, "404 not found!");
            return;
        }
        if (uri.isEmpty() || "/".equals(uri)) {
            redirect(ctx, "/index");
            return;
        }

        if ("/index".equals(uri)) {
            Template template = FreemarkerUtils.getTemplate("socket.ftl");
            StringWriter writer = new StringWriter();
            template.process(null, writer);
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(writer.toString(), StandardCharsets.UTF_8));
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            return;
        }

        WebSocketServerHandshakerFactory handShakerFactory =
                new WebSocketServerHandshakerFactory("ws://" + msg.headers().get(HOST) + "/websocket", null, true);
        handShaker = handShakerFactory.newHandshaker(msg);
        if (handShaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handShaker.handshake(ctx.channel(), msg);
        }
    }

    private void socket(ChannelHandlerContext ctx, WebSocketFrame msg) {
        if (msg instanceof CloseWebSocketFrame) {
            handShaker.close(ctx.channel(), ((CloseWebSocketFrame) msg).retain());
        } else if (msg instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(msg.content().retain()));
        } else if (!(msg instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", msg.getClass().getName()));
        } else {
            String text = ((TextWebSocketFrame) msg).text();
            log.info(String.format("%s received %s", ctx.channel(), text));
            ctx.channel().write(new TextWebSocketFrame(text + ", welcome used Netty Websocket service - " + new Date()));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void sendResponse(ChannelHandlerContext ctx, HttpRequest request, HttpResponseStatus status, String content) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(content, StandardCharsets.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=utf-8");
        HttpHeaderUtil.setContentLength(response, content.length());
        if (HttpHeaderUtil.isKeepAlive(request)) {
            HttpHeaderUtil.setKeepAlive(response, true);
        }
        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if (!HttpHeaderUtil.isKeepAlive(request) || response.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void redirect(ChannelHandlerContext ctx, String url) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.TEMPORARY_REDIRECT);
        HttpHeaders headers = response.headers();
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "x-requested-with,content-type");
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "POST,GET");
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes() + "");
        headers.set(HttpHeaderNames.LOCATION, url); //重定向URL设置
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}

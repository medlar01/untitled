package com.zbc.netty.nio.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

/**
 * <H3>  </H3>
 * <p>
 * create: 2021/3/26 <br/>
 * email: bingco.zn@gmail.com <br/>
 *
 * @author zhan_bingcong
 * @version 1.0
 * @since jdk8+
 */
public class HttpFileHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final static Logger log = LoggerFactory.getLogger(HttpFileHandler.class);
    private final static String BASE_URL = "F:/module";

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String uri = msg.uri();
        log.debug("请求到达: " + uri);

        if (!msg.decoderResult().isSuccess()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }
        if (msg.method() != GET) {
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }

        String obsPath = BASE_URL.concat(uri);
        File file = new File(obsPath);
        if (file.isDirectory()) {
            sendDir(ctx, uri, Objects.requireNonNull(file.list()));
            return;
        }
        if (file.isFile()) {
            sendFile(ctx, msg, file);
            return;
        }
        sendError(ctx, BAD_REQUEST);
    }

    private void sendFile(ChannelHandlerContext ctx, FullHttpRequest request, File file) throws IOException {
        try {
            RandomAccessFile raFile = new RandomAccessFile(file, "r");
            long fileLength = raFile.length();
            HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK);
            HttpHeaderUtil.setContentLength(response, fileLength);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
            response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
            if (HttpHeaderUtil.isKeepAlive(request)) {
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);

            ctx.write(new ChunkedFile(raFile, 0, fileLength, 8192), ctx.newProgressivePromise())
                    .addListener(new ChannelProgressiveFutureListener() {
                        @Override
                        public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                            log.debug("Transfer complete.");
                            raFile.close();
                        }

                        @Override
                        public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                            log.debug("Transfer progress: " + progress + "/" + total);
                        }
                    });
            ChannelFuture channelFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
//            if (!HttpHeaderUtil.isKeepAlive(request)) {
//                channelFuture.addListener(ChannelFutureListener.CLOSE);
//            }
        } catch (FileNotFoundException e) {
            log.debug("文件不存在!", e);
            sendError(ctx, NOT_FOUND);
        }

    }

    private void sendDir(ChannelHandlerContext ctx, String uri, String[] list) {
        StringBuilder html = new StringBuilder();
        if (!uri.equals("/")) {
            int i = uri.lastIndexOf("/");
            i = i == 0 ? 1 : i;
            html.append("<a href='").append(uri.substring(0, i)).append("'>返回</a><br/>");
        }
        else {
            uri = "";
        }
        for (String child : list) {
            html.append("\t|- <a href='").append(uri).append("/").append(child).append("'>").append(child).append("</a><br/>");
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, Unpooled.copiedBuffer(html.toString(), StandardCharsets.UTF_8));
        response.headers()
                .set(CONTENT_TYPE, "text/html; charset=UTF-8");
        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus hrs) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, hrs,
                Unpooled.copiedBuffer("Failure: " + hrs.toString(), StandardCharsets.UTF_8));
        response.headers()
                .set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }
}

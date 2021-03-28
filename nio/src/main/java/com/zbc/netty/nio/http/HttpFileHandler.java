package com.zbc.netty.nio.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
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
    private final static String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private final static String HTTP_DATE_GMT_TIMEZONE = "GMT";
    private final static String BASE_URL = "E:/";

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

    private void sendFile(ChannelHandlerContext ctx, FullHttpRequest request, File file) throws IOException, ParseException {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            long length = raf.length();
            MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, Unpooled.copiedBuffer(fileToByte(file)));
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, length + "")
                    .set(CONTENT_TYPE, mftm.getContentType(file))
                    .set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.writeAndFlush(response)
                    .addListener(ChannelFutureListener.CLOSE);
        } catch (FileNotFoundException e) {
            log.debug("文件不存在!", e);
            sendError(ctx, NOT_FOUND);
        }

    }

    private byte[] fileToByte(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int size;
            while ((size = fis.read(buf)) != -1) {
                bos.write(buf, 0, size);
            }
            fis.close();
            bos.close();

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private void sendDir(ChannelHandlerContext ctx, String uri, String[] list) {
        StringBuilder html = new StringBuilder();
        if (!uri.equals("/")) {
            int i = uri.lastIndexOf("/");
            i = i == 0 ? 1 : i;
            html.append("<a href='").append(uri.substring(0, i)).append("'>返回</a><br/>");
        } else {
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

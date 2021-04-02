package com.zbc.netty.nio.http;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

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
public class HttpFileHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private final static Logger log = LoggerFactory.getLogger(HttpFileHandler.class);
    private final static String BASE_URL = "E:";

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
        String uri = URLDecoder.decode(msg.uri(), "utf-8");
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

    private void sendFile(ChannelHandlerContext ctx, HttpRequest request, File file) throws IOException, ParseException {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            long fileLength = randomAccessFile.length();
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            HttpHeaderUtil.setContentLength(response, fileLength);
            setContentTypeHeader(response, file);
            if (HttpHeaderUtil.isKeepAlive(request)) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);


            ChannelFuture sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                    if (total < 0) {
                        log.debug("file progress: " + progress);
                    } else {
                        log.debug("file progress: " + progress + " / " + total + ", total：" + total + ", progress：" + progress);
                    }
                }

                @Override
                public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                    log.debug("file progress complete.");
                    randomAccessFile.close();
                }
            });

            ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!HttpHeaderUtil.isKeepAlive(request)) {
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            }

        } catch (FileNotFoundException e) {
            log.debug("文件不存在!", e);
            sendError(ctx, NOT_FOUND);
        }

    }

    private void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }

    private void sendDir(ChannelHandlerContext ctx, String uri, String[] list) throws IOException, TemplateException {
        ArrayList<Map<String, String>> objList = new ArrayList<>();

        if (!uri.equals("/")) {
            int i = uri.lastIndexOf("/");
            i = i == 0 ? 1 : i;
            Map<String, String> map = new HashMap<>();
            map.put("href", uri.substring(0, i));
            map.put("name", "..");
            objList.add(map);
        } else {
            uri = "";
        }
        for (String child : list) {
            File file = new File(BASE_URL.concat(uri), child);
            if (file.isHidden())
                continue;
            Map<String, String> map = new HashMap<>();
            map.put("href", new StringJoiner("/")
                    .add(uri)
                    .add(child)
                    .toString()
            );
            map.put("name", child);
            objList.add(map);
        }
        Template template = FreemarkerUtils.getTemplate("index.ftl");
        Map<String, Object> map = new HashMap<>();
        map.put("list", objList);
        StringWriter writer = new StringWriter();
        template.process(map, writer);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, Unpooled.copiedBuffer(writer.toString(), StandardCharsets.UTF_8));
        response.headers()
                .set(CONTENT_TYPE, "text/html; charset=utf-8");
        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus hrs) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, hrs,
                Unpooled.copiedBuffer("Failure: " + hrs.toString(), StandardCharsets.UTF_8));
        response.headers()
                .set(CONTENT_TYPE, "text/plain; charset=utf-8");
        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }
}

package com.bingco.graphql.sock;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * https中使用wss协议的websocket测试用例
 * 搭建步骤：
 * 1. 生成本地自签HTTPS证书并导入受信任库，参考https://github.com/bingco-zhan/notes/blob/main/常见证书说明.txt
 * 2. nginx配置sock代理：
 * <pre>
 *     location /sock {
 *         proxy_pass http://websock;
 *         proxy_http_version 1.1;
 *         proxy_read_timeout 1800s;
 *         proxy_send_timeout 1800s;
 *         proxy_set_header Upgrade $http_upgrade;
 *         proxy_set_header Connection "Upgrade";
 *     }
 * </pre>
 */
@Component
@ServerEndpoint("/sock")
public class SockServer {
    @OnOpen()
    public void open(Session session) {}

    @OnClose()
    public void close() {}

    @OnMessage()
    public void message(String msg, Session session) {}

    @OnError()
    public void error(Session session, Throwable e) {}
}

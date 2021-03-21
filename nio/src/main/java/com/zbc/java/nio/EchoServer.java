package com.zbc.java.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * <H3>  </H3>
 * <p>
 * create: 2021/3/16 <br/>
 * email: bingco.zn@gmail.com <br/>
 *
 * @author zhan_bingcong
 * @version 1.0
 * @since jdk8+
 */
public class EchoServer {

    private boolean running = true;

    public static void main(String[] args) throws IOException {
        int portNumber = 8080;
        new EchoServer().start(portNumber);
    }

    private void start(int portNumber) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket()
                .bind(new InetSocketAddress(portNumber), 2048);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        process(selector, ssc);
    }

    private void process(Selector selector, ServerSocketChannel ssc) throws IOException {
        while(running) {
            if (selector.select(1000) == 0)
                continue;


            Set<SelectionKey> sks = selector.selectedKeys();
            Iterator<SelectionKey> iterator = sks.iterator();
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                iterator.remove();
                if (!sk.isValid())
                    continue;


                if (sk.isAcceptable()) {
                    SocketChannel channel = ((ServerSocketChannel) sk.channel()).accept();
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                    System.out.println("echo: can accept!");
                }

                if (sk.isReadable()) {
                    System.out.println("echo: can read!");
                    ByteBuffer buffer = ByteBuffer.allocate(2048);
                    SocketChannel channel = (SocketChannel) sk.channel();
                    try {
                        if (channel.read(buffer) > 0) {
                            buffer.flip();
                            byte[] buf = new byte[buffer.remaining()];
                            buffer.get(buf);
                            String msg = new String(buf, StandardCharsets.UTF_8);
                            System.out.println("echo: " + msg);


                            if (!"done".equals(msg)) {
                                buffer.flip();
                                channel.write(buffer);
                            } else {
                                running = false;
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("echo: disconnect!");
                        channel.close();
                    }
                }
            }
        }

        selector.close();
        ssc.close();
    }

}

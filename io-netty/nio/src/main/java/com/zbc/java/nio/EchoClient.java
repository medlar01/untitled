package com.zbc.java.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
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
public class EchoClient {

    private boolean running = true;

    public static void main(String[] args) throws IOException {
        int portNumber = 8080;
        new EchoClient().start(portNumber);
    }

    private void start(int portNumber) throws IOException {
        Selector selector = Selector.open();
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress(portNumber));
        sc.register(selector, SelectionKey.OP_CONNECT);
        start0(sc);
        process(selector, sc);
    }

    private void start0(SocketChannel sc) {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (running) {
                String msg = scanner.nextLine();
                if ("done".equals(msg))
                    running = false;

                ByteBuffer buffer = ByteBuffer.allocate(2048);
                buffer.put(msg.getBytes(StandardCharsets.UTF_8));
                buffer.flip();
                try {
                    sc.write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void process(Selector selector, SocketChannel sc) throws IOException {
        while (running) {
            if (selector.select(1000) == 0)
                continue;

            Set<SelectionKey> sks = selector.selectedKeys();
            Iterator<SelectionKey> iterator = sks.iterator();
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                iterator.remove();
                if (!sk.isValid())
                    continue;


                if (sk.isConnectable()) {
                    SocketChannel channel = (SocketChannel) sk.channel();
                    if (!channel.finishConnect())
                        continue;
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                    System.out.println("echo: connected!");
                }

                if (sk.isReadable()) {
                    SocketChannel channel = (SocketChannel) sk.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(2048);
                    channel.read(buffer);
                    buffer.flip();
                    byte[] buf = new byte[buffer.remaining()];
                    buffer.get(buf);
                    System.out.println("echo: " + new String(buf, StandardCharsets.UTF_8));
                }
            }
        }
        selector.close();
        sc.close();
    }
}

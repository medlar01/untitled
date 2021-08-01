package com.zbc.java.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * <H3>  </H3>
 * <p>
 * create: 2021/3/17 <br/>
 * email: bingco.zn@gmail.com <br/>
 *
 * @author zhan_bingcong
 * @version 1.0
 * @since jdk8+
 */
public class EchoClient {

    private CountDownLatch latch;
    private AsynchronousSocketChannel asc;

    public static void main(String[] args) throws IOException {
        int portNumber = 8080;
        new EchoClient().start(portNumber);
    }

    private void start(int portNumber) throws IOException {
        asc = AsynchronousSocketChannel.open();
        latch = new CountDownLatch(1);
        asc.connect(new InetSocketAddress("127.0.0.1", portNumber), this, new ConnectHandler(asc, this));
        try {
            latch.await();
            asc.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static final class ConnectHandler implements CompletionHandler<Void, EchoClient> {
        private final AsynchronousSocketChannel asc;
        private final EchoClient client;

        public ConnectHandler(AsynchronousSocketChannel asc, EchoClient client) {
            this.asc = asc;
            this.client = client;
        }

        @Override
        public void completed(Void result, EchoClient attachment) {
            System.out.println("echo: connect completed!");
            new Thread(() -> {
                boolean running = true;
                while (running) {
                    Scanner scanner = new Scanner(System.in);
                    String msg = scanner.nextLine();
                    ByteBuffer buffer = ByteBuffer.allocate(msg.length());
                    buffer.put(msg.getBytes(StandardCharsets.UTF_8));
                    buffer.flip();
                    asc.write(buffer);
                    if ("done".equals(msg)) {
                        try {
                            asc.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        running = false;
                        client.latch.countDown();
                    }
                }
            }).start();
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            asc.read(buffer, buffer, new ReadHandler(client));
        }

        @Override
        public void failed(Throwable exc, EchoClient attachment) {
            System.out.println("echo: connect failed!");
            exc.printStackTrace();
            attachment.latch.countDown();
        }
    }

    public static final class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {

        private final EchoClient client;

        public ReadHandler(EchoClient client) {
            this.client = client;
        }

        @Override
        public void completed(Integer result, ByteBuffer buffer) {
            System.out.println("echo: read completed!");
            ByteBuffer newBuffer = ByteBuffer.allocate(2048);
            this.client.asc.read(newBuffer, newBuffer, this);
            if (!buffer.hasRemaining())
                return;

            buffer.flip();
            byte[] buf = new byte[buffer.remaining()];
            buffer.get(buf);
            System.out.println("echo: " + new String(buf, StandardCharsets.UTF_8));
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            System.out.println("echo: read failed!");
            this.client.latch.countDown();
        }
    }
}

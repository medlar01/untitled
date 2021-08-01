package com.zbc.java.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
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
public class EchoServer {

    private CountDownLatch latch;
    private AsynchronousServerSocketChannel assc;

    public static void main(String[] args) throws IOException {
        int portNumber = 8080;
        new EchoServer().start(portNumber);
    }

    private void start(int portNumber) throws IOException {
        assc = AsynchronousServerSocketChannel.open();
        assc.bind(new InetSocketAddress(portNumber));
        latch = new CountDownLatch(1);
        assc.accept(this, new AcceptHandler());
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static final class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, EchoServer> {

        @Override
        public void completed(AsynchronousSocketChannel asc, EchoServer attachment) {
            System.out.println("echo: accept completed!");
            attachment.assc.accept(attachment, this);
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            asc.read(buffer, buffer, new ReadHandler(asc, attachment));
        }

        @Override
        public void failed(Throwable exc, EchoServer attachment) {
            System.out.println("echo: accept failed!");
            attachment.latch.countDown();
        }
    }

    public static final class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousSocketChannel asc;
        private final EchoServer attachment;

        public ReadHandler(AsynchronousSocketChannel asc, EchoServer attachment) {
            this.asc = asc;
            this.attachment = attachment;
        }

        @Override
        public void completed(Integer result, ByteBuffer buffer) {
            System.out.println("echo: read completed " + result);
            ByteBuffer newBuffer = ByteBuffer.allocate(2048);
            this.asc.read(newBuffer, newBuffer, this);
            if (!buffer.hasRemaining())
                return;
            buffer.flip();
            byte[] buf = new byte[buffer.remaining()];
            buffer.get(buf);
            String msg = new String(buf, StandardCharsets.UTF_8);
            System.out.println("echo: " + msg);
            this.asc.write(ByteBuffer.wrap(buf));
            if ("done".equals(msg)) {
                try {
                    this.asc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.attachment.latch.countDown();
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer buffer) {
            System.out.println("echo: read failed!");
        }
    }
}

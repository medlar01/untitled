package com.zbc.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class LockMain {

    private static int count = 1;

    public static void main(String[] args) throws Exception {
        try (CuratorFramework client = CuratorFrameworkFactory.newClient(
                "127.0.0.1:2181",
                60000,
                15000,
                new ExponentialBackoffRetry(1000, 3))
        ) {
            client.start();
            List<Thread> list = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                Thread td = new Thread(() -> {
                    try {
                        Lock lock = new ZkLock(client, "/lock/main");
                        lock.lock();
                        Format formatter = new SimpleDateFormat("yyyyMMddHHmm");
                        System.out.println(Thread.currentThread() + " order no: " + formatter.format(new Date()) + "-" + (count++));
                        lock.unlock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                td.start();
                list.add(td);
            }
            for (Thread thread : list) {
                thread.join();
            }
        }
    }
}
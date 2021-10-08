package com.bingco;

public class VolatileMain implements Runnable {
//    private static boolean flag;
    private volatile static boolean flag;

    public static void main(String[] args) throws Exception {
        new Thread(new VolatileMain()).start();
        Thread.sleep(2000);
        new Thread(() -> {
            System.out.println("00 开启线程2 00");
            flag = true;
            System.out.println("00 修改flag值 00");
        }).start();
    }

    @Override
    public void run() {
        while (!flag) {}
        System.out.println("00 结束循环 00");
    }
}

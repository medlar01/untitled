package com.bingco;

import org.openjdk.jol.info.ClassLayout;

public class SynchronizedMain {
    public static void main(String[] args) {
        final ExampleObj obj = new ExampleObj();
        /*
         * 对象实列的组成
         * --------------------------------------------------
         * com.bingco.SynchronizedMain$ExampleObj object internals:
         * OFF  SZ      TYPE DESCRIPTION          VALUE
         *   0   8      (object header: mark)     0x0000000000000005 (biasable; age: 0)
         *   8   4      (object header: class)    0x00060248
         *  12   1      boolean ExampleObj.flag   false
         *  13   3      (object alignment gap)
         * Instance size: 16 bytes
         * --------------------------------------------------
         * |- 对象头
         *      |- Mark word(8个字节)
         *          32位: 25bit hash | 4bit age | 1bit biased_lock | 2bit lock
         *          64位: 56bit hash | 4bit age | 1bit biased_lock | 2bit lock
         *      |- Klass pointer/Class metadata address(4个字节)
         * |- 成员属性
         * |- 字节对齐
         *
         * ?? 对象状态 [normal(无状态)|biased(偏向锁)|promoted(锁升级)|GC]
         * 只有一条线程在执行：偏向锁
         * 多条线程交替执行(没有发生锁竞争)：轻量锁
         * 多条线程交替执行(发生锁竞争)：重量锁
         * GC
         */
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());
        synchronized (obj) {
            System.out.println("Hello synchronized");
        }
    }

    final static class ExampleObj {
        boolean flag = false;

        public void method() {
        }
    }
}

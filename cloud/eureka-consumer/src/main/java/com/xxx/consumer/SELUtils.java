package com.xxx.consumer;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import java.lang.ref.WeakReference;
import java.util.*;

public class SELUtils {
    private static SpelExpressionParser parser;
    /*
     * @软引用SoftReference: 只有在内存不足的时候JVM才会回收该对象
     * @弱引用WeakReference: 在垃圾回收器线程扫描它所管辖的内存区域的过程中，一旦发现了只具有弱引用的对象，
     *          不管当前内存空间足够与否，都会回收它的内存。不过，由于垃圾回收器是一个优先级很低的线程，因此不一定会很快发现那些只具有弱引用的对象。
     * @虚引用PhantomReference: 唯一的用处：能在对象被GC时收到系统通知，JAVA中用PhantomReference来实现虚引用
     */
    private static final Map<String, WeakReference<Expression>> cached = new HashMap<>();
    private static final Timer timer = new Timer();
    static {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("开始清理缓存 ... ...");
                Iterator<Map.Entry<String, WeakReference<Expression>>> iterator = cached.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, WeakReference<Expression>> entry = iterator.next();
                    WeakReference<Expression> reference = entry.getValue();
                    if (reference.get() == null) { // 发生GC会被清理
                        iterator.remove();
                        System.out.println("删除缓存: " + entry.getKey());
                    }
                }
            }
        }, 1000, 30 * 1000);
    }


    private static SpelExpressionParser get() {
        if (parser != null) return parser;
        synchronized (SELUtils.class) {
            if (parser != null) return parser;
            parser = new SpelExpressionParser();
            return parser;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T>T exec(Object obj, String expressionString) {
        String name = obj.getClass().getName() + "@" + obj.hashCode();
        WeakReference<Expression> reference = cached.get(name);
        Expression expression = null;
        if (reference != null) {
            expression = reference.get();
        }
        if (expression == null) {
            expression = get().parseExpression(expressionString);
            cached.put(name, new WeakReference<>(expression));
        }
        System.out.println("expression:" + expression);
        return (T) expression.getValue(obj);
    }
}

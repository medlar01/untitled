package com.xxx.consumer;

import java.util.List;

public class Extensions {

    public static <T> T ask(Ask obj, String expressionString) {
        return SELUtils.exec(obj, expressionString);
    }

    public static <T> T ask(List<? extends Ask> obj, String expressionString) {
        return SELUtils.exec(obj, expressionString);
    }
}

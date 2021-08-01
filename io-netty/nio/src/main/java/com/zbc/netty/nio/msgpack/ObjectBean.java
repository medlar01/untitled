package com.zbc.netty.nio.msgpack;

public class ObjectBean {

    private int num;
    private String name;
    private Class<?> clazz;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "ObjectBean{" +
                "num=" + num +
                ", name='" + name + '\'' +
                ", clazz=" + clazz +
                '}';
    }
}

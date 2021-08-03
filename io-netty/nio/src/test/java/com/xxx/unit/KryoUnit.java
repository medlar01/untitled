package com.xxx.unit;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xxx.unit.pojo.Tiger;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoUnit {

    @Test
    public void kryoSerialization() {
        var kryo = new Kryo();
        kryo.register(Tiger.class);

        Tiger tiger = new Tiger()
                .setAge(2)
                .setName("东北虎")
                .setColor("yellow")
                .setSex('N');

        var output = new Output(new ByteArrayOutputStream());
        kryo.writeObject(output, tiger);
        byte[] buffer = output.getBuffer();
        output.close();
        System.out.println("序列化大小: " + buffer.length);

        var input = new Input(new ByteArrayInputStream(buffer));
        Tiger newTiger = kryo.readObject(input, Tiger.class);
        System.out.println("反序列化: " + newTiger.toString());
    }
}

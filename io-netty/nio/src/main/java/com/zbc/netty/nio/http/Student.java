package com.zbc.netty.nio.http;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlRootElement
public class Student {
    private Integer id;
    private Integer age;
    private String name;
}

package com.xxx.unit.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Tiger {
    private String name;
    private String color;
    private Integer age;
    private char sex;
}

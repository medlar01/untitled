package com.xxx.consumer.vary;

import com.xxx.consumer.pojo.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper
public interface UserMap {
    UserMap INST = Mappers.getMapper(UserMap.class);
    @Mappings({
            @Mapping(expression = "java(Long.valueOf((String)map.get(\"id\")))", target = "id"),
            @Mapping(expression = "java((Integer)map.get(\"age\"))", target = "age"),
            @Mapping(expression = "java((String)map.get(\"name\"))", target = "name"),
            @Mapping(expression = "java((String)map.get(\"motto\"))", target = "motto")
    })
    User mapToUser(Map<String, ?> map);
}

package com.bingco.basic.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Mapper
public interface BasicMapper {
    List<Map<String, Serializable>> findTableList(String dbname);
    List<Map<String, Serializable>> findColumnList(String dbname, String name);
}

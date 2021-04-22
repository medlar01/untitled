package com.bingco.basic.service;

import com.bingco.basic.mapper.BasicMapper;
import com.github.pagehelper.PageSerializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Map;

import static com.github.pagehelper.page.PageMethod.startPage;

@Service
public class BasicService {

    private BasicMapper basicMapper;

    @Autowired
    public void setBasicMapper(BasicMapper basicMapper) {
        this.basicMapper = basicMapper;
    }

    public Flux<Map<String, Serializable>> findTableList() {
        return Flux.fromIterable(basicMapper.findTableList("ams-basic"));
    }

    public Mono<PageSerializable<Map<String, Serializable>>> findTableLimit(String dbname, int page, int rows) {
        return Mono.defer(() -> Mono.just(startPage(page, rows, "table_name desc")
                .doSelectPageSerializable(() -> basicMapper.findTableList(dbname))));
    }

    public Flux<Map<String, Serializable>> findColumnList(String dbname, String name) {
        return Flux.fromIterable(basicMapper.findColumnList(dbname, name));
    }

    public Mono<PageSerializable<Map<String, Serializable>>> findColumnLimit(String dbname, String name, int page, int rows) {
        return Mono.defer(() -> Mono.just(startPage(page, rows, "ordinal_position asc")
                .doSelectPageSerializable(() -> basicMapper.findColumnList(dbname, name))));
    }
}

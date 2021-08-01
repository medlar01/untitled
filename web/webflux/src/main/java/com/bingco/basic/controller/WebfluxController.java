package com.bingco.basic.controller;

import com.bingco.basic.service.BasicService;
import com.github.pagehelper.PageSerializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Map;

@RestController
public class WebfluxController {

    private BasicService basicService;

    @Autowired
    public void setBasicService(BasicService basicService) {
        this.basicService = basicService;
    }

    @GetMapping("/index")
    public Mono<String> index() {
        return Mono.just("Hello Webflux Index!");
    }

    @GetMapping("/tables")
    public Flux<Map<String, Serializable>> tables() {
        return basicService.findTableList();
    }

    @GetMapping("/table_limit")
    public Mono<PageSerializable<Map<String, Serializable>>> tableLimit(String dbname, int page, int rows) {
        return basicService.findTableLimit(dbname, page, rows);
    }

    @GetMapping("columns")
    public Flux<Map<String, Serializable>> columns(String dbname, String name) {
        return basicService.findColumnList(dbname, name);
    }

    @GetMapping("/column_limit")
    public Mono<PageSerializable<Map<String, Serializable>>> columnLimit(String dbname, String name, int page, int rows) {
        return basicService.findColumnLimit(dbname, name, page, rows);
    }
}

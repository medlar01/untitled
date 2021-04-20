package com.bingco.webflux.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class WebfluxController {

    @GetMapping("/index")
    public Mono<String> index() {
        return Mono.just("Hello Webflux Index!");
    }
}

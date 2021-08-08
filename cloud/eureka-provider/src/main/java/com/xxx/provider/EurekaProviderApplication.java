package com.xxx.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping
@SpringBootApplication
public class EurekaProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaProviderApplication.class, args);
        // TODO
        // https://github.com/seata/seata-samples/blob/master/springcloud-eureka-seata/order/src/main/java/io/seata/sample/OrderApplication.java
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/demo")
    public String demo() {
        return "OK";
    }
}

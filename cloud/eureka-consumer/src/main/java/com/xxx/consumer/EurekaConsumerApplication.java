package com.xxx.consumer;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@RequestMapping
@SpringBootApplication
public class EurekaConsumerApplication {
    private static ListableBeanFactory beanFactory;

    public static void main(String[] args) {
        beanFactory = SpringApplication.run(EurekaConsumerApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/demo")
    public String demo() {
        ResponseEntity<String> entity = restTemplate()
                .getForEntity("http://eureka-provider/demo", String.class);
        return entity.getBody();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/transfer")
    public String transfer(int fromUserId, int toUserId, double amount, String remark) {
        JdbcTemplate jdbcTemplate = beanFactory.getBean("jdbcTemplate", JdbcTemplate.class);
        jdbcTemplate.update("insert into t_record(amount, `from`, `to`, remark) values(?, ?, ?, ?)", new Object[]{ amount, fromUserId, toUserId, remark });
        ResponseEntity<String> entity = restTemplate()
                .getForEntity(String.format("http://eureka-provider/updateOrder?fromUserId=%s&toUserId=%s&amount=%s", fromUserId, toUserId, amount), String.class);
        return entity.getBody();
    }


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource dataSource() {
        return new HikariDataSource();
    }

    @Primary
    @Bean("dataSourceProxy")
    public DataSourceProxy dataSourceProxy(DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    @Bean("jdbcTemplate")
    @ConditionalOnBean(DataSourceProxy.class)
    public JdbcTemplate jdbcTemplate(DataSourceProxy dataSourceProxy) {
        return new JdbcTemplate(dataSourceProxy);
    }
}

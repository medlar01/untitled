package com.xxx.provider;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@RequestMapping
@SpringBootApplication
public class EurekaProviderApplication {
    private static ListableBeanFactory beanFactory;

    public static void main(String[] args) {
        beanFactory = SpringApplication.run(EurekaProviderApplication.class, args);
        // https://github.com/seata/seata-samples/blob/master/springcloud-eureka-seata/order/src/main/java/io/seata/sample/OrderApplication.java
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/demo")
    public String demo() {
        return "OK";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "updateOrder")
    public String order(int fromUserId, int toUserId, double amount) throws SQLException {
        JdbcTemplate jdbcTemplate = beanFactory.getBean("jdbcTemplate", JdbcTemplate.class);
        Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource())
                .getConnection();
        try {
            connection.setAutoCommit(false);
            jdbcTemplate.update("update t_card set amount = amount - ? where user_id = ?", new Object[]{amount, fromUserId});
            jdbcTemplate.update("update t_card set amount = amount + ? where user_id = ?", new Object[]{amount, toUserId});
            int a = 1/0;
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            return "FAIL";
        } finally {
            connection.close();
        }
        return "OK";
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

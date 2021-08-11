package com.xxx.provider;

import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * 下游服务
 */
@RequestMapping
@SpringBootApplication
@EnableJpaRepositories
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

    /**
     * seata AT模式示例：转账修改金额
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "at/updateOrder")
    @GlobalTransactional
    public String atOrder(int fromUserId, int toUserId, double amount) throws SQLException {
        CardDAO dao = beanFactory.getBean(CardDAO.class);
        Card c1 = dao.findById(fromUserId)
                .orElse(null);
        assert c1 != null;
        c1.setAmount(c1.getAmount() - amount);
        Card c2 = dao.findById(toUserId)
                .orElse(null);
        assert c2 != null;
        c2.setAmount(c2.getAmount() + amount);
        // 测试上游正常下游 division by zero exception 时，全局事务发生回滚
//        int a = 1 / 0;
        dao.saveAll(Arrays.asList(c1, c2));
        return "OK";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "tcc/updateOrder")
    @GlobalTransactional
    public String tccOrder(int fromUserId, int toUserId, double amount) throws SQLException {
        TccBusinessService service = beanFactory.getBean(TccBusinessService.class);
        return service.business(null, fromUserId, toUserId, amount) ? "FAIL" : "OK";
    }
}

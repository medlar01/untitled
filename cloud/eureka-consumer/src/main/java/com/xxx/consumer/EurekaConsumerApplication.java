package com.xxx.consumer;

import io.seata.spring.annotation.GlobalLock;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

/**
 * 上游服务
 */
@RequestMapping
@EnableJpaRepositories
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

    /**
     * seata AT模式示例：转账新增记录
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "at/transfer")
    @GlobalLock // @GlobalTransactional
    public String atTransfer(int fromUserId, int toUserId, double amount, String remark) {
        Record record = new Record();
        record.setFrom(fromUserId);
        record.setTo(toUserId);
        record.setAmount(amount);
        record.setRemark(remark);
        RecordDAO dao = beanFactory.getBean(RecordDAO.class);
        dao.save(record);
//        try {
            ResponseEntity<String> entity = restTemplate()
                    .getForEntity(String.format("http://eureka-provider/at/updateOrder?fromUserId=%s&toUserId=%s&amount=%s", fromUserId, toUserId, amount), String.class);
            // 测试下游正常上游 division by zero exception 时，全局事务回滚
//            int a = 1 / 0;
            return entity.getBody();
//        } catch (Exception e) {
//            return "FAIL";
//        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "tcc/transfer")
    @GlobalTransactional
    public String tccTransfer(int fromUserId, int toUserId, double amount, String remark) {
        TccBusinessService service = beanFactory.getBean(TccBusinessService.class);
        return service.business(null, fromUserId, toUserId, amount, remark) ? "OK" : "FAIL";
    }
}

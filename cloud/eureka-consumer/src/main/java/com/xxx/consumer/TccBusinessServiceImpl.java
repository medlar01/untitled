package com.xxx.consumer;

import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class TccBusinessServiceImpl implements TccBusinessService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RecordDAO dao;

    public boolean business(BusinessActionContext context, int fromUserId, int toUserId, double amount, String remark) {
        Record record = new Record();
        record.setFrom(fromUserId);
        record.setTo(toUserId);
        record.setAmount(amount);
        record.setRemark(remark);
        dao.save(record);
        log.debug("try 阶段: tid > " + context.getXid());
//        try {
        ResponseEntity<String> entity = restTemplate
                .getForEntity(String.format("http://eureka-provider/tcc/updateOrder?fromUserId=%s&toUserId=%s&amount=%s", fromUserId, toUserId, amount), String.class);
        // 测试下游正常上游 division by zero exception 时，全局事务回滚
//            int a = 1 / 0;
        return entity.getStatusCode().is2xxSuccessful();
//        } catch (Exception e) {
//            return "FAIL";
//        }
    }

    public boolean commit(BusinessActionContext context) {
        log.debug("业务执行完毕：" + context);
        return true;
    }

    public boolean rollback(BusinessActionContext context) {
        log.debug("业务执行失败：" + context);
        return true;
    }
}

package com.xxx.provider;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class TccBusinessServiceImpl implements TccBusinessService {

    @Autowired
    private CardDAO dao;

    public boolean business(
            BusinessActionContext context, int fromUserId, int toUserId, double amount) {
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
        log.debug("try 阶段: tid > " + context.getXid());
        return true;
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

package com.xxx.provider;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

@LocalTCC
public interface TccBusinessService {

    @TwoPhaseBusinessAction(name = "tccBusinessService", commitMethod = "commit", rollbackMethod = "rollback")
    boolean business(
            BusinessActionContext context,
            @BusinessActionContextParameter(paramName = "fromUserId") int fromUserId,
            @BusinessActionContextParameter(paramName = "toUserId") int toUserId,
            @BusinessActionContextParameter(paramName = "amount") double amount);

    boolean commit(BusinessActionContext context);

    boolean rollback(BusinessActionContext context);
}

package com.zbc.netty.nio.marshalling;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * <H3>  </H3>
 * <p>
 * create: 2021/3/23 <br/>
 * email: bingco.zn@gmail.com <br/>
 *
 * @author zhan_bingcong
 * @version 1.0
 * @since jdk8+
 */
@Data
@Builder
@ToString
public class SubscribeReq implements Serializable {
    private String address;
    private Integer phoneNumber;
    private String productName;
    private Integer subReqId;
    private String username;
}

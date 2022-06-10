package com.transaction.txlcnclienta.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/20 15:49
 * @Description:
 */
@FeignClient(value = "tx-client-b")
public interface BFeign {

    @PostMapping("/client/b/save")
    void save();

}
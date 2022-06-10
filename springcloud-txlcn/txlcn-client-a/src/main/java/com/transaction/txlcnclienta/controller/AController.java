package com.transaction.txlcnclienta.controller;

import com.transaction.txlcnclienta.pojo.Demo;
import com.transaction.txlcnclienta.service.AService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/20 15:47
 * @Description:
 */
@RestController
public class AController {

    @Autowired
    private AService aService;

    /**
     * 测试分布式事务
     *
     * @return
     */
    @PostMapping("/client/a/save")
    public Demo save() {
        Demo demo = new Demo();
        demo.setKid("a");
        demo.setAppName("tx-client-a");
        demo.setGroupId("a");
        aService.save(demo);
        return demo;
    }
}

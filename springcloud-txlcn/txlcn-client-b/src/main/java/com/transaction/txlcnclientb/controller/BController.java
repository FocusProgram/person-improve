package com.transaction.txlcnclientb.controller;

import com.transaction.txlcnclientb.pojo.Demo;
import com.transaction.txlcnclientb.service.BService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/20 15:47
 * @Description:
 */
@RestController
public class BController {

    @Autowired
    private BService bService;

    @PostMapping("/client/b/save")
    public void save() {
        Demo demo = new Demo();
        demo.setKid("b");
        demo.setAppName("tx-client-b");
        demo.setGroupId("b");
        demo.setDemoField("b");
        bService.save(demo);
    }
}


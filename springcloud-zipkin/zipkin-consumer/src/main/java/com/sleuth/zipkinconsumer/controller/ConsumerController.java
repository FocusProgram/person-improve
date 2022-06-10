package com.sleuth.zipkinconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/5/6 15:16
 * @Description:
 */
@RestController
public class ConsumerController {

    private static final Logger LOG = Logger.getLogger(ConsumerController.class.getName());

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/consumer")
    public String callHome() {
        LOG.log(Level.INFO, "请求 service-consumer");
        LOG.log(Level.INFO, "远程调用：http://localhost:9200/provider");
        return restTemplate.getForObject("http://localhost:9200/provider", String.class);
    }

    @RequestMapping("/consumerInfo")
    public String info() {
        LOG.log(Level.INFO, "请求 service-consumer");
        return "i'm service-consumer";
    }

}

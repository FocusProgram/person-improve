package com.sleuth.zipkinprovider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/5/6 15:08
 * @Description:
 */
@RestController
public class ProviderController {

    private static final Logger LOG = Logger.getLogger(ProviderController.class.getName());

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/provider")
    public String callHome() {
        LOG.log(Level.INFO, "请求 service-provider");
        LOG.log(Level.INFO, "远程调用：http://localhost:9100/consumerInfo");
        return restTemplate.getForObject("http://localhost:9100/consumerInfo", String.class);
    }

    @RequestMapping("/providerInfo")
    public String info() {
        LOG.log(Level.INFO, "请求 service-provider ");
        return "i'm service-provider";
    }

}

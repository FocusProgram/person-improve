package com.example.springcloudeurekaconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @Author Mr.Kong
 * @Description
 * @Date 2020/3/10 10:53
 */
@RestController
@RequestMapping("consumer")
public class ConsumerController {

    private final static String url = "http://eureka-provider/provider/get";

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("get")
    public Object get() {
        return restTemplate.getForObject(url, String.class);
    }

}

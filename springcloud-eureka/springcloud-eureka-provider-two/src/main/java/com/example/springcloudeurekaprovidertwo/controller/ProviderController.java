package com.example.springcloudeurekaprovidertwo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Mr.Kong
 * @Description
 * @Date 2020/3/10 10:40
 */
@RestController
@RequestMapping("provider")
public class ProviderController {

    @Value("${server.port}")
    private String port;

    @GetMapping("/get")
    public String get() {
        System.out.println("------------->我是提供服务提供者,端口号为" + port);
        return "我是提供服务提供者,端口号为" + port;
    }

}

package com.example.springcloudapollo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Mr.Kong
 * @Description // 实现配置文件的读取
 **/
@RestController
@RequestMapping("apollo")
public class ApolloController {

    @Value("${name:无法读取到值}")
    private String name;

    @Value("${age:0}")
    private Long age;

    @RequestMapping("/getname")
    public String getName() {
        return name + "的年龄" + age;
    }
}

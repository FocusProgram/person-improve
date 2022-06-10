package com.example.quartz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: Mr.Kong
 * @Date: 2019/9/17 17:09
 */
@RequestMapping(value = "/jenkins")
@RestController
public class JenkinsController {

    @GetMapping("/trigger")
    public Boolean trigger(){
        //push构建jenkins
        return true;
    }
}

package com.api.orderservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/5/14 09:47
 * @Description:
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @GetMapping("info")
    public String getOrder() {
        return "order";
    }

    @GetMapping("getParamter")
    public String getOrder(@RequestParam(required = false) String name) {
        return name;
    }

}

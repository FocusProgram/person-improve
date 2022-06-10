package com.api.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 服务熔断Hystrix返回路径
 *
 * @author Mr.Kong
 * @date 2020-05-17 13:45
 */
@RestController
@RequestMapping("/fallback")
public class GatewayController {

    @GetMapping("/order")
    public String fallback(){
        return "抱歉，order-service服务暂时不可用";
    }

}

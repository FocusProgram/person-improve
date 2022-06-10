package com.api.consumer.controller;

import com.api.consumer.api.ProviderFegin;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @Description: 服务消费者
 * @Author Mr.Kong
 * @Date 2020-06-14 16:13
 */
@Slf4j
@RequestMapping("consumer")
@RestController
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProviderFegin providerFegin;

    /**
     * 基于RestTemplete调用Springcloud-provide服务
     * @return
     */
    @HystrixCommand(fallbackMethod = "getDefaultUser")
    @GetMapping("getUserOne")
    public String getUserOne() {
        return restTemplate.getForObject("http://springcloud-provider/provider/user",String.class);
    }

    /**
     * 基于openFegin调用Springcloud-provide服务
     * @return
     */
    @GetMapping("getUserTwo")
    public String getUserTwo(){
        return providerFegin.getUser();
    }

    public String getDefaultUser() {
        return "Hystrix发生熔断.......";
    }

}

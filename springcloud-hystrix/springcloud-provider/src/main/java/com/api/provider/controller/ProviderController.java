package com.api.provider.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 服务提供者
 * @Author Mr.Kong
 * @Date 2020-06-14 16:19
 */
@Slf4j
@RequestMapping("provider")
@RestController
public class ProviderController {

    @GetMapping("user")
    public String getUser() {
        return "Mr.Kong";
    }

}

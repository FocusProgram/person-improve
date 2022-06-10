package com.api.consumer.api;

import com.api.consumer.fallback.ProviderFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description:
 * @Author Mr.Kong
 * @Date 2020-06-14 17:39
 */
@FeignClient(name = "springcloud-provider",path = "provider", configuration = FeignClientsConfiguration.class, fallback = ProviderFallBack.class)
public interface ProviderFegin {

    @GetMapping("user")
    public String getUser();

}

package com.api.consumer.fallback;

import com.api.consumer.api.ProviderFegin;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author Mr.Kong
 * @Date 2020-06-14 17:56
 */
@Component
public class ProviderFallBack implements ProviderFegin {

    @Override
    public String getUser() {
        return "start hystrix......";
    }
}

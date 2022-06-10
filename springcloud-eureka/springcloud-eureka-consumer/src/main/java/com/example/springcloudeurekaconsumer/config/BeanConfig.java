package com.example.springcloudeurekaconsumer.config;

import com.example.springcloudeurekaconsumer.rule.MyRule;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @Author Mr.Kong
 * @Description
 * @Date 2020/3/10 10:50
 */
@Configuration
public class BeanConfig {

    /**
     * @Bean 将服务注册到Spring容器中
     * @LoadBalanced 实现负载均衡调用服务
     **/
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

//    /**
//     * 选择随机算法
//     **/
//    @Bean
//    public RandomRule getRandomRule(){
//        return new RandomRule();
//    }

    /**
     * 自定义负载均衡算法（调用五次）
     **/
    @Bean
    public MyRule getMyRule() {
        return new MyRule();
    }
}

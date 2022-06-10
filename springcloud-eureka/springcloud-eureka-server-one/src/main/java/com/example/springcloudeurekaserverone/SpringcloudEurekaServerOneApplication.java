package com.example.springcloudeurekaserverone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SpringcloudEurekaServerOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudEurekaServerOneApplication.class, args);
    }

}

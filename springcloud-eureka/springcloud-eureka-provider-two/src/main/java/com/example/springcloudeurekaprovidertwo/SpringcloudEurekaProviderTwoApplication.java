package com.example.springcloudeurekaprovidertwo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SpringcloudEurekaProviderTwoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudEurekaProviderTwoApplication.class, args);
    }

}

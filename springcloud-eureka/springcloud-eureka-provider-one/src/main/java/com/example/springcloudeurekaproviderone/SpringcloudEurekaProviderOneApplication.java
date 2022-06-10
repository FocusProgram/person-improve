package com.example.springcloudeurekaproviderone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SpringcloudEurekaProviderOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudEurekaProviderOneApplication.class, args);
    }

}

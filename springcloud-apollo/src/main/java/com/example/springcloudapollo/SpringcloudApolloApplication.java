package com.example.springcloudapollo;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
@EnableApolloConfig
public class SpringcloudApolloApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudApolloApplication.class, args);
    }

}

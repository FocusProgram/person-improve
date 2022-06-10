package com.transaction.txlcnclienta;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDistributedTransaction
@EnableFeignClients
@EnableDiscoveryClient
public class TxlcnClientAApplication {

    public static void main(String[] args) {
        SpringApplication.run(TxlcnClientAApplication.class, args);
    }

}

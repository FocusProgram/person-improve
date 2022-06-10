package com.transaction.txlcnclientb;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDistributedTransaction
@EnableDiscoveryClient
public class TxlcnClientBApplication {

    public static void main(String[] args) {
        SpringApplication.run(TxlcnClientBApplication.class, args);
    }

}

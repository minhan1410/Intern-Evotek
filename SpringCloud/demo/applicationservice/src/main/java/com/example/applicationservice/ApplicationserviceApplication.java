package com.example.applicationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
// tao server va dk vs discovery server
public class ApplicationserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationserviceApplication.class, args);
    }

}

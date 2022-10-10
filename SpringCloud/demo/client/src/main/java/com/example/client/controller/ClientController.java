package com.example.client.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {
    final EurekaClient eurekaClient;
    final RestTemplateBuilder restTemplateBuilder;

    public ClientController(EurekaClient eurekaClient, RestTemplateBuilder restTemplateBuilder) {
        this.eurekaClient = eurekaClient;
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @GetMapping()
    public String callService() {
//    * Goi application-service tu discovery server
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("application-service", false);
        String homePageUrl = instanceInfo.getHomePageUrl();

        ResponseEntity<String> response = restTemplateBuilder.build().exchange(homePageUrl, HttpMethod.GET, null, String.class);

        return response.getBody();
    }
}

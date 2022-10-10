package com.example.applicationservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Value("${service.instance.name}")
    private String instance;

    @GetMapping()
    public String hello() {
        return "hello " + instance;
    }
}

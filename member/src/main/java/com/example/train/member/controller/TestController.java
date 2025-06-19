package com.example.train.member.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
//spring cloud注解，用于动态更新nacos配置
@RefreshScope
public class TestController {

    @Value("${test.nacos}")
    private String testNacos;

    @GetMapping("/hello")
    public String hello() {
        return String.format("hello %s", testNacos);
    }
}

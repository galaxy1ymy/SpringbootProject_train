package com.example.train.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
//spring cloud注解，用于动态更新nacos配置
@RefreshScope
public class TestController {

    @Value("${test.nacos}")
    private String testNacos;

    @Autowired
    Environment environment;

    @GetMapping("/hello")
    public String hello() {
        String port=environment.getProperty("local.server.port");
        return String.format("hello %s! 端口: %s", testNacos,port);
    }
}

package com.blog.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication
public class BlogGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogGatewayApplication.class, args);
    }
}

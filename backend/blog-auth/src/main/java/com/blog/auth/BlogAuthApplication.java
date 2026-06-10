package com.blog.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BlogAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogAuthApplication.class, args);
    }
}

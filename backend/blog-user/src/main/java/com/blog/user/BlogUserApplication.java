package com.blog.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BlogUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogUserApplication.class, args);
    }
}

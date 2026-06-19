package com.blog.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.blog.auth", "com.blog.common"})
@EntityScan(basePackages = {"com.blog.auth.entity", "com.blog.common.entity"})
@EnableJpaRepositories(basePackages = {"com.blog.auth.repository", "com.blog.common.repository"})
@EnableJpaAuditing
public class BlogAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogAuthApplication.class, args);
    }
}

package com.blog.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"com.blog.service", "com.blog.common"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.blog\\.auth\\..*"))
@EntityScan(basePackages = {"com.blog.service.entity", "com.blog.common.entity"})
@EnableJpaRepositories(basePackages = {"com.blog.service.repository", "com.blog.common.repository"})
@EnableJpaAuditing
@EnableScheduling
public class BlogServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogServiceApplication.class, args);
    }
}

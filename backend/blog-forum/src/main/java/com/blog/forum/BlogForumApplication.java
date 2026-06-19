package com.blog.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.blog.forum", "com.blog.common"})
@EntityScan(basePackages = {"com.blog.forum.entity", "com.blog.common.entity"})
@EnableJpaRepositories(basePackages = {"com.blog.forum.repository", "com.blog.common.repository"})
@EnableJpaAuditing
@EnableScheduling
public class BlogForumApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogForumApplication.class, args);
    }
}

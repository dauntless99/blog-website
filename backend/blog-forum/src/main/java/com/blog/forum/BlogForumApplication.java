package com.blog.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BlogForumApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogForumApplication.class, args);
    }
}

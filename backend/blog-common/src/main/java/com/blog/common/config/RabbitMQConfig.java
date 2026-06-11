package com.blog.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 * 定义队列、交换机、路由键及绑定关系
 */
@Configuration
public class RabbitMQConfig {

    // ==================== 队列定义 ====================

    /** 缓存清除队列 - 用于通知清除Redis缓存 */
    public static final String QUEUE_CACHE_EVICT = "queue.cache.evict";

    /** 阅读量更新队列 - 用于异步更新文章阅读量 */
    public static final String QUEUE_VIEW_COUNT = "queue.view.count";

    /** 回复计数更新队列 - 用于异步更新帖子回复数 */
    public static final String QUEUE_REPLY_COUNT = "queue.reply.count";

    // ==================== 交换机定义 ====================

    /** 博客服务交换机 */
    public static final String EXCHANGE_BLOG = "exchange.blog";

    /** 论坛服务交换机 */
    public static final String EXCHANGE_FORUM = "exchange.forum";

    // ==================== 路由键定义 ====================

    /** 缓存清除路由键 */
    public static final String ROUTING_CACHE_EVICT = "routing.cache.evict";

    /** 阅读量更新路由键 */
    public static final String ROUTING_VIEW_COUNT = "routing.view.count";

    /** 回复计数更新路由键 */
    public static final String ROUTING_REPLY_COUNT = "routing.reply.count";

    // ==================== Blog 模块配置 ====================

    /**
     * 创建博客服务的Topic交换机
     * @return TopicExchange实例
     */
    @Bean
    public TopicExchange blogExchange() {
        return new TopicExchange(EXCHANGE_BLOG);
    }

    /**
     * 创建缓存清除队列（持久化）
     * @return Queue实例
     */
    @Bean
    public Queue cacheEvictQueue() {
        return QueueBuilder.durable(QUEUE_CACHE_EVICT).build();
    }

    /**
     * 创建阅读量更新队列（持久化）
     * @return Queue实例
     */
    @Bean
    public Queue viewCountQueue() {
        return QueueBuilder.durable(QUEUE_VIEW_COUNT).build();
    }

    /**
     * 绑定缓存清除队列到博客交换机
     * @return Binding实例
     */
    @Bean
    public Binding cacheEvictBinding() {
        return BindingBuilder.bind(cacheEvictQueue()).to(blogExchange()).with(ROUTING_CACHE_EVICT);
    }

    /**
     * 绑定阅读量更新队列到博客交换机
     * @return Binding实例
     */
    @Bean
    public Binding viewCountBinding() {
        return BindingBuilder.bind(viewCountQueue()).to(blogExchange()).with(ROUTING_VIEW_COUNT);
    }

    // ==================== Forum 模块配置 ====================

    /**
     * 创建论坛服务的Topic交换机
     * @return TopicExchange实例
     */
    @Bean
    public TopicExchange forumExchange() {
        return new TopicExchange(EXCHANGE_FORUM);
    }

    /**
     * 创建回复计数更新队列（持久化）
     * @return Queue实例
     */
    @Bean
    public Queue replyCountQueue() {
        return QueueBuilder.durable(QUEUE_REPLY_COUNT).build();
    }

    /**
     * 绑定回复计数更新队列到论坛交换机
     * @return Binding实例
     */
    @Bean
    public Binding replyCountBinding() {
        return BindingBuilder.bind(replyCountQueue()).to(forumExchange()).with(ROUTING_REPLY_COUNT);
    }

    // ==================== 消息转换器 ====================

    /**
     * 配置JSON消息转换器
     * 将消息序列化为JSON格式，便于跨语言交互
     * @return MessageConverter实例
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

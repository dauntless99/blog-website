package com.blog.forum.mq;

import com.blog.forum.entity.ForumThread;
import com.blog.forum.repository.ForumReplyRepository;
import com.blog.forum.repository.ForumThreadRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.blog.common.config.RabbitMQConfig.*;

@Component
@RequiredArgsConstructor
public class ForumMQConsumer {

    private static final Logger log = LoggerFactory.getLogger(ForumMQConsumer.class);

    private final ForumThreadRepository threadRepository;
    private final ForumReplyRepository replyRepository;

    // ===== 消费回复计数更新消息 =====
    @RabbitListener(queues = QUEUE_REPLY_COUNT)
    public void handleReplyCount(Map<String, Object> msg) {
        Long threadId = Long.valueOf(msg.get("threadId").toString());
        int count = (int) replyRepository.countByThreadId(threadId);
        ForumThread thread = threadRepository.findById(threadId).orElse(null);
        if (thread != null) {
            thread.setReplyCount(count);
            threadRepository.save(thread);
            log.debug("异步更新回复计数: threadId={}, count={}", threadId, count);
        }
    }
}

package com.blog.forum.repository;

import com.blog.forum.entity.ForumReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ForumReplyRepository extends JpaRepository<ForumReply, Long> {

    Page<ForumReply> findByThreadIdOrderByCreatedAtAsc(Long threadId, Pageable pageable);

    List<ForumReply> findByThreadIdOrderByCreatedAtAsc(Long threadId);

    long countByThreadId(Long threadId);
}

package com.blog.forum.repository;

import com.blog.forum.entity.ForumCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ForumCategoryRepository extends JpaRepository<ForumCategory, Long> {
    List<ForumCategory> findAllByOrderBySortOrderAsc();
}

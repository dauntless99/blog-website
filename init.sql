-- ============================================
-- Enterprise Blog Forum - Database Init Script (FIXED)
-- Table names aligned with JPA @Table annotations
-- ============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. Auth Service Database (blog_auth)
-- ============================================
CREATE DATABASE IF NOT EXISTS blog_auth DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog_auth;

-- Users table (User entity -> @Table(name = "users"))
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(128) NOT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `nickname` VARCHAR(30) DEFAULT NULL,
  `avatar` VARCHAR(500) DEFAULT NULL,
  `bio` VARCHAR(200) DEFAULT NULL,
  `role` INT NOT NULL DEFAULT 0,
  `level_id` BIGINT DEFAULT NULL,
  `points` INT NOT NULL DEFAULT 0,
  `status` INT NOT NULL DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Roles table (Role entity -> @Table(name = "roles"))
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(30) NOT NULL,
  `role_key` VARCHAR(50) NOT NULL,
  `display_name` VARCHAR(50) DEFAULT NULL,
  `description` VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User-Role (UserRole entity -> @Table(name = "user_roles"))
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User levels (UserLevel entity -> @Table(name = "user_levels"))
DROP TABLE IF EXISTS `user_levels`;
CREATE TABLE `user_levels` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(30) NOT NULL,
  `icon` VARCHAR(100) DEFAULT NULL,
  `min_points` INT NOT NULL DEFAULT 0,
  `color` VARCHAR(20) DEFAULT NULL,
  `privileges` VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Point records (PointRecord entity -> @Table(name = "point_records"))
DROP TABLE IF EXISTS `point_records`;
CREATE TABLE `point_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `points` INT NOT NULL,
  `type` VARCHAR(50) NOT NULL,
  `target_id` BIGINT DEFAULT NULL,
  `description` VARCHAR(200) DEFAULT NULL,
  `before_points` INT NOT NULL DEFAULT 0,
  `after_points` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Departments (Department entity -> @Table(name = "departments"))
DROP TABLE IF EXISTS `departments`;
CREATE TABLE `departments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `parent_id` BIGINT DEFAULT 0,
  `department_name` VARCHAR(50) NOT NULL,
  `leader` VARCHAR(50) DEFAULT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `sort_order` INT DEFAULT 0,
  `status` INT DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Announcements (Announcement entity -> @Table(name = "announcements"))
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT NOT NULL,
  `priority` INT DEFAULT 0,
  `status` VARCHAR(20) DEFAULT 'draft',
  `is_top` INT DEFAULT 0,
  `publish_time` DATETIME DEFAULT NULL,
  `end_time` DATETIME DEFAULT NULL,
  `publisher_id` BIGINT DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- File uploads (FileUpload entity -> @Table(name = "file_uploads"))
DROP TABLE IF EXISTS `file_uploads`;
CREATE TABLE `file_uploads` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `file_name` VARCHAR(255) NOT NULL,
  `file_path` VARCHAR(500) NOT NULL,
  `file_size` BIGINT NOT NULL,
  `file_type` VARCHAR(50) DEFAULT NULL,
  `uploader_id` BIGINT DEFAULT NULL,
  `download_count` INT DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 2. Blog Service Database (blog_service)
-- ============================================
CREATE DATABASE IF NOT EXISTS blog_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog_service;

-- Blog posts (BlogPost entity -> @Table(name = "blog_posts"))
DROP TABLE IF EXISTS `blog_posts`;
CREATE TABLE `blog_posts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `content` LONGTEXT NOT NULL,
  `summary` VARCHAR(500) DEFAULT NULL,
  `author_id` BIGINT NOT NULL,
  `author_name` VARCHAR(30) DEFAULT NULL,
  `category` VARCHAR(50) DEFAULT NULL,
  `tags` VARCHAR(200) DEFAULT NULL,
  `view_count` INT NOT NULL DEFAULT 0,
  `like_count` INT NOT NULL DEFAULT 0,
  `comment_count` INT NOT NULL DEFAULT 0,
  `status` VARCHAR(20) NOT NULL DEFAULT 'published',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_category` (`category`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Blog comments (BlogComment entity -> @Table(name = "blog_comments"))
DROP TABLE IF EXISTS `blog_comments`;
CREATE TABLE `blog_comments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL,
  `content` LONGTEXT NOT NULL,
  `author_id` BIGINT NOT NULL,
  `author_name` VARCHAR(30) DEFAULT NULL,
  `author_avatar` VARCHAR(500) DEFAULT NULL,
  `parent_id` BIGINT DEFAULT NULL,
  `like_count` INT NOT NULL DEFAULT 0,
  `status` VARCHAR(20) NOT NULL DEFAULT 'approved',
  `is_admin_reply` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_author_id` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Blog likes (BlogLike entity -> @Table(name = "blog_likes"))
DROP TABLE IF EXISTS `blog_likes`;
CREATE TABLE `blog_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(20) NOT NULL DEFAULT 'POST',
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 3. Forum Service Database (blog_forum)
-- ============================================
CREATE DATABASE IF NOT EXISTS blog_forum DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog_forum;

-- Forum categories (ForumCategory entity -> @Table(name = "forum_categories"))
DROP TABLE IF EXISTS `forum_categories`;
CREATE TABLE `forum_categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(200) DEFAULT NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  `thread_count` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Forum threads (ForumThread entity -> @Table(name = "forum_threads"))
DROP TABLE IF EXISTS `forum_threads`;
CREATE TABLE `forum_threads` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `content` LONGTEXT NOT NULL,
  `category_id` BIGINT NOT NULL,
  `category_name` VARCHAR(50) DEFAULT NULL,
  `author_id` BIGINT NOT NULL,
  `author_name` VARCHAR(30) DEFAULT NULL,
  `view_count` INT NOT NULL DEFAULT 0,
  `reply_count` INT NOT NULL DEFAULT 0,
  `is_pinned` INT NOT NULL DEFAULT 0,
  `is_locked` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_author_id` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Forum replies (ForumReply entity -> @Table(name = "forum_replies"))
DROP TABLE IF EXISTS `forum_replies`;
CREATE TABLE `forum_replies` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `content` LONGTEXT NOT NULL,
  `thread_id` BIGINT NOT NULL,
  `author_id` BIGINT NOT NULL,
  `author_name` VARCHAR(30) DEFAULT NULL,
  `parent_id` BIGINT DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_thread_id` (`thread_id`),
  KEY `idx_author_id` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 4. Common Database (blog_common)
-- ============================================
CREATE DATABASE IF NOT EXISTS blog_common DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog_common;

-- Private messages (PrivateMessage entity -> @Table(name = "private_messages"))
DROP TABLE IF EXISTS `private_messages`;
CREATE TABLE `private_messages` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sender_id` BIGINT NOT NULL,
  `receiver_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `is_read` INT DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Notifications (Notification entity -> @Table(name = "notifications"))
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(30) NOT NULL,
  `title` VARCHAR(100) DEFAULT NULL,
  `content` TEXT NOT NULL,
  `source_type` VARCHAR(20) DEFAULT NULL,
  `source_id` BIGINT DEFAULT NULL,
  `is_read` INT DEFAULT 0,
  `read_time` DATETIME DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 5. User Service Database (blog_user)
-- ============================================
CREATE DATABASE IF NOT EXISTS blog_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog_user;

-- User profiles (UserProfile entity -> @Table(name = "user_profiles"))
DROP TABLE IF EXISTS `user_profiles`;
CREATE TABLE `user_profiles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `nickname` VARCHAR(30) DEFAULT NULL,
  `avatar` VARCHAR(500) DEFAULT NULL,
  `bio` VARCHAR(200) DEFAULT NULL,
  `website` VARCHAR(100) DEFAULT NULL,
  `location` VARCHAR(50) DEFAULT NULL,
  `post_count` INT NOT NULL DEFAULT 0,
  `thread_count` INT NOT NULL DEFAULT 0,
  `reply_count` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 6. Initial Seed Data
-- ============================================

-- Default roles
INSERT INTO blog_auth.roles (role_name, role_key, display_name, description) VALUES
('Super Admin', 'SUPER_ADMIN', 'Super Admin', 'Full system permissions'),
('Normal User', 'USER', 'Normal User', 'Regular registered user'),
('Moderator', 'MODERATOR', 'Moderator', 'Forum moderator'),
('VIP User', 'VIP', 'VIP User', 'VIP member');

-- Default users (password: 123456)
INSERT INTO blog_auth.users (username, password, email, nickname, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@example.com', 'Admin', 1, 1),
('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'test@example.com', 'TestUser', 0, 1);

-- Assign admin role
INSERT INTO blog_auth.user_roles (user_id, role_id) VALUES (1, 1);

-- User levels
INSERT INTO blog_auth.user_levels (name, icon, min_points, color, privileges) VALUES
('Newbie', 'star', 0, '#909399', 'Basic access'),
('Junior', 'star-filled', 101, '#67C23A', 'Create posts'),
('Intermediate', 'diamond', 501, '#409EFF', 'Post & comment'),
('Senior', 'crown', 2001, '#E6A23C', 'All features'),
('Expert', 'trophy', 5001, '#F56C6C', 'All + moderation');

-- Default departments
INSERT INTO blog_auth.departments (parent_id, department_name, leader, sort_order) VALUES
(0, 'Headquarters', 'admin', 1),
(1, 'Technology', 'admin', 1),
(1, 'Operations', 'admin', 2),
(1, 'Marketing', 'admin', 3);

-- Default announcement
INSERT INTO blog_auth.announcements (title, content, priority, status, is_top, publisher_id) VALUES
('Welcome to Enterprise Forum', 'Welcome! Please follow the community guidelines.', 1, 'published', 1, 1);

-- Forum categories
INSERT INTO blog_forum.forum_categories (name, description, sort_order) VALUES
('Tech Talk', 'Share technical insights', 1),
('Product Feedback', 'Product suggestions', 2),
('Workplace', 'Work experience sharing', 3),
('Leisure', 'Relax and have fun', 4);

-- Sample blog posts
INSERT INTO blog_service.blog_posts (title, content, summary, category, author_id, author_name, status, view_count, like_count) VALUES
('Welcome to Blog', 'Welcome post.\n\n## Features\n\n- Post articles\n- Comments\n- Likes', 'Welcome to the blog system', 'System', 1, 'admin', 'published', 100, 5),
('Spring Boot Best Practices', 'Spring Boot tips.\n\n## Configuration\nUse YAML...', 'Spring Boot tips and best practices', 'Java', 1, 'admin', 'published', 250, 15);

-- Sample forum threads
INSERT INTO blog_forum.forum_threads (title, content, category_id, author_id, author_name, reply_count) VALUES
('New Member Here', 'Hello everyone!', 3, 1, 'admin', 5),
('Spring Boot Question', 'How to optimize startup speed?', 1, 1, 'admin', 12);

-- Sample user profiles
INSERT INTO blog_user.user_profiles (user_id, nickname, post_count, thread_count, reply_count) VALUES
(1, 'Admin', 2, 2, 0),
(2, 'TestUser', 0, 0, 0);

SET FOREIGN_KEY_CHECKS = 1;

package com.blog.common.controller;

import com.blog.common.entity.PrivateMessage;
import com.blog.common.service.PrivateMessageService;
import com.blog.common.result.Result;
import com.blog.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 私信控制器
 * 处理私信的发送、接收、查询等请求
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final PrivateMessageService messageService;

    /**
     * 获取对话列表
     * @param token 用户Token
     * @return 对话列表
     */
    @GetMapping("/conversations")
    public Result<List<PrivateMessage>> getConversations(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        List<PrivateMessage> conversations = messageService.getConversations(userId);
        return Result.success(conversations);
    }

    /**
     * 获取与某个用户的消息记录
     * @param otherUserId 对方用户ID
     * @param token 用户Token
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认20）
     * @return 消息分页
     */
    @GetMapping
    public Result<Map<String, Object>> getMessages(
            @RequestParam Long otherUserId,
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = JwtUtil.getUserId(token);
        Page<PrivateMessage> messagePage = messageService.getMessages(userId, otherUserId, page, size);
        
        // 标记消息为已读
        messageService.markAsRead(userId, otherUserId);
        
        return Result.success(Map.of(
                "content", messagePage.getContent(),
                "totalElements", messagePage.getTotalElements(),
                "totalPages", messagePage.getTotalPages(),
                "currentPage", messagePage.getNumber(),
                "size", messagePage.getSize()
        ));
    }

    /**
     * 发送私信
     * @param request 请求体，包含receiverId和content
     * @param token 用户Token
     * @return 发送的消息
     */
    @PostMapping
    public Result<PrivateMessage> sendMessage(@RequestBody Map<String, Object> request,
                                              @RequestHeader("Authorization") String token) {
        try {
            Long userId = JwtUtil.getUserId(token);
            Long receiverId = Long.valueOf(request.get("receiverId").toString());
            String content = (String) request.get("content");
            
            PrivateMessage message = messageService.sendMessage(userId, receiverId, content);
            return Result.success("发送成功", message);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取未读消息数量
     * @param token 用户Token
     * @return 未读消息数
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        long count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 获取与某个用户的未读消息数量
     * @param otherUserId 对方用户ID
     * @param token 用户Token
     * @return 未读消息数
     */
    @GetMapping("/unread-count/{otherUserId}")
    public Result<Long> getUnreadCount(@PathVariable Long otherUserId,
                                        @RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        long count = messageService.getUnreadCount(userId, otherUserId);
        return Result.success(count);
    }

    /**
     * 标记消息为已读
     * @param otherUserId 对方用户ID
     * @param token 用户Token
     * @return 操作结果
     */
    @PostMapping("/mark-read")
    public Result<Void> markAsRead(@RequestParam Long otherUserId,
                                    @RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        messageService.markAsRead(userId, otherUserId);
        return Result.success("标记成功", null);
    }

    /**
     * 删除对话
     * @param otherUserId 对方用户ID
     * @param token 用户Token
     * @return 操作结果
     */
    @DeleteMapping("/conversation/{otherUserId}")
    public Result<Void> deleteConversation(@PathVariable Long otherUserId,
                                           @RequestHeader("Authorization") String token) {
        try {
            Long userId = JwtUtil.getUserId(token);
            messageService.deleteConversation(userId, otherUserId);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除单条消息
     * @param id 消息ID
     * @param token 用户Token
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id,
                                       @RequestHeader("Authorization") String token) {
        try {
            Long userId = JwtUtil.getUserId(token);
            messageService.deleteMessage(id, userId);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
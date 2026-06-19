import api from './index'

/**
 * 用户相关API
 */

// 获取用户资料
export function getUserProfile(userId) {
  return api.get(`/user/profile/${userId}`)
}

// 更新用户资料
export function updateProfile(data) {
  return api.put('/user/profile', data)
}

// 获取当前用户资料
export function getMyProfile() {
  return api.get('/user/my-profile')
}

// 上传头像
export function uploadAvatar(formData) {
  return api.post('/user/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 获取用户发布的文章
export function getUserPosts(userId, params) {
  return api.get(`/user/${userId}/posts`, { params })
}

// 获取用户发布的帖子
export function getUserThreads(userId, params) {
  return api.get(`/user/${userId}/threads`, { params })
}

// 获取用户积分记录
export function getUserPoints(params) {
  return api.get('/user/points', { params })
}

// 获取用户通知列表
export function getNotifications(params) {
  return api.get('/notifications', { params })
}

// 获取未读通知数量
export function getUnreadCount() {
  return api.get('/notifications/unread-count')
}

// 标记通知已读
export function markAsRead(id) {
  return api.put(`/notifications/${id}/read`)
}

// 标记所有通知已读
export function markAllAsRead() {
  return api.put('/notifications/read-all')
}

// 删除通知
export function deleteNotification(id) {
  return api.delete(`/notifications/${id}`)
}

// 获取私信列表
export function getPrivateMessages(params) {
  return api.get('/messages', { params })
}

// 发送私信
export function sendMessage(data) {
  return api.post('/messages', data)
}

// 获取与某用户的聊天记录
export function getChatHistory(userId, params) {
  return api.get(`/messages/chat/${userId}`, { params })
}

// 标记私信已读
export function markMessageAsRead(id) {
  return api.put(`/messages/${id}/read`)
}

// 删除私信
export function deleteMessage(id) {
  return api.delete(`/messages/${id}`)
}

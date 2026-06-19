import api from './index'

/**
 * 论坛相关API
 */

// 获取论坛分类列表
export function getForumCategories() {
  return api.get('/forum/categories')
}

// 获取帖子列表
export function getThreads(params) {
  return api.get('/forum/threads', { params })
}

// 获取帖子详情
export function getThreadDetail(id) {
  return api.get(`/forum/threads/${id}`)
}

// 创建帖子
export function createThread(data) {
  return api.post('/forum/threads', data)
}

// 更新帖子
export function updateThread(id, data) {
  return api.put(`/forum/threads/${id}`, data)
}

// 删除帖子
export function deleteThread(id) {
  return api.delete(`/forum/threads/${id}`)
}

// 获取帖子回复
export function getReplies(threadId, params) {
  return api.get(`/forum/replies/${threadId}`, { params })
}

// 添加回复
export function addReply(data) {
  return api.post('/forum/replies', data)
}

// 删除回复
export function deleteReply(replyId) {
  return api.delete(`/forum/replies/${replyId}`)
}

// 获取热门帖子
export function getHotThreads() {
  return api.get('/forum/hot')
}

// 获取最新帖子
export function getLatestThreads() {
  return api.get('/forum/latest')
}

// 获取我的帖子
export function getMyThreads(params) {
  return api.get('/forum/my-threads', { params })
}

// 置顶帖子（管理员）
export function topThread(id) {
  return api.put(`/forum/threads/${id}/top`)
}

// 锁定帖子（管理员）
export function lockThread(id) {
  return api.put(`/forum/threads/${id}/lock`)
}

// 设置精华（管理员）
export function essenceThread(id) {
  return api.put(`/forum/threads/${id}/essence`)
}

// 搜索帖子
export function searchThreads(params) {
  return api.get('/search/threads', { params })
}

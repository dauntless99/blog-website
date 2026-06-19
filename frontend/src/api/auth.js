import api from './index'

/**
 * 认证相关API
 */

// 用户登录
export function login(data) {
  return api.post('/auth/login', data)
}

// 用户注册
export function register(data) {
  return api.post('/auth/register', data)
}

// 获取当前用户信息
export function getCurrentUser() {
  return api.get('/auth/current')
}

// 用户登出
export function logout() {
  return api.post('/auth/logout')
}

// 获取用户列表（管理员）
export function getUserList(params) {
  return api.get('/auth/users', { params })
}

// 更新用户状态
export function updateUserStatus(userId, status) {
  return api.put(`/auth/users/${userId}/status`, { status })
}

// 删除用户
export function deleteUser(userId) {
  return api.delete(`/auth/users/${userId}`)
}

// 获取角色列表
export function getRoles() {
  return api.get('/auth/roles')
}

// 获取部门列表
export function getDepartments() {
  return api.get('/departments')
}

// 获取公告列表
export function getAnnouncements(params) {
  return api.get('/announcements', { params })
}

// 获取公告详情
export function getAnnouncementDetail(id) {
  return api.get(`/announcements/${id}`)
}

// 创建公告
export function createAnnouncement(data) {
  return api.post('/announcements', data)
}

// 更新公告
export function updateAnnouncement(id, data) {
  return api.put(`/announcements/${id}`, data)
}

// 删除公告
export function deleteAnnouncement(id) {
  return api.delete(`/announcements/${id}`)
}

// 发布公告
export function publishAnnouncement(id) {
  return api.put(`/announcements/${id}/publish`)
}

// 获取系统统计
export function getSystemStats() {
  return api.get('/admin/stats')
}

// 文件上传
export function uploadFile(formData) {
  return api.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 获取文件列表
export function getFileList(params) {
  return api.get('/files', { params })
}

// 下载文件
export function downloadFile(fileId) {
  return api.get(`/files/${fileId}/download`, { responseType: 'blob' })
}

// 删除文件
export function deleteFile(fileId) {
  return api.delete(`/files/${fileId}`)
}

// 获取积分记录
export function getPointRecords(params) {
  return api.get('/admin/points', { params })
}

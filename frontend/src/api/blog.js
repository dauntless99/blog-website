import api from './index'

/**
 * 文章相关API
 */

// 获取文章列表
export function getPosts(params) {
  return api.get('/blog/posts', { params })
}

// 获取文章详情
export function getPostDetail(id) {
  return api.get(`/blog/posts/${id}`)
}

// 创建文章
export function createPost(data) {
  return api.post('/blog/posts', data)
}

// 更新文章
export function updatePost(id, data) {
  return api.put(`/blog/posts/${id}`, data)
}

// 删除文章
export function deletePost(id) {
  return api.delete(`/blog/posts/${id}`)
}

// 获取热门文章
export function getHotPosts() {
  return api.get('/blog/hot')
}

// 获取最新文章
export function getLatestPosts() {
  return api.get('/blog/latest')
}

// 获取文章分类
export function getCategories() {
  return api.get('/blog/categories')
}

// 搜索文章
export function searchPosts(params) {
  return api.get('/search/posts', { params })
}

// 获取文章评论
export function getComments(postId, params) {
  return api.get(`/blog/comments/${postId}`, { params })
}

// 添加评论
export function addComment(data) {
  return api.post('/blog/comments', data)
}

// 删除评论
export function deleteComment(commentId) {
  return api.delete(`/blog/comments/${commentId}`)
}

// 点赞文章
export function likePost(postId) {
  return api.post(`/blog/likes/post/${postId}`)
}

// 取消点赞
export function unlikePost(postId) {
  return api.delete(`/blog/likes/post/${postId}`)
}

// 点赞评论
export function likeComment(commentId) {
  return api.post(`/blog/likes/comment/${commentId}`)
}

// 取消评论点赞
export function unlikeComment(commentId) {
  return api.delete(`/blog/likes/comment/${commentId}`)
}

// 检查是否已点赞
export function checkLikeStatus(postId) {
  return api.get(`/blog/likes/check/post/${postId}`)
}

// 提交文章审核
export function submitForReview(postId) {
  return api.put(`/blog/posts/${postId}/submit-review`)
}

// 审核文章（管理员）
export function reviewPost(postId, data) {
  return api.put(`/blog/posts/${postId}/review`, data)
}

// 获取我的文章
export function getMyPosts(params) {
  return api.get('/blog/my-posts', { params })
}

// 获取待审核文章（管理员）
export function getPendingPosts(params) {
  return api.get('/blog/pending', { params })
}

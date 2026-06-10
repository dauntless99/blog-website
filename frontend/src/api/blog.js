import api from './index'

export function getPosts(params) {
  return api.get('/blog/posts', { params })
}

export function getPostDetail(id) {
  return api.get(`/blog/posts/${id}`)
}

export function createPost(data) {
  return api.post('/blog/posts', data)
}

export function updatePost(id, data) {
  return api.put(`/blog/posts/${id}`, data)
}

export function deletePost(id) {
  return api.delete(`/blog/posts/${id}`)
}

export function getHotPosts() {
  return api.get('/blog/hot')
}

export function getLatestPosts() {
  return api.get('/blog/latest')
}

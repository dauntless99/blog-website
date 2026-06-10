import api from './index'

export function getCategories() {
  return api.get('/forum/categories')
}

export function createCategory(data) {
  return api.post('/forum/categories', data)
}

export function getThreads(params) {
  return api.get('/forum/threads', { params })
}

export function getThreadDetail(id) {
  return api.get(`/forum/threads/${id}`)
}

export function createThread(data) {
  return api.post('/forum/threads', data)
}

export function deleteThread(id) {
  return api.delete(`/forum/threads/${id}`)
}

export function getReplies(threadId, params) {
  return api.get(`/forum/threads/${threadId}/replies`, { params })
}

export function createReply(data) {
  return api.post('/forum/replies', data)
}

import api from './index'

export function getProfile(userId) {
  return api.get(`/user/profile/${userId}`)
}

export function updateProfile(data) {
  return api.put('/user/profile', data)
}

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi } from '../api/auth'
import { ElMessage } from 'element-plus'
import router from '../router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const currentUser = computed(() => user.value)

  async function login(credentials) {
    try {
      const res = await loginApi(credentials)
      if (res.code === 200) {
        token.value = res.data.token
        user.value = {
          userId: res.data.userId,
          username: res.data.username,
          nickname: res.data.nickname,
          avatar: res.data.avatar,
        }
        localStorage.setItem('token', token.value)
        localStorage.setItem('user', JSON.stringify(user.value))
        ElMessage.success(res.message)
        router.push('/')
        return true
      } else {
        ElMessage.error(res.message)
        return false
      }
    } catch (error) {
      ElMessage.error('登录失败')
      return false
    }
  }

  async function register(data) {
    try {
      const res = await registerApi(data)
      if (res.code === 200) {
        token.value = res.data.token
        user.value = {
          userId: res.data.userId,
          username: res.data.username,
          nickname: res.data.nickname,
          avatar: res.data.avatar,
        }
        localStorage.setItem('token', token.value)
        localStorage.setItem('user', JSON.stringify(user.value))
        ElMessage.success(res.message)
        router.push('/')
        return true
      } else {
        ElMessage.error(res.message)
        return false
      }
    } catch (error) {
      ElMessage.error('注册失败')
      return false
    }
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/')
    ElMessage.success('已退出登录')
  }

  return {
    token,
    user,
    isLoggedIn,
    currentUser,
    login,
    register,
    logout,
  }
})

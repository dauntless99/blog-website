import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

// 请求拦截器 - 添加token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器 - 处理错误
api.interceptors.response.use(
  (response) => {
    const res = response.data
    // 根据业务码处理
    if (res.code === 200) {
      return res
    } else if (res.code === 401) {
      // Token过期或无效
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
      return Promise.reject(new Error(res.message || 'Unauthorized'))
    } else if (res.code === 403) {
      ElMessage.error('没有权限访问该资源')
      return Promise.reject(new Error(res.message || 'Forbidden'))
    } else if (res.code === 429) {
      // 限流提示
      ElMessage.warning('请求过于频繁，请稍后再试')
      return Promise.reject(new Error(res.message || 'Rate limited'))
    } else {
      // 其他业务错误
      if (res.message) {
        ElMessage.error(res.message)
      }
      return Promise.reject(new Error(res.message || 'Request failed'))
    }
  },
  (error) => {
    // 网络错误处理
    if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else if (error.message === 'Network Error') {
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      ElMessage.error(error.message || '网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default api

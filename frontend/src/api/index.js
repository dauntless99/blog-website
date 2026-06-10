import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

// 请求拦截器 - 添加 token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器 - 处理错误
api.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
      ElMessage.error('请先登录')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  (error) => {
    ElMessage.error('网络错误，请稍后重试')
    return Promise.reject(error)
  }
)

export default api

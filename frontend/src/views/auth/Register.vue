<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>注册</h2>
      <p class="auth-subtitle">创建你的账号，加入我们的社区</p>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleRegister">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="3-20个字符" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="选填" prefix-icon="Message" size="large" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="选填，默认同用户名" prefix-icon="Postcard" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="6-30个字符" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" native-type="submit" style="width: 100%;">
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer">
        已有账号？<router-link to="/login">立即登录 →</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  nickname: '',
  password: '',
  confirmPassword: '',
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 30, message: '密码长度6-30个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  await authStore.register({
    username: form.username,
    password: form.password,
    email: form.email || undefined,
    nickname: form.nickname || undefined,
  })
  loading.value = false
}
</script>

<style scoped>
.auth-page {
  min-height: calc(100vh - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
}
.auth-card {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  width: 420px;
  max-width: 90vw;
}
.auth-card h2 { text-align: center; margin: 0 0 8px; font-size: 24px; }
.auth-subtitle { text-align: center; color: #999; margin-bottom: 30px; font-size: 14px; }
.auth-footer { text-align: center; color: #999; font-size: 14px; margin-top: 8px; }
.auth-footer a { color: #409eff; text-decoration: none; }
</style>

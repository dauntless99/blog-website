<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>登录</h2>
      <p class="auth-subtitle">登录你的账号，开始创作与交流</p>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" native-type="submit" style="width: 100%;">
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer">
        还没有账号？<router-link to="/register">立即注册 →</router-link>
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
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  await authStore.login(form)
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

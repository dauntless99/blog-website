<template>
  <header class="navbar">
    <div class="navbar-inner">
      <div class="navbar-left">
        <router-link to="/" class="logo">
          <el-icon :size="28"><ChatLineSquare /></el-icon>
          <span class="logo-text">博客论坛</span>
        </router-link>
        <nav class="nav-links">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/blog" class="nav-item">博客</router-link>
          <router-link to="/forum" class="nav-item">论坛</router-link>
        </nav>
      </div>

      <div class="navbar-right">
        <template v-if="authStore.isLoggedIn">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="32" :src="authStore.currentUser?.avatar">
                {{ authStore.currentUser?.nickname?.charAt(0) }}
              </el-avatar>
              <span class="username">{{ authStore.currentUser?.nickname }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push(`/profile/${authStore.currentUser?.userId}`)">
                  <el-icon><User /></el-icon> 个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/blog/create')">
                  <el-icon><Edit /></el-icon> 写文章
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/forum/create')">
                  <el-icon><Plus /></el-icon> 发帖子
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" size="small" @click="$router.push('/login')">登录</el-button>
          <el-button size="small" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
}
</script>

<style scoped>
.navbar {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
}
.navbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.navbar-left {
  display: flex;
  align-items: center;
  gap: 40px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: #409eff;
  font-weight: bold;
}
.logo-text {
  font-size: 20px;
}
.nav-links {
  display: flex;
  gap: 8px;
}
.nav-item {
  padding: 8px 16px;
  text-decoration: none;
  color: #333;
  border-radius: 6px;
  transition: all 0.2s;
  font-size: 15px;
}
.nav-item:hover,
.nav-item.router-link-active {
  color: #409eff;
  background: #ecf5ff;
}
.navbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
}
.user-info:hover {
  background: #f5f5f5;
}
.username {
  font-size: 14px;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>

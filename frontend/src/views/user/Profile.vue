<template>
  <div class="profile-page">
    <div class="profile-card">
      <div class="profile-header">
        <el-avatar :size="80" :src="profile.avatar">
          {{ profile.nickname?.charAt(0) || 'U' }}
        </el-avatar>
        <div class="profile-info">
          <h2>{{ profile.nickname || '未知用户' }}</h2>
          <p class="profile-bio">{{ profile.bio || '这个人很懒，什么都没写...' }}</p>
          <div class="profile-stats">
            <span><strong>{{ profile.postCount || 0 }}</strong> 文章</span>
            <span><strong>{{ profile.threadCount || 0 }}</strong> 帖子</span>
            <span><strong>{{ profile.replyCount || 0 }}</strong> 回复</span>
          </div>
          <div class="profile-extras" v-if="profile.website || profile.location">
            <span v-if="profile.location"><el-icon><Location /></el-icon> {{ profile.location }}</span>
            <span v-if="profile.website"><el-icon><Link /></el-icon> <a :href="profile.website" target="_blank">{{ profile.website }}</a></span>
          </div>
        </div>
        <el-button v-if="isOwner" type="primary" @click="openEditDialog">
          <el-icon><Edit /></el-icon> 编辑资料
        </el-button>
      </div>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑个人资料" width="500px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="输入昵称" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input v-model="editForm.bio" type="textarea" :rows="3" placeholder="介绍一下自己..." />
        </el-form-item>
        <el-form-item label="个人网站">
          <el-input v-model="editForm.website" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="所在地">
          <el-input v-model="editForm.location" placeholder="例如：北京" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProfile, updateProfile } from '../../api/user'
import { useAuthStore } from '../../stores/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const authStore = useAuthStore()

const profile = ref({})
const showEditDialog = ref(false)
const saving = ref(false)

const userId = computed(() => {
  return route.params.userId || authStore.currentUser?.userId
})

const isOwner = computed(() => {
  return authStore.currentUser && Number(userId.value) === authStore.currentUser.userId
})

const editForm = reactive({
  nickname: '',
  bio: '',
  website: '',
  location: '',
})

async function fetchProfile() {
  if (!userId.value) return
  try {
    const res = await getProfile(userId.value)
    if (res.code === 200) profile.value = res.data
  } catch (e) {}
}

function openEditDialog() {
  editForm.nickname = profile.value.nickname || ''
  editForm.bio = profile.value.bio || ''
  editForm.website = profile.value.website || ''
  editForm.location = profile.value.location || ''
  showEditDialog.value = true
}

async function handleSaveProfile() {
  saving.value = true
  try {
    const res = await updateProfile({ ...editForm })
    if (res.code === 200) {
      ElMessage.success('资料已更新')
      profile.value = res.data
      showEditDialog.value = false
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
  saving.value = false
}

onMounted(() => fetchProfile())
</script>

<style scoped>
.profile-page { max-width: 700px; margin: 0 auto; }
.profile-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  overflow: hidden;
}
.profile-header {
  padding: 40px;
  display: flex;
  align-items: flex-start;
  gap: 24px;
}
.profile-info { flex: 1; }
.profile-info h2 { font-size: 22px; margin: 0 0 8px; }
.profile-bio { color: #999; font-size: 14px; margin: 0 0 16px; line-height: 1.5; }
.profile-stats { display: flex; gap: 24px; margin-bottom: 12px; }
.profile-stats span { font-size: 14px; color: #666; }
.profile-stats strong { color: #333; margin-right: 4px; }
.profile-extras { display: flex; gap: 16px; font-size: 13px; color: #999; flex-wrap: wrap; }
.profile-extras span { display: flex; align-items: center; gap: 4px; }
.profile-extras a { color: #409eff; text-decoration: none; }
</style>

<template>
  <div class="blog-detail-page">
    <div v-if="post" class="post-detail">
      <!-- 文章头 -->
      <div class="post-header">
        <h1>{{ post.title }}</h1>
        <div class="post-meta">
          <span><el-icon><User /></el-icon> {{ post.authorName }}</span>
          <span v-if="post.category"><el-icon><Folder /></el-icon> {{ post.category }}</span>
          <span><el-icon><View /></el-icon> {{ post.viewCount }} 阅读</span>
          <span><el-icon><Clock /></el-icon> {{ formatDate(post.createdAt) }}</span>
        </div>
        <div v-if="post.tags" class="post-tags">
          <el-tag v-for="tag in post.tags.split(',')" :key="tag" size="small">{{ tag.trim() }}</el-tag>
        </div>
      </div>

      <!-- 文章内容 -->
      <div class="post-content" v-html="renderedContent"></div>

      <!-- 操作按钮 -->
      <div v-if="isAuthor" class="post-actions">
        <el-button type="primary" @click="$router.push(`/blog/edit/${post.id}`)">
          <el-icon><Edit /></el-icon> 编辑
        </el-button>
        <el-button type="danger" @click="handleDelete">
          <el-icon><Delete /></el-icon> 删除
        </el-button>
      </div>
    </div>

    <div v-else-if="loading" class="loading">
      <el-skeleton :rows="10" animated />
    </div>
    <el-empty v-else description="文章不存在" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPostDetail, deletePost } from '../../api/blog'
import { useAuthStore } from '../../stores/auth'
import { marked } from 'marked'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const post = ref(null)
const loading = ref(true)

const isAuthor = computed(() => {
  return authStore.currentUser && post.value &&
    authStore.currentUser.userId === post.value.authorId
})

const renderedContent = computed(() => {
  return post.value ? marked(post.value.content || '') : ''
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

async function fetchPost() {
  loading.value = true
  try {
    const res = await getPostDetail(route.params.id)
    if (res.code === 200) {
      post.value = res.data
    }
  } catch (e) {}
  loading.value = false
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
    const res = await deletePost(post.value.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      router.push('/blog')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => fetchPost())
</script>

<style scoped>
.blog-detail-page {
  max-width: 800px;
  margin: 0 auto;
}
.post-detail {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  overflow: hidden;
}
.post-header {
  padding: 30px 30px 20px;
  border-bottom: 1px solid #f0f0f0;
}
.post-header h1 { font-size: 28px; margin: 0 0 16px; color: #222; line-height: 1.4; }
.post-meta { display: flex; gap: 16px; font-size: 13px; color: #999; flex-wrap: wrap; margin-bottom: 10px; }
.post-meta span { display: flex; align-items: center; gap: 4px; }
.post-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.post-content {
  padding: 30px;
  line-height: 1.8;
  font-size: 16px;
}
.post-content :deep(h1) { font-size: 1.8em; margin: 20px 0 14px; }
.post-content :deep(h2) { font-size: 1.5em; margin: 18px 0 12px; }
.post-content :deep(h3) { font-size: 1.2em; margin: 16px 0 10px; }
.post-content :deep(p) { margin: 12px 0; }
.post-content :deep(code) { background: #f5f5f5; padding: 2px 6px; border-radius: 3px; font-size: 0.9em; }
.post-content :deep(pre) { background: #2d3748; color: #e2e8f0; padding: 20px; border-radius: 8px; overflow-x: auto; margin: 16px 0; }
.post-content :deep(blockquote) { border-left: 4px solid #409eff; padding: 8px 16px; color: #666; background: #f8f9fa; margin: 16px 0; }
.post-content :deep(img) { max-width: 100%; border-radius: 6px; margin: 12px 0; }
.post-actions { padding: 20px 30px; border-top: 1px solid #f0f0f0; display: flex; gap: 12px; }
.loading { background: #fff; padding: 30px; border-radius: 10px; }
</style>

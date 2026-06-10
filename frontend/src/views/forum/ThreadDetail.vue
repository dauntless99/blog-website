<template>
  <div class="thread-detail-page">
    <div v-if="thread" class="thread-detail">
      <!-- 帖子主内容 -->
      <div class="thread-main-card">
        <div class="thread-header">
          <h1>
            <el-tag v-if="thread.isPinned" type="danger" size="small">置顶</el-tag>
            {{ thread.title }}
          </h1>
          <div class="thread-meta">
            <span><el-icon><User /></el-icon> {{ thread.authorName }}</span>
            <span v-if="thread.categoryName"><el-icon><Folder /></el-icon> {{ thread.categoryName }}</span>
            <span><el-icon><View /></el-icon> {{ thread.viewCount }}</span>
            <span><el-icon><Clock /></el-icon> {{ formatDate(thread.createdAt) }}</span>
          </div>
        </div>
        <div class="thread-content" v-html="renderedContent"></div>
      </div>

      <!-- 回复列表 -->
      <div class="replies-section">
        <h3>回复 ({{ thread.replyCount }})</h3>
        <div v-if="replies.length > 0" class="reply-list">
          <div v-for="reply in replies" :key="reply.id" class="reply-card">
            <div class="reply-header">
              <div class="reply-author">
                <el-avatar :size="36">{{ reply.authorName?.charAt(0) }}</el-avatar>
                <div class="reply-author-info">
                  <span class="reply-name">{{ reply.authorName }}</span>
                  <span class="reply-time">{{ formatDateTime(reply.createdAt) }}</span>
                </div>
              </div>
              <span class="reply-floor">#{{ reply.id }}</span>
            </div>
            <div class="reply-content" v-html="renderMarkdown(reply.content)"></div>
          </div>
        </div>
        <el-empty v-else description="暂无回复，快来抢沙发吧" :image-size="80" />

        <Pagination
          :current-page="replyPage"
          :page-size="replySize"
          :total="replyTotal"
          @page-change="handleReplyPageChange"
          @size-change="handleReplySizeChange"
        />
      </div>

      <!-- 回复编辑器 -->
      <div v-if="authStore.isLoggedIn" class="reply-editor-card">
        <h3>发表回复</h3>
        <el-input
          v-model="replyContent"
          type="textarea"
          :rows="4"
          placeholder="写下你的回复..."
        />
        <div class="reply-submit">
          <el-button type="primary" :loading="replying" @click="handleReply">
            发布回复
          </el-button>
        </div>
      </div>
      <div v-else class="reply-login-hint">
        <el-button type="primary" @click="$router.push('/login')">登录</el-button>
        <span>后参与回复</span>
      </div>
    </div>

    <div v-else-if="loading" class="loading">
      <el-skeleton :rows="10" animated />
    </div>
    <el-empty v-else description="帖子不存在" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getThreadDetail, getReplies, createReply } from '../../api/forum'
import { useAuthStore } from '../../stores/auth'
import { marked } from 'marked'
import { ElMessage } from 'element-plus'
import Pagination from '../../components/Pagination.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const thread = ref(null)
const replies = ref([])
const loading = ref(true)
const replyContent = ref('')
const replying = ref(false)
const replyPage = ref(0)
const replySize = ref(20)
const replyTotal = ref(0)

const renderedContent = computed(() => renderMarkdown(thread.value?.content || ''))

function renderMarkdown(text) {
  return text ? marked(text) : ''
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

function formatDateTime(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}

async function fetchThread() {
  loading.value = true
  try {
    const res = await getThreadDetail(route.params.id)
    if (res.code === 200) thread.value = res.data
  } catch (e) {}
  loading.value = false
}

async function fetchReplies() {
  try {
    const res = await getReplies(route.params.id, {
      page: replyPage.value,
      size: replySize.value,
    })
    if (res.code === 200) {
      replies.value = res.data.content
      replyTotal.value = res.data.totalElements
    }
  } catch (e) {}
}

function handleReplyPageChange(page) { replyPage.value = page; fetchReplies() }
function handleReplySizeChange(size) { replySize.value = size; replyPage.value = 0; fetchReplies() }

async function handleReply() {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  replying.value = true
  try {
    const res = await createReply({
      content: replyContent.value,
      threadId: Number(route.params.id),
    })
    if (res.code === 200) {
      ElMessage.success('回复成功')
      replyContent.value = ''
      // 刷新回复和帖子信息
      await fetchReplies()
      await fetchThread()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    ElMessage.error('回复失败')
  }
  replying.value = false
}

onMounted(() => {
  fetchThread()
  fetchReplies()
})
</script>

<style scoped>
.thread-detail-page { max-width: 800px; margin: 0 auto; }
.thread-main-card {
  background: #fff;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.thread-header { border-bottom: 1px solid #f0f0f0; padding-bottom: 16px; margin-bottom: 20px; }
.thread-header h1 { font-size: 24px; margin: 0 0 12px; color: #222; }
.thread-meta { display: flex; gap: 16px; font-size: 13px; color: #999; flex-wrap: wrap; }
.thread-meta span { display: flex; align-items: center; gap: 4px; }
.thread-content { line-height: 1.8; font-size: 16px; }
.thread-content :deep(h1) { font-size: 1.6em; }
.thread-content :deep(h2) { font-size: 1.4em; }
.thread-content :deep(h3) { font-size: 1.2em; }
.thread-content :deep(pre) { background: #2d3748; color: #e2e8f0; padding: 16px; border-radius: 8px; overflow-x: auto; }
.thread-content :deep(blockquote) { border-left: 4px solid #409eff; padding-left: 16px; color: #666; background: #f8f9fa; padding: 8px 16px; }
.thread-content :deep(code) { background: #f5f5f5; padding: 2px 6px; border-radius: 3px; }

.replies-section {
  background: #fff;
  border-radius: 10px;
  padding: 24px 30px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.replies-section h3 { font-size: 17px; margin: 0 0 16px; }
.reply-card { padding: 16px 0; border-bottom: 1px solid #f5f5f5; }
.reply-card:last-child { border-bottom: none; }
.reply-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.reply-author { display: flex; align-items: center; gap: 10px; }
.reply-author-info { display: flex; flex-direction: column; }
.reply-name { font-size: 14px; font-weight: 500; color: #333; }
.reply-time { font-size: 12px; color: #bbb; }
.reply-floor { font-size: 12px; color: #ccc; }
.reply-content { line-height: 1.7; font-size: 15px; color: #444; }

.reply-editor-card {
  background: #fff;
  border-radius: 10px;
  padding: 24px 30px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.reply-editor-card h3 { font-size: 17px; margin: 0 0 12px; }
.reply-submit { margin-top: 12px; text-align: right; }

.reply-login-hint {
  background: #fff;
  border-radius: 10px;
  padding: 30px;
  text-align: center;
  color: #999;
}
.reply-login-hint span { margin-left: 8px; }

.loading { background: #fff; padding: 30px; border-radius: 10px; }
</style>

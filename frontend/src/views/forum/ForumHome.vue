<template>
  <div class="forum-page">
    <div class="page-header">
      <h1>论坛社区</h1>
      <el-button type="primary" @click="$router.push('/forum/create')">
        <el-icon><Plus /></el-icon> 发布帖子
      </el-button>
    </div>

    <!-- 分类导航 -->
    <div class="category-bar">
      <el-button
        :type="!currentCategory ? 'primary' : ''"
        size="small"
        @click="switchCategory(null)"
      >全部</el-button>
      <el-button
        v-for="cat in categories"
        :key="cat.id"
        :type="currentCategory === cat.id ? 'primary' : ''"
        size="small"
        @click="switchCategory(cat.id)"
      >{{ cat.name }}</el-button>
    </div>

    <!-- 帖子列表 -->
    <div v-if="threads.length > 0" class="thread-list">
      <div v-for="thread in threads" :key="thread.id" class="thread-card" @click="$router.push(`/forum/thread/${thread.id}`)">
        <div class="thread-main">
          <h2>
            <el-tag v-if="thread.isPinned" type="danger" size="small" style="margin-right: 6px;">置顶</el-tag>
            {{ thread.title }}
          </h2>
          <p class="thread-summary">{{ thread.content?.substring(0, 150) }}</p>
          <div class="thread-meta">
            <span><el-icon><User /></el-icon> {{ thread.authorName }}</span>
            <span v-if="thread.categoryName"><el-icon><Folder /></el-icon> {{ thread.categoryName }}</span>
            <span><el-icon><View /></el-icon> {{ thread.viewCount }}</span>
            <span><el-icon><ChatDotRound /></el-icon> {{ thread.replyCount }} 回复</span>
            <span><el-icon><Clock /></el-icon> {{ formatDate(thread.createdAt) }}</span>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无帖子" />

    <Pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      @page-change="handlePageChange"
      @size-change="handleSizeChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategories, getThreads } from '../../api/forum'
import Pagination from '../../components/Pagination.vue'

const categories = ref([])
const threads = ref([])
const currentCategory = ref(null)
const currentPage = ref(0)
const pageSize = ref(10)
const total = ref(0)

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

function switchCategory(catId) {
  currentCategory.value = catId
  currentPage.value = 0
  fetchThreads()
}

async function fetchCategories() {
  try {
    const res = await getCategories()
    if (res.code === 200) categories.value = res.data
  } catch (e) {}
}

async function fetchThreads() {
  try {
    const res = await getThreads({
      categoryId: currentCategory.value || undefined,
      page: currentPage.value,
      size: pageSize.value,
    })
    if (res.code === 200) {
      threads.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (e) {}
}

function handlePageChange(page) {
  currentPage.value = page
  fetchThreads()
}

function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 0
  fetchThreads()
}

onMounted(() => {
  fetchCategories()
  fetchThreads()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h1 { font-size: 24px; margin: 0; }
.category-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  background: #fff;
  padding: 12px 16px;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.thread-card {
  background: #fff;
  padding: 20px 24px;
  border-radius: 10px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.2s;
}
.thread-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
  transform: translateY(-1px);
}
.thread-main h2 { font-size: 17px; margin: 0 0 8px; color: #333; }
.thread-summary { color: #999; font-size: 14px; margin: 0 0 10px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.thread-meta { display: flex; gap: 16px; font-size: 13px; color: #bbb; flex-wrap: wrap; }
.thread-meta span { display: flex; align-items: center; gap: 4px; }
</style>

<template>
  <div class="blog-list-page">
    <div class="page-header">
      <h1>博客文章</h1>
      <el-button type="primary" @click="$router.push('/blog/create')">
        <el-icon><Edit /></el-icon> 写文章
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索文章..."
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
        style="width: 300px;"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="category" placeholder="全部分类" clearable @change="handleSearch" style="width: 150px;">
        <el-option label="技术" value="技术" />
        <el-option label="生活" value="生活" />
        <el-option label="随笔" value="随笔" />
        <el-option label="教程" value="教程" />
        <el-option label="其他" value="其他" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- 文章列表 -->
    <div v-if="posts.length > 0" class="post-list">
      <article v-for="post in posts" :key="post.id" class="post-card" @click="$router.push(`/blog/${post.id}`)">
        <div class="post-body">
          <h2>{{ post.title }}</h2>
          <p class="post-summary">{{ post.summary || post.content?.substring(0, 200) }}</p>
          <div class="post-meta">
            <span><el-icon><User /></el-icon> {{ post.authorName }}</span>
            <span v-if="post.category"><el-icon><Folder /></el-icon> {{ post.category }}</span>
            <span><el-icon><View /></el-icon> {{ post.viewCount }} 阅读</span>
            <span><el-icon><Clock /></el-icon> {{ formatDate(post.createdAt) }}</span>
          </div>
        </div>
        <div v-if="post.tags" class="post-tags">
          <el-tag v-for="tag in post.tags.split(',')" :key="tag" size="small" class="tag">{{ tag.trim() }}</el-tag>
        </div>
      </article>
    </div>
    <el-empty v-else description="暂无文章" />

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
import { getPosts } from '../../api/blog'
import Pagination from '../../components/Pagination.vue'

const posts = ref([])
const keyword = ref('')
const category = ref('')
const currentPage = ref(0)
const pageSize = ref(10)
const total = ref(0)

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

async function fetchPosts() {
  try {
    const res = await getPosts({
      page: currentPage.value,
      size: pageSize.value,
      keyword: keyword.value || undefined,
      category: category.value || undefined,
    })
    if (res.code === 200) {
      posts.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (e) {}
}

function handleSearch() {
  currentPage.value = 0
  fetchPosts()
}

function handlePageChange(page) {
  currentPage.value = page
  fetchPosts()
}

function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 0
  fetchPosts()
}

onMounted(() => fetchPosts())
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h1 { font-size: 24px; margin: 0; }
.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  background: #fff;
  padding: 16px;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.post-card {
  background: #fff;
  padding: 20px 24px;
  border-radius: 10px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  justify-content: space-between;
}
.post-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
  transform: translateY(-1px);
}
.post-body { flex: 1; }
.post-body h2 { font-size: 18px; margin: 0 0 8px; color: #333; }
.post-summary { color: #999; font-size: 14px; margin: 0 0 10px; line-height: 1.5; }
.post-meta { display: flex; gap: 16px; font-size: 13px; color: #bbb; flex-wrap: wrap; }
.post-meta span { display: flex; align-items: center; gap: 4px; }
.post-tags { display: flex; gap: 6px; flex-wrap: wrap; align-items: flex-start; padding-left: 20px; }
.tag { margin: 0; }
</style>

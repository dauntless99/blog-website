<template>
  <div class="home-page">
    <!-- 英雄区 -->
    <section class="hero">
      <h1>欢迎来到博客论坛</h1>
      <p>分享知识，交流想法，连接你我</p>
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="$router.push('/blog/create')">
          <el-icon><Edit /></el-icon> 写文章
        </el-button>
        <el-button size="large" @click="$router.push('/forum')">
          <el-icon><ChatDotRound /></el-icon> 逛论坛
        </el-button>
      </div>
    </section>

    <!-- 内容区 -->
    <div class="home-grid">
      <!-- 最新文章 -->
      <section class="card">
        <div class="card-header">
          <h2><el-icon><Document /></el-icon> 最新文章</h2>
          <router-link to="/blog" class="more-link">查看更多 →</router-link>
        </div>
        <div v-if="latestPosts.length > 0" class="post-list">
          <div v-for="post in latestPosts" :key="post.id" class="post-item" @click="$router.push(`/blog/${post.id}`)">
            <h3>{{ post.title }}</h3>
            <p class="post-summary">{{ post.summary || post.content?.substring(0, 150) }}</p>
            <div class="post-meta">
              <span><el-icon><User /></el-icon> {{ post.authorName }}</span>
              <span><el-icon><View /></el-icon> {{ post.viewCount }}</span>
              <span>{{ formatDate(post.createdAt) }}</span>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无文章" />
      </section>

      <!-- 侧边栏 -->
      <aside>
        <!-- 热门文章 -->
        <section class="card">
          <div class="card-header">
            <h2><el-icon><TrendCharts /></el-icon> 热门文章</h2>
          </div>
          <div v-if="hotPosts.length > 0" class="hot-list">
            <div v-for="(post, idx) in hotPosts" :key="post.id" class="hot-item" @click="$router.push(`/blog/${post.id}`)">
              <span class="hot-rank" :class="{ top3: idx < 3 }">{{ idx + 1 }}</span>
              <div class="hot-info">
                <p class="hot-title">{{ post.title }}</p>
                <span class="hot-views">{{ post.viewCount }} 次阅读</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无数据" :image-size="60" />
        </section>

        <!-- 论坛入口 -->
        <section class="card">
          <div class="card-header">
            <h2><el-icon><ChatLineSquare /></el-icon> 论坛板块</h2>
            <router-link to="/forum" class="more-link">进入论坛 →</router-link>
          </div>
          <div class="forum-entry">
            <p>与大家分享观点、提问解答、技术交流</p>
            <el-button type="primary" @click="$router.push('/forum/create')">
              <el-icon><Plus /></el-icon> 发布帖子
            </el-button>
          </div>
        </section>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLatestPosts, getHotPosts } from '../api/blog'

const latestPosts = ref([])
const hotPosts = ref([])

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

onMounted(async () => {
  try {
    const [latestRes, hotRes] = await Promise.all([
      getLatestPosts(),
      getHotPosts(),
    ])
    if (latestRes.code === 200) latestPosts.value = latestRes.data
    if (hotRes.code === 200) hotPosts.value = hotRes.data
  } catch (e) {
    // 服务未启动时静默处理
  }
})
</script>

<style scoped>
.hero {
  text-align: center;
  padding: 60px 20px;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border-radius: 12px;
  color: #fff;
  margin-bottom: 30px;
}
.hero h1 { font-size: 36px; margin-bottom: 12px; }
.hero p { font-size: 18px; opacity: 0.9; margin-bottom: 24px; }
.hero-actions { display: flex; gap: 12px; justify-content: center; }

.home-grid {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 24px;
}
.card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  margin-bottom: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}
.card-header h2 { font-size: 18px; display: flex; align-items: center; gap: 6px; margin: 0; }
.more-link { color: #409eff; text-decoration: none; font-size: 14px; }

.post-item {
  padding: 14px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.2s;
}
.post-item:hover { background: #fafafa; margin: 0 -16px; padding: 14px 16px; border-radius: 6px; }
.post-item:last-child { border-bottom: none; }
.post-item h3 { font-size: 16px; margin: 0 0 6px; color: #333; }
.post-summary { color: #999; font-size: 13px; margin: 0 0 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.post-meta { display: flex; gap: 16px; font-size: 12px; color: #bbb; }
.post-meta span { display: flex; align-items: center; gap: 3px; }

.hot-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}
.hot-item:hover { background: #fafafa; margin: 0 -12px; padding: 10px 12px; border-radius: 4px; }
.hot-item:last-child { border-bottom: none; }
.hot-rank { font-size: 16px; font-weight: bold; color: #999; width: 24px; text-align: center; }
.hot-rank.top3 { color: #409eff; }
.hot-title { font-size: 14px; margin: 0 0 4px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; width: 240px; }
.hot-views { font-size: 12px; color: #bbb; }

.forum-entry { text-align: center; padding: 16px 0; }
.forum-entry p { color: #999; font-size: 14px; margin-bottom: 12px; }

@media (max-width: 768px) {
  .home-grid { grid-template-columns: 1fr; }
}
</style>

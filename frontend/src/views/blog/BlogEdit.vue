<template>
  <div class="blog-edit-page">
    <div class="edit-header">
      <h1>{{ isEdit ? '编辑文章' : '写文章' }}</h1>
    </div>

    <div class="edit-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="输入文章标题..." size="large" />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="form.category" placeholder="选择分类" clearable style="width: 100%">
                <el-option label="技术" value="技术" />
                <el-option label="生活" value="生活" />
                <el-option label="随笔" value="随笔" />
                <el-option label="教程" value="教程" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标签 (逗号分隔)">
              <el-input v-model="form.tags" placeholder="例如: Vue, Spring, 教程" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="文章摘要 (留空则自动截取前200字)" />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <MarkdownEditor v-model="form.content" placeholder="使用 Markdown 编写文章内容..." :rows="20" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleSubmit">
            {{ isEdit ? '更新文章' : '发布文章' }}
          </el-button>
          <el-button size="large" @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createPost, updatePost, getPostDetail } from '../../api/blog'
import MarkdownEditor from '../../components/MarkdownEditor.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '',
  content: '',
  summary: '',
  category: '',
  tags: '',
})

const rules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }],
}

onMounted(async () => {
  if (isEdit.value) {
    try {
      const res = await getPostDetail(route.params.id)
      if (res.code === 200) {
        const post = res.data
        form.title = post.title
        form.content = post.content
        form.summary = post.summary || ''
        form.category = post.category || ''
        form.tags = post.tags || ''
      }
    } catch (e) {
      ElMessage.error('加载文章失败')
      router.back()
    }
  }
})

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const data = { ...form }
    const res = isEdit.value
      ? await updatePost(route.params.id, data)
      : await createPost(data)

    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '文章已更新' : '文章已发布')
      router.push(`/blog/${res.data.id}`)
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
  loading.value = false
}
</script>

<style scoped>
.edit-header { margin-bottom: 20px; }
.edit-header h1 { font-size: 24px; margin: 0; }
.edit-card {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
</style>

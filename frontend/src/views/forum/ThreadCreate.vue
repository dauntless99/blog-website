<template>
  <div class="thread-create-page">
    <div class="edit-header">
      <h1>发布帖子</h1>
    </div>

    <div class="edit-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-row :gutter="16">
          <el-col :span="16">
            <el-form-item label="帖子标题" prop="title">
              <el-input v-model="form.title" placeholder="输入帖子标题..." size="large" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="选择分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%" size="large">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="内容" prop="content">
          <MarkdownEditor v-model="form.content" placeholder="使用 Markdown 编写帖子内容..." :rows="18" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleSubmit">
            发布帖子
          </el-button>
          <el-button size="large" @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories, createThread } from '../../api/forum'
import MarkdownEditor from '../../components/MarkdownEditor.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const categories = ref([])

const form = reactive({
  title: '',
  content: '',
  categoryId: null,
})

const rules = {
  title: [{ required: true, message: '请输入帖子标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入帖子内容', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
}

onMounted(async () => {
  try {
    const res = await getCategories()
    if (res.code === 200) categories.value = res.data
  } catch (e) {}
})

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await createThread({ ...form })
    if (res.code === 200) {
      ElMessage.success('发帖成功')
      router.push(`/forum/thread/${res.data.id}`)
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    ElMessage.error('发帖失败')
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

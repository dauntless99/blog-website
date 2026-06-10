<template>
  <div class="markdown-editor">
    <div class="editor-toolbar">
      <el-button-group>
        <el-button size="small" @click="insertMarkdown('**', '**')" title="粗体">
          <strong>B</strong>
        </el-button>
        <el-button size="small" @click="insertMarkdown('*', '*')" title="斜体">
          <em>I</em>
        </el-button>
        <el-button size="small" @click="insertMarkdown('~~', '~~')" title="删除线">
          <del>S</del>
        </el-button>
        <el-button size="small" @click="insertMarkdown('`', '`')" title="行内代码">
          &lt;/&gt;
        </el-button>
      </el-button-group>
      <el-button-group style="margin-left: 8px;">
        <el-button size="small" @click="insertMarkdown('\n# ', '')" title="标题1">H1</el-button>
        <el-button size="small" @click="insertMarkdown('\n## ', '')" title="标题2">H2</el-button>
        <el-button size="small" @click="insertMarkdown('\n### ', '')" title="标题3">H3</el-button>
      </el-button-group>
      <el-button-group style="margin-left: 8px;">
        <el-button size="small" @click="insertMarkdown('\n- ', '')" title="无序列表">列表</el-button>
        <el-button size="small" @click="insertMarkdown('\n1. ', '')" title="有序列表">编号</el-button>
        <el-button size="small" @click="insertMarkdown('\n> ', '')" title="引用">引用</el-button>
      </el-button-group>
      <el-button-group style="margin-left: 8px;">
        <el-button size="small" @click="insertMarkdown('\n```\n', '\n```\n')" title="代码块">代码</el-button>
        <el-button size="small" @click="insertMarkdown('[', '](url)')" title="链接">链接</el-button>
        <el-button size="small" @click="insertMarkdown('![alt](', ')')" title="图片">图片</el-button>
      </el-button-group>
      <el-switch
        v-model="showPreview"
        active-text="预览"
        inactive-text="编辑"
        style="margin-left: auto;"
      />
    </div>

    <div class="editor-body">
      <el-input
        v-show="!showPreview"
        type="textarea"
        :model-value="modelValue"
        @update:model-value="$emit('update:modelValue', $event)"
        :rows="rows"
        :placeholder="placeholder"
        class="editor-textarea"
        ref="textareaRef"
      />
      <div
        v-show="showPreview"
        class="preview-pane"
        v-html="renderedMarkdown"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { marked } from 'marked'

const props = defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: '请输入内容...' },
  rows: { type: Number, default: 15 },
})

defineEmits(['update:modelValue'])

const showPreview = ref(false)
const textareaRef = ref(null)

const renderedMarkdown = computed(() => {
  if (!props.modelValue) return ''
  return marked(props.modelValue)
})

function insertMarkdown(before, after) {
  showPreview.value = false
  const textarea = textareaRef.value?.$el?.querySelector('textarea')
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const text = props.modelValue
  const selected = text.substring(start, end)
  const newText = text.substring(0, start) + before + selected + after + text.substring(end)

  // 触发更新
  const event = new Event('update:modelValue')
  // Use native setter
  const nativeSetter = Object.getOwnPropertyDescriptor(
    HTMLTextAreaElement.prototype, 'value'
  ).set
  nativeSetter.call(textarea, newText)
  textarea.dispatchEvent(new Event('input', { bubbles: true }))
}
</script>

<style scoped>
.markdown-editor {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
}
.editor-toolbar {
  padding: 8px 12px;
  background: #fafafa;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
}
.editor-body {
  min-height: 200px;
}
.editor-textarea :deep(textarea) {
  border: none !important;
  box-shadow: none !important;
  border-radius: 0 !important;
  font-family: 'Fira Code', 'Consolas', monospace;
  font-size: 14px;
  line-height: 1.6;
}
.preview-pane {
  padding: 16px;
  min-height: 200px;
  line-height: 1.8;
  font-size: 15px;
}
.preview-pane :deep(h1) { font-size: 1.8em; margin: 16px 0 12px; }
.preview-pane :deep(h2) { font-size: 1.5em; margin: 14px 0 10px; }
.preview-pane :deep(h3) { font-size: 1.2em; margin: 12px 0 8px; }
.preview-pane :deep(code) { background: #f0f0f0; padding: 2px 6px; border-radius: 3px; }
.preview-pane :deep(pre) { background: #2d3748; color: #e2e8f0; padding: 16px; border-radius: 6px; overflow-x: auto; }
.preview-pane :deep(blockquote) { border-left: 4px solid #409eff; padding-left: 16px; color: #666; margin: 12px 0; }
.preview-pane :deep(img) { max-width: 100%; border-radius: 6px; }
</style>

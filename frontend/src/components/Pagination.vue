<template>
  <div class="pagination-wrapper" v-if="total > 0">
    <el-pagination
      :current-page="currentPage + 1"
      :page-size="pageSize"
      :total="total"
      :page-sizes="[5, 10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
    />
  </div>
</template>

<script setup>
const props = defineProps({
  currentPage: { type: Number, default: 0 },
  pageSize: { type: Number, default: 10 },
  total: { type: Number, default: 0 },
})

const emit = defineEmits(['page-change', 'size-change'])

function handlePageChange(page) {
  emit('page-change', page - 1)
}

function handleSizeChange(size) {
  emit('size-change', size)
}
</script>

<style scoped>
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>

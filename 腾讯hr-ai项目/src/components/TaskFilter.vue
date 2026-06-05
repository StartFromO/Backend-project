<template>
  <div class="filter-bar">
    <t-input
      v-model="localSearch"
      placeholder="搜索事项..."
      clearable
      size="small"
      @change="emitFilters"
    >
      <template #prefix-icon><t-icon name="search" /></template>
    </t-input>
    <div class="filter-selects">
      <t-select
        v-model="localCategory"
        :options="categoryOptions"
        placeholder="分类"
        size="small"
        @change="emitFilters"
      />
      <t-select
        v-model="localPriority"
        :options="priorityOptions"
        placeholder="优先级"
        size="small"
        @change="emitFilters"
      />
      <t-select
        v-model="localStatus"
        :options="statusOptions"
        placeholder="状态"
        size="small"
        @change="emitFilters"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  category: string
  priority: string
  status: string
  search: string
}>()

const emit = defineEmits<{
  update: [filters: { category: string; priority: string; status: string; search: string }]
}>()

const localCategory = ref(props.category)
const localPriority = ref(props.priority)
const localStatus = ref(props.status)
const localSearch = ref(props.search)

watch(() => props.category, (v) => { localCategory.value = v })
watch(() => props.priority, (v) => { localPriority.value = v })
watch(() => props.status, (v) => { localStatus.value = v })
watch(() => props.search, (v) => { localSearch.value = v })

const categoryOptions = [
  { value: 'all', label: '📂 全部分类' },
  { value: 'work', label: '💼 工作' },
  { value: 'personal', label: '👤 个人' },
  { value: 'health', label: '❤️ 健康' },
  { value: 'study', label: '📚 学习' },
  { value: 'other', label: '📌 其他' },
]

const priorityOptions = [
  { value: 'all', label: '⚡ 全部优先级' },
  { value: 'urgent', label: '🔴 紧急' },
  { value: 'high', label: '🟠 高' },
  { value: 'medium', label: '🔵 中' },
  { value: 'low', label: '⚪ 低' },
]

const statusOptions = [
  { value: 'all', label: '📋 全部状态' },
  { value: 'active', label: '🔄 进行中' },
  { value: 'completed', label: '✅ 已完成' },
]

function emitFilters() {
  emit('update', {
    category: localCategory.value,
    priority: localPriority.value,
    status: localStatus.value,
    search: localSearch.value,
  })
}
</script>

<style scoped>
.filter-bar {
  padding: 0 20px 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  border-bottom: 1px solid rgba(26, 115, 232, 0.06);
}

.filter-selects {
  display: flex;
  gap: 8px;
}

.filter-selects :deep(.t-select) {
  flex: 1;
}
</style>

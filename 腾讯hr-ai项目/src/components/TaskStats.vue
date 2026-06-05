<template>
  <div class="stats-bar">
    <div class="stats-grid">
      <div class="stat-item" v-for="s in statItems" :key="s.key">
        <span class="stat-num" :style="{ color: s.color }">{{ s.value }}</span>
        <span class="stat-label">{{ s.label }}</span>
      </div>
    </div>
    <t-button theme="primary" block size="large" @click="$emit('add')" class="btn-purple btn-add">
      <template #icon><t-icon name="add" /></template>
      新建重要事项
    </t-button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  stats: { total: number; active: number; completed: number; withReminder: number; urgent: number }
}>()

defineEmits<{
  add: []
}>()

const statItems = computed(() => [
  { key: 'active', value: props.stats.active, label: '待完成', color: '#1a73e8' },
  { key: 'urgent', value: props.stats.urgent, label: '紧急', color: '#ef4444' },
  { key: 'withReminder', value: props.stats.withReminder, label: '已提醒', color: '#7c3aed' },
  { key: 'completed', value: props.stats.completed, label: '已完成', color: '#10b981' },
])
</script>

<style scoped>
.stats-bar {
  padding: 16px 20px;
  border-bottom: 1px solid rgba(26, 115, 232, 0.06);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 4px;
  background: rgba(26, 115, 232, 0.03);
  border-radius: 10px;
  border: 1px solid rgba(26, 115, 232, 0.04);
}

.stat-num {
  font-size: 20px;
  font-weight: 800;
  line-height: 1;
}

.stat-label {
  font-size: 11px;
  color: var(--td-text-color-secondary);
  margin-top: 4px;
}

.btn-add {
  height: 44px !important;
  font-size: 15px !important;
  font-weight: 600 !important;
  border-radius: 12px !important;
}
</style>

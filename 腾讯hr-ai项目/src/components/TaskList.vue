<template>
  <div class="task-list" ref="listRef">
    <!-- 空状态 -->
    <div v-if="tasks.length === 0" class="empty-state">
      <t-icon name="file-unknown" size="48px" style="color: #cbd5e1" />
      <p>暂无事项</p>
      <span>点击上方按钮创建第一个重要事项</span>
    </div>

    <!-- 任务列表 -->
    <div
      v-for="task in tasks"
      :key="task.id"
      class="task-item"
      :class="{ 'task-selected': task.id === selectedId, 'task-completed': task.completed }"
      @click="$emit('select', task)"
    >
      <div class="task-left">
        <div
          class="task-check"
          :class="{ checked: task.completed }"
          @click.stop="$emit('toggle', task)"
        >
          <t-icon v-if="task.completed" name="check" size="12px" />
        </div>
      </div>

      <div class="task-body">
        <div class="task-header">
          <span class="task-icon">{{ getCategoryIcon(task.category) }}</span>
          <span class="task-title">{{ task.title }}</span>
          <t-tag
            :style="{ background: getPriorityBg(task.priority), color: getPriorityColor(task.priority), border: 'none' }"
            size="small"
            class="task-tag"
          >
            {{ getPriorityLabel(task.priority) }}
          </t-tag>
        </div>
        <div v-if="task.description" class="task-desc">
          {{ task.description }}
        </div>
        <div class="task-meta">
          <span v-if="task.reminderTime" class="task-reminder">
            <t-icon name="alarm" size="13px" />
            {{ formatReminder(task.reminderTime) }}
          </span>
          <span class="task-date">{{ formatDate(task.createdAt) }}</span>
        </div>
      </div>

      <div class="task-right">
        <t-button
          size="small"
          variant="text"
          shape="square"
          theme="danger"
          @click.stop="$emit('delete', task)"
        >
          <template #icon><t-icon name="delete" /></template>
        </t-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Task } from '../types/task'
import { getCategoryIcon, getPriorityLabel, getPriorityColor } from '../types/task'

defineProps<{
  tasks: Task[]
  selectedId: string | null
}>()

defineEmits<{
  select: [task: Task]
  toggle: [task: Task]
  delete: [task: Task]
}>()

function getPriorityBg(priority: string): string {
  const map: Record<string, string> = {
    urgent: 'rgba(239,68,68,0.1)',
    high: 'rgba(245,158,11,0.1)',
    medium: 'rgba(26,115,232,0.08)',
    low: 'rgba(148,163,184,0.08)',
  }
  return map[priority] || 'rgba(148,163,184,0.08)'
}

/**
 * 将 "YYYY-MM-DDTHH:mm" 格式的本地时间字符串解析为 Date 对象
 */
function parseLocalTime(iso: string): Date {
  const parts = iso.split(/[T :\-]/)
  return new Date(
    Number(parts[0]),
    Number(parts[1]) - 1,
    Number(parts[2]),
    Number(parts[3]) || 0,
    Number(parts[4]) || 0,
    0, 0
  )
}

function formatReminder(time: string): string {
  const d = parseLocalTime(time)
  const now = new Date()
  const isToday = d.toDateString() === now.toDateString()
  const tomorrow = new Date(now)
  tomorrow.setDate(tomorrow.getDate() + 1)
  const isTomorrow = d.toDateString() === tomorrow.toDateString()

  const timeStr = d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })

  if (isToday) return `今天 ${timeStr}`
  if (isTomorrow) return `明天 ${timeStr}`
  return `${d.getMonth() + 1}/${d.getDate()} ${timeStr}`
}

function formatDate(iso: string): string {
  const d = parseLocalTime(iso)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  const mins = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (mins < 1) return '刚刚'
  if (mins < 60) return `${mins}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return `${d.getMonth() + 1}/${d.getDate()}`
}
</script>

<style scoped>
.task-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 12px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 8px;
}

.empty-state p {
  font-size: 14px;
  color: var(--td-text-color-secondary);
  font-weight: 500;
}

.empty-state span {
  font-size: 12px;
  color: var(--td-text-color-placeholder);
}

.task-item {
  display: flex;
  align-items: flex-start;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-bottom: 4px;
  border: 1.5px solid transparent;
  animation: fade-in 0.3s ease-out;
}

.task-item:hover {
  background: rgba(26, 115, 232, 0.04);
}

.task-selected {
  background: rgba(26, 115, 232, 0.06) !important;
  border-color: rgba(26, 115, 232, 0.15) !important;
}

.task-completed .task-title {
  text-decoration: line-through;
  color: var(--td-text-color-placeholder) !important;
  opacity: 0.7;
}

.task-left {
  padding-top: 2px;
  margin-right: 10px;
}

.task-check {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #cbd5e1;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.task-check.checked {
  background: var(--td-success-color);
  border-color: var(--td-success-color);
  color: white;
}

.task-body {
  flex: 1;
  min-width: 0;
}

.task-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 2px;
}

.task-icon {
  font-size: 14px;
  flex-shrink: 0;
}

.task-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--td-text-color-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.task-tag {
  flex-shrink: 0;
}

.task-desc {
  font-size: 12px;
  color: var(--td-text-color-secondary);
  margin-top: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.task-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: var(--td-text-color-placeholder);
}

.task-reminder {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  color: var(--td-purple);
  font-weight: 500;
}

.task-right {
  opacity: 0;
  transition: opacity 0.2s ease;
  padding-top: 2px;
}

.task-item:hover .task-right {
  opacity: 1;
}
</style>

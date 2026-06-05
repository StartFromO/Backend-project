<template>
  <div class="detail-container animate-slide-up">
    <!-- 头部操作栏 -->
    <div class="detail-header glass-card">
      <div class="detail-actions">
        <t-button size="small" variant="outline" @click="$emit('edit', task)">
          <template #icon><t-icon name="edit" /></template>
          编辑
        </t-button>
        <t-button
          size="small"
          variant="outline"
          :theme="task.completed ? 'default' : 'success'"
          @click="$emit('toggle', task)"
        >
          <template #icon><t-icon :name="task.completed ? 'rollback' : 'check'" /></template>
          {{ task.completed ? '恢复' : '完成' }}
        </t-button>
        <t-button size="small" variant="outline" theme="danger" @click="$emit('delete', task)">
          <template #icon><t-icon name="delete" /></template>
          删除
        </t-button>
      </div>
    </div>

    <!-- 倒计时区域 -->
    <div v-if="task.reminderTime && !task.completed" class="countdown-card glass-card">
      <div class="countdown-icon">
        <div class="countdown-ring" :class="{ 'ring-urgent': isUrgent, 'ring-warning': isSoon }"></div>
        <t-icon name="alarm" size="28px" :style="{ color: isUrgent ? '#ef4444' : '#7c3aed' }" />
      </div>
      <div class="countdown-info">
        <span class="countdown-label">
          {{ isOverdue ? '已超过' : isToday ? '距离提醒还有' : '距离提醒还有' }}
        </span>
        <div class="countdown-timer" :class="{ 'timer-overdue': isOverdue, 'timer-urgent': isUrgent }">
          <span v-if="countdown.days > 0" class="timer-block">
            <em>{{ countdown.days }}</em><small>天</small>
          </span>
          <span class="timer-block">
            <em>{{ pad(countdown.hours) }}</em><small>时</small>
          </span>
          <span class="timer-sep">:</span>
          <span class="timer-block">
            <em>{{ pad(countdown.minutes) }}</em><small>分</small>
          </span>
          <span class="timer-sep">:</span>
          <span class="timer-block">
            <em>{{ pad(countdown.seconds) }}</em><small>秒</small>
          </span>
        </div>
        <span class="countdown-target">
          提醒时间：{{ formatFullTime(task.reminderTime) }}
        </span>
      </div>
    </div>

    <!-- 无提醒时的卡片 -->
    <div v-else-if="!task.completed" class="no-reminder-card glass-card">
      <t-icon name="alarm-off" size="32px" style="color: #94a3b8" />
      <span>该事项未设置提醒时间</span>
      <t-button size="small" variant="text" theme="primary" @click="$emit('edit', task)">
        点击设置提醒
      </t-button>
    </div>

    <!-- 已完成状态 -->
    <div v-else class="completed-card glass-card">
      <div class="completed-icon">✅</div>
      <span class="completed-text">该事项已完成</span>
      <span class="completed-time">{{ formatFullTime(task.createdAt) }} 创建</span>
    </div>

    <!-- 事项信息卡片 -->
    <div class="info-card glass-card">
      <div class="info-row">
        <span class="info-label">标题</span>
        <span class="info-value title-value">{{ task.title }}</span>
      </div>
      <div v-if="task.description" class="info-row">
        <span class="info-label">描述</span>
        <span class="info-value">{{ task.description }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">分类</span>
        <t-tag variant="light" size="small">
          {{ getCategoryIcon(task.category) }} {{ getCategoryLabel(task.category) }}
        </t-tag>
      </div>
      <div class="info-row">
        <span class="info-label">优先级</span>
        <t-tag
          :style="{
            background: getPriorityBg(task.priority),
            color: getPriorityColor(task.priority),
            border: 'none',
          }"
          size="small"
        >
          {{ getPriorityLabel(task.priority) }}
        </t-tag>
      </div>
      <div class="info-row">
        <span class="info-label">创建时间</span>
        <span class="info-value">{{ formatFullTime(task.createdAt) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onUnmounted } from 'vue'
import type { Task } from '../types/task'
import { getCategoryLabel, getCategoryIcon, getPriorityLabel, getPriorityColor } from '../types/task'

const props = defineProps<{
  task: Task
}>()

defineEmits<{
  edit: [task: Task]
  toggle: [task: Task]
  delete: [task: Task]
}>()

const now = ref(Date.now())
let timer: ReturnType<typeof setInterval>

function startTimer() {
  timer = setInterval(() => {
    now.value = Date.now()
  }, 1000)
}

watch(() => props.task.id, () => {
  clearInterval(timer)
  startTimer()
}, { immediate: true })

onUnmounted(() => {
  clearInterval(timer)
})

/**
 * 将 "YYYY-MM-DDTHH:mm" 格式的本地时间字符串解析为 Date 对象
 * 避免浏览器将其当作 UTC 时间处理
 */
function parseLocalTime(iso: string): Date {
  const parts = iso.split(/[T :\-]/)
  return new Date(
    Number(parts[0]),
    Number(parts[1]) - 1,
    Number(parts[2]),
    Number(parts[3]),
    Number(parts[4]),
    0, 0
  )
}

const countdown = computed(() => {
  if (!props.task.reminderTime) return { days: 0, hours: 0, minutes: 0, seconds: 0, total: 0 }
  const diff = parseLocalTime(props.task.reminderTime).getTime() - now.value
  const absDiff = Math.abs(diff)
  const days = Math.floor(absDiff / 86400000)
  const hours = Math.floor((absDiff % 86400000) / 3600000)
  const minutes = Math.floor((absDiff % 3600000) / 60000)
  const seconds = Math.floor((absDiff % 60000) / 1000)
  return { days, hours, minutes, seconds, total: diff }
})

const isOverdue = computed(() => countdown.value.total < -60000)
const isUrgent = computed(() => countdown.value.total > 0 && countdown.value.total < 3600000)
const isSoon = computed(() => countdown.value.total > 0 && countdown.value.total < 86400000)

const isToday = computed(() => {
  if (!props.task.reminderTime) return false
  return parseLocalTime(props.task.reminderTime).toDateString() === new Date().toDateString()
})

function pad(n: number): string {
  return n.toString().padStart(2, '0')
}

function formatFullTime(iso: string): string {
  const d = parseLocalTime(iso)
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function getPriorityBg(priority: string): string {
  const map: Record<string, string> = {
    urgent: 'rgba(239,68,68,0.1)',
    high: 'rgba(245,158,11,0.1)',
    medium: 'rgba(26,115,232,0.08)',
    low: 'rgba(148,163,184,0.08)',
  }
  return map[priority] || 'rgba(148,163,184,0.08)'
}
</script>

<style scoped>
.detail-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  animation: slide-up 0.4s ease-out;
}

/* ===== 头部 ===== */
.detail-header {
  border-radius: var(--td-radius-large);
  padding: 16px 20px;
}

.detail-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

/* ===== 倒计时卡片 ===== */
.countdown-card {
  border-radius: var(--td-radius-large);
  padding: 32px 28px;
  display: flex;
  align-items: center;
  gap: 24px;
  position: relative;
  overflow: hidden;
}

.countdown-card::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.04), rgba(26, 115, 232, 0.04));
  border-radius: inherit;
}

.countdown-icon {
  position: relative;
  width: 72px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.countdown-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px solid rgba(124, 58, 237, 0.2);
  animation: pulse-ring 3s ease-in-out infinite;
}

.ring-urgent {
  border-color: rgba(239, 68, 68, 0.4);
  animation: pulse-ring 1s ease-in-out infinite;
}

.ring-warning {
  border-color: rgba(245, 158, 11, 0.3);
  animation: pulse-ring 2s ease-in-out infinite;
}

.countdown-info {
  flex: 1;
  min-width: 0;
  position: relative;
  z-index: 1;
}

.countdown-label {
  font-size: 13px;
  color: var(--td-text-color-secondary);
  display: block;
  margin-bottom: 8px;
}

.countdown-timer {
  display: flex;
  align-items: center;
  gap: 2px;
}

.timer-block {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  min-width: 44px;
}

.timer-block em {
  font-size: 36px;
  font-weight: 800;
  font-style: normal;
  font-variant-numeric: tabular-nums;
  background: linear-gradient(135deg, #1a73e8, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1.1;
}

.timer-block small {
  font-size: 11px;
  color: var(--td-text-color-placeholder);
  margin-top: 2px;
}

.timer-sep {
  font-size: 28px;
  font-weight: 700;
  color: var(--td-text-color-placeholder);
  margin: 0 4px;
  margin-top: -12px;
}

.timer-overdue .timer-block em {
  background: linear-gradient(135deg, #ef4444, #f87171);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.timer-urgent .timer-block em {
  background: linear-gradient(135deg, #f59e0b, #ef4444);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: glow-pulse 1.5s ease-in-out infinite;
}

.countdown-target {
  display: block;
  margin-top: 10px;
  font-size: 12px;
  color: var(--td-text-color-placeholder);
}

/* ===== 无提醒、已完成卡片 ===== */
.no-reminder-card,
.completed-card {
  border-radius: var(--td-radius-large);
  padding: 40px 28px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.no-reminder-card span,
.completed-text {
  font-size: 14px;
  color: var(--td-text-color-secondary);
}

.completed-icon {
  font-size: 40px;
}

.completed-time {
  font-size: 12px;
  color: var(--td-text-color-placeholder);
}

/* ===== 信息卡片 ===== */
.info-card {
  border-radius: var(--td-radius-large);
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-label {
  width: 64px;
  flex-shrink: 0;
  font-size: 13px;
  color: var(--td-text-color-secondary);
  font-weight: 500;
}

.info-value {
  font-size: 14px;
  color: var(--td-text-color-primary);
  flex: 1;
}

.title-value {
  font-weight: 700;
  font-size: 16px;
}
</style>

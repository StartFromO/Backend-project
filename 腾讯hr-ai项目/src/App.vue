<template>
  <div class="app-container">
    <!-- 科技感背景 -->
    <div class="tech-bg"></div>

    <!-- 顶部导航 -->
    <header class="app-header">
      <div class="header-left">
        <div class="logo-icon">
          <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
            <circle cx="14" cy="14" r="12" stroke="url(#logo-grad)" stroke-width="2.5" fill="none"/>
            <circle cx="14" cy="14" r="4" fill="url(#logo-grad)"/>
            <path d="M14 2v5M14 21v5M2 14h5M21 14h5" stroke="url(#logo-grad)" stroke-width="1.5" stroke-linecap="round"/>
            <defs>
              <linearGradient id="logo-grad" x1="0" y1="0" x2="28" y2="28">
                <stop offset="0%" stop-color="#1a73e8"/>
                <stop offset="100%" stop-color="#7c3aed"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1 class="app-title">ChronoTask</h1>
        <span class="app-subtitle">重要事项管理器</span>
      </div>
      <div class="header-right">
        <div class="time-display">
          <span class="time-text">{{ currentTime }}</span>
          <span class="date-text">{{ currentDate }}</span>
        </div>
        <t-button
          v-if="!reminder.notificationGranted.value"
          size="small"
          variant="outline"
          theme="primary"
          @click="handleEnableNotification"
          class="btn-notify"
        >
          <template #icon><t-icon name="notification" /></template>
          开启提醒
        </t-button>
        <t-tag
          v-else
          theme="success"
          variant="light"
          size="small"
        >
          <template #icon><t-icon name="check-circle" /></template>
          提醒已开启
        </t-tag>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="app-main">
      <!-- 左侧面板 -->
      <aside class="panel-left glass-card">
        <TaskStats
          :stats="stats"
          @add="handleOpenAdd"
        />
        <TaskFilter
          :category="state.filterCategory"
          :priority="state.filterPriority"
          :status="state.filterStatus"
          :search="state.searchQuery"
          @update="handleFilterUpdate"
        />
        <TaskList
          :tasks="filteredTasks"
          :selected-id="selectedTaskId"
          @select="handleSelectTask"
          @toggle="handleToggleComplete"
          @delete="handleDeleteTask"
        />
      </aside>

      <!-- 右侧面板 -->
      <section class="panel-right">
        <!-- 没有选中任务时的欢迎页 -->
        <div v-if="!selectedTask" class="welcome glass-card">
          <div class="welcome-icon">
            <div class="welcome-ring"></div>
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
              <circle cx="32" cy="32" r="28" stroke="url(#welcome-grad)" stroke-width="2" stroke-dasharray="8 4"/>
              <circle cx="32" cy="32" r="8" fill="url(#welcome-grad)" opacity="0.8"/>
              <defs>
                <linearGradient id="welcome-grad" x1="0" y1="0" x2="64" y2="64">
                  <stop offset="0%" stop-color="#1a73e8"/>
                  <stop offset="100%" stop-color="#7c3aed"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h2 class="welcome-title">ChronoTask</h2>
          <p class="welcome-desc">选择左侧事项查看详情<br>或创建一个新的重要事项</p>
          <t-button theme="primary" size="large" @click="handleOpenAdd" class="btn-purple">
            <template #icon><t-icon name="add" /></template>
            新建事项
          </t-button>
          <div class="welcome-stats">
            <div class="welcome-stat-item">
              <span class="welcome-stat-num">{{ stats.active }}</span>
              <span class="welcome-stat-label">待完成</span>
            </div>
            <div class="welcome-stat-divider"></div>
            <div class="welcome-stat-item">
              <span class="welcome-stat-num">{{ stats.withReminder }}</span>
              <span class="welcome-stat-label">已设提醒</span>
            </div>
            <div class="welcome-stat-divider"></div>
            <div class="welcome-stat-item">
              <span class="welcome-stat-num">{{ stats.urgent }}</span>
              <span class="welcome-stat-label">紧急事项</span>
            </div>
          </div>
        </div>

        <!-- 任务详情 -->
        <TaskDetail
          v-else
          :task="selectedTask"
          :key="selectedTask.id"
          @edit="handleOpenEdit"
          @toggle="handleToggleComplete"
          @delete="handleDeleteTask"
        />
      </section>
    </main>

    <!-- 添加/编辑对话框 -->
    <AddTaskDialog
      v-model:visible="dialogVisible"
      :edit-task="editingTask"
      @save="handleSave"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { DialogPlugin } from 'tdesign-vue-next'
import type { Task, TaskFormData } from './types/task'
import { useTaskStore } from './stores/taskStore'
import { useReminder } from './composables/useReminder'
import TaskStats from './components/TaskStats.vue'
import TaskFilter from './components/TaskFilter.vue'
import TaskList from './components/TaskList.vue'
import TaskDetail from './components/TaskDetail.vue'
import AddTaskDialog from './components/AddTaskDialog.vue'

const { state, filteredTasks, stats, addTask, updateTask, deleteTask, toggleComplete, setFilter } = useTaskStore()
const reminder = useReminder()

const selectedTaskId = ref<string | null>(null)
const dialogVisible = ref(false)
const editingTask = ref<Task | null>(null)

const selectedTask = computed(() => {
  if (!selectedTaskId.value) return null
  return state.tasks.find(t => t.id === selectedTaskId.value) || null
})

// 当前时间
const currentTime = ref('')
const currentDate = ref('')
let timeTimer: ReturnType<typeof setInterval>

function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  currentDate.value = now.toLocaleDateString('zh-CN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })
}

function handleOpenAdd() {
  editingTask.value = null
  dialogVisible.value = true
}

function handleOpenEdit(task: Task) {
  editingTask.value = task
  dialogVisible.value = true
}

function handleSave(form: TaskFormData) {
  if (editingTask.value) {
    updateTask(editingTask.value.id, {
      title: form.title,
      description: form.description,
      category: form.category,
      priority: form.priority,
      reminderTime: form.reminderTime || null,
      notified: false,
    })
  } else {
    const newTask = addTask(form)
    selectedTaskId.value = newTask.id
  }
  dialogVisible.value = false
  editingTask.value = null
}

function handleSelectTask(task: Task) {
  selectedTaskId.value = task.id
}

function handleToggleComplete(task: Task) {
  toggleComplete(task.id)
}

function handleDeleteTask(task: Task) {
  const dialog = DialogPlugin.confirm({
    header: '确认删除',
    body: `确定要删除事项「${task.title}」吗？此操作不可恢复。`,
    confirmBtn: { content: '删除', theme: 'danger' },
    cancelBtn: { content: '取消' },
    onConfirm: () => {
      deleteTask(task.id)
      if (selectedTaskId.value === task.id) {
        selectedTaskId.value = null
      }
      dialog.destroy()
    },
    onClose: () => {
      dialog.destroy()
    },
  })
}

function handleFilterUpdate(filters: { category: string; priority: string; status: string; search: string }) {
  setFilter(filters.category, filters.priority, filters.status, filters.search)
}

async function handleEnableNotification() {
  await reminder.enable()
}

onMounted(() => {
  updateTime()
  timeTimer = setInterval(updateTime, 1000)
  reminder.init()
})

onUnmounted(() => {
  clearInterval(timeTimer)
})
</script>

<style scoped>
.app-container {
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
  z-index: 1;
}

/* ===== 顶部导航 ===== */
.app-header {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 28px;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(26, 115, 232, 0.08);
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  display: flex;
  align-items: center;
}

.app-title {
  font-size: 20px;
  font-weight: 800;
  background: linear-gradient(135deg, #1a73e8, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.app-subtitle {
  font-size: 12px;
  color: var(--td-text-color-secondary);
  padding: 2px 8px;
  background: rgba(26, 115, 232, 0.06);
  border-radius: 12px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.time-display {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.time-text {
  font-size: 18px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  color: var(--td-text-color-primary);
  letter-spacing: 1px;
}

.date-text {
  font-size: 11px;
  color: var(--td-text-color-secondary);
}

.btn-notify {
  border-color: var(--td-purple) !important;
  color: var(--td-purple) !important;
}

.btn-notify:hover {
  background: rgba(124, 58, 237, 0.08) !important;
}

/* ===== 主体区域 ===== */
.app-main {
  flex: 1;
  display: flex;
  gap: 20px;
  padding: 20px 28px;
  overflow: hidden;
  min-height: 0;
}

/* ===== 左侧面板 ===== */
.panel-left {
  width: 400px;
  flex-shrink: 0;
  border-radius: var(--td-radius-large);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ===== 右侧面板 ===== */
.panel-right {
  flex: 1;
  min-width: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

/* ===== 欢迎页 ===== */
.welcome {
  border-radius: var(--td-radius-large);
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  min-height: 100%;
}

.welcome-icon {
  position: relative;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.welcome-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 1.5px solid rgba(124, 58, 237, 0.15);
  animation: pulse-ring 3s ease-in-out infinite;
}

.welcome-title {
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #1a73e8, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-desc {
  color: var(--td-text-color-secondary);
  font-size: 14px;
  line-height: 1.8;
  margin-bottom: 32px;
}

.welcome-stats {
  display: flex;
  align-items: center;
  gap: 0;
  margin-top: 48px;
  padding: 16px 32px;
  background: rgba(26, 115, 232, 0.03);
  border-radius: 16px;
  border: 1px solid rgba(26, 115, 232, 0.06);
}

.welcome-stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 24px;
}

.welcome-stat-num {
  font-size: 24px;
  font-weight: 800;
  background: linear-gradient(135deg, #1a73e8, #4d8ae4);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-stat-label {
  font-size: 12px;
  color: var(--td-text-color-secondary);
  margin-top: 4px;
}

.welcome-stat-divider {
  width: 1px;
  height: 32px;
  background: rgba(26, 115, 232, 0.1);
}
</style>

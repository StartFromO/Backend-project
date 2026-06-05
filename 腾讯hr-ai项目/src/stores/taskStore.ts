import { reactive, computed } from 'vue'
import type { Task, TaskFormData } from '../types/task'
import { generateId } from '../types/task'

const STORAGE_KEY = 'chronotask-tasks'

/**
 * 将 Date 对象格式化为 YYYY-MM-DDTHH:mm 本地时间字符串
 */
function toLocalDatetimeString(date: Date): string {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day}T${hours}:${minutes}`
}

function loadTasks(): Task[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

const state = reactive<{
  tasks: Task[]
  filterCategory: string
  filterPriority: string
  filterStatus: string
  searchQuery: string
}>({
  tasks: loadTasks(),
  filterCategory: 'all',
  filterPriority: 'all',
  filterStatus: 'all',
  searchQuery: '',
})

function saveTasks() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state.tasks))
}

// 过滤后的任务列表
const filteredTasks = computed(() => {
  let result = [...state.tasks]

  if (state.filterCategory !== 'all') {
    result = result.filter(t => t.category === state.filterCategory)
  }
  if (state.filterPriority !== 'all') {
    result = result.filter(t => t.priority === state.filterPriority)
  }
  if (state.filterStatus === 'active') {
    result = result.filter(t => !t.completed)
  } else if (state.filterStatus === 'completed') {
    result = result.filter(t => t.completed)
  }
  if (state.searchQuery.trim()) {
    const q = state.searchQuery.trim().toLowerCase()
    result = result.filter(
      t => t.title.toLowerCase().includes(q) || t.description.toLowerCase().includes(q)
    )
  }

  // 排序：未完成在前，按创建时间倒序
  result.sort((a, b) => {
    if (a.completed !== b.completed) return a.completed ? 1 : -1
    return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
  })

  return result
})

// 统计
const stats = computed(() => {
  const total = state.tasks.length
  const active = state.tasks.filter(t => !t.completed).length
  const completed = state.tasks.filter(t => t.completed).length
  const withReminder = state.tasks.filter(t => t.reminderTime && !t.completed).length
  const urgent = state.tasks.filter(t => t.priority === 'urgent' && !t.completed).length
  return { total, active, completed, withReminder, urgent }
})

function addTask(form: TaskFormData) {
  const task: Task = {
    id: generateId(),
    title: form.title,
    description: form.description,
    category: form.category,
    priority: form.priority,
    reminderTime: form.reminderTime || null,
    completed: false,
    createdAt: toLocalDatetimeString(new Date()),
    notified: false,
  }
  state.tasks.unshift(task)
  saveTasks()
  return task
}

function updateTask(id: string, updates: Partial<Task>) {
  const idx = state.tasks.findIndex(t => t.id === id)
  if (idx !== -1) {
    state.tasks[idx] = { ...state.tasks[idx], ...updates }
    saveTasks()
  }
}

function deleteTask(id: string) {
  state.tasks = state.tasks.filter(t => t.id !== id)
  saveTasks()
}

function toggleComplete(id: string) {
  const task = state.tasks.find(t => t.id === id)
  if (task) {
    task.completed = !task.completed
    saveTasks()
  }
}

function markNotified(id: string) {
  const task = state.tasks.find(t => t.id === id)
  if (task) {
    task.notified = true
    saveTasks()
  }
}

function setFilter(category: string, priority: string, status: string, search: string) {
  state.filterCategory = category
  state.filterPriority = priority
  state.filterStatus = status
  state.searchQuery = search
}

export function useTaskStore() {
  return {
    state,
    filteredTasks,
    stats,
    addTask,
    updateTask,
    deleteTask,
    toggleComplete,
    markNotified,
    setFilter,
  }
}

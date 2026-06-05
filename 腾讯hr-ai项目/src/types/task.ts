export interface Task {
  id: string
  title: string
  description: string
  category: TaskCategory
  priority: TaskPriority
  reminderTime: string | null // ISO datetime string
  completed: boolean
  createdAt: string
  notified: boolean
}

export type TaskCategory = 'work' | 'personal' | 'health' | 'study' | 'other'

export type TaskPriority = 'low' | 'medium' | 'high' | 'urgent'

export interface TaskFormData {
  title: string
  description: string
  category: TaskCategory
  priority: TaskPriority
  reminderTime: string
}

export const CATEGORY_OPTIONS: { value: TaskCategory; label: string; icon: string }[] = [
  { value: 'work', label: '工作', icon: '💼' },
  { value: 'personal', label: '个人', icon: '👤' },
  { value: 'health', label: '健康', icon: '❤️' },
  { value: 'study', label: '学习', icon: '📚' },
  { value: 'other', label: '其他', icon: '📌' },
]

export const PRIORITY_OPTIONS: { value: TaskPriority; label: string; color: string }[] = [
  { value: 'low', label: '低', color: '#94a3b8' },
  { value: 'medium', label: '中', color: '#1a73e8' },
  { value: 'high', label: '高', color: '#f59e0b' },
  { value: 'urgent', label: '紧急', color: '#ef4444' },
]

export function getCategoryLabel(cat: TaskCategory): string {
  return CATEGORY_OPTIONS.find(c => c.value === cat)?.label || cat
}

export function getCategoryIcon(cat: TaskCategory): string {
  return CATEGORY_OPTIONS.find(c => c.value === cat)?.icon || '📌'
}

export function getPriorityLabel(pri: TaskPriority): string {
  return PRIORITY_OPTIONS.find(p => p.value === pri)?.label || pri
}

export function getPriorityColor(pri: TaskPriority): string {
  return PRIORITY_OPTIONS.find(p => p.value === pri)?.color || '#94a3b8'
}

export function generateId(): string {
  return Date.now().toString(36) + Math.random().toString(36).substring(2, 9)
}

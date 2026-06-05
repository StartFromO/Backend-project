<template>
  <t-dialog
    :visible="visible"
    :header="editTask ? '✏️ 编辑事项' : '🆕 新建重要事项'"
    width="520px"
    :close-on-overlay-click="false"
    @close="$emit('update:visible', false)"
    :footer="false"
    class="task-dialog"
  >
    <t-form
      ref="formRef"
      :data="form"
      :rules="rules"
      label-width="70px"
      @submit="handleSubmit"
    >
      <t-form-item label="标题" name="title">
        <t-input
          v-model="form.title"
          placeholder="请输入事项标题"
          :maxlength="50"
          clearable
          size="large"
        />
      </t-form-item>

      <t-form-item label="描述" name="description">
        <t-textarea
          v-model="form.description"
          placeholder="详细描述（选填）"
          :maxlength="200"
          :autosize="{ minRows: 2, maxRows: 4 }"
        />
      </t-form-item>

      <t-form-item label="分类" name="category">
        <t-select v-model="form.category" :options="categoryOptions" />
      </t-form-item>

      <t-form-item label="优先级" name="priority">
        <t-radio-group v-model="form.priority">
          <t-radio-button
            v-for="p in priorityOptions"
            :key="p.value"
            :value="p.value"
            :style="{
              '--radio-checked-bg': p.color,
              '--radio-checked-border': p.color,
            }"
          >
            {{ p.label }}
          </t-radio-button>
        </t-radio-group>
      </t-form-item>

      <t-form-item label="提醒时间" name="reminderTime">
        <t-date-picker
          v-model="form.reminderTime"
          enable-time-picker
          placeholder="选择提醒时间（可选）"
          clearable
          format="YYYY-MM-DD HH:mm"
          value-type="YYYY-MM-DD HH:mm"
          style="width: 100%"
        />
      </t-form-item>

      <!-- 快捷时间按钮 -->
      <div class="quick-times">
        <t-button
          v-for="qt in quickTimeOptions"
          :key="qt.label"
          size="small"
          variant="outline"
          @click="setQuickTime(qt.minutes)"
        >
          {{ qt.label }}
        </t-button>
        <t-button size="small" variant="text" theme="default" @click="form.reminderTime = ''">
          清除
        </t-button>
      </div>

      <div class="form-footer">
        <t-button variant="outline" @click="$emit('update:visible', false)">取消</t-button>
        <t-button theme="primary" type="submit" class="btn-purple">
          {{ editTask ? '保存修改' : '创建事项' }}
        </t-button>
      </div>
    </t-form>
  </t-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { Task, TaskCategory, TaskPriority } from '../types/task'
import { CATEGORY_OPTIONS, PRIORITY_OPTIONS } from '../types/task'

const props = defineProps<{
  visible: boolean
  editTask: Task | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  save: [form: { title: string; description: string; category: TaskCategory; priority: TaskPriority; reminderTime: string }]
}>()

const form = ref({
  title: '',
  description: '',
  category: 'work' as TaskCategory,
  priority: 'medium' as TaskPriority,
  reminderTime: '',
})

const rules = {
  title: [{ required: true, message: '请输入事项标题', trigger: 'blur' }],
}

const categoryOptions = CATEGORY_OPTIONS.map(c => ({
  value: c.value,
  label: `${c.icon} ${c.label}`,
}))

const priorityOptions = PRIORITY_OPTIONS

const quickTimeOptions = [
  { label: '15分钟后', minutes: 15 },
  { label: '30分钟后', minutes: 30 },
  { label: '1小时后', minutes: 60 },
  { label: '明天 9:00', minutes: 'tomorrow9' as any },
  { label: '明天 14:00', minutes: 'tomorrow14' as any },
]

watch(() => props.visible, (val) => {
  if (val) {
    if (props.editTask) {
      form.value = {
        title: props.editTask.title,
        description: props.editTask.description,
        category: props.editTask.category,
        priority: props.editTask.priority,
        reminderTime: props.editTask.reminderTime || '',
      }
    } else {
      form.value = {
        title: '',
        description: '',
        category: 'work',
        priority: 'medium',
        reminderTime: '',
      }
    }
  }
})

/**
 * 将本地时间 Date 对象格式化为 YYYY-MM-DDTHH:mm 字符串（本地时区）
 */
function toLocalDatetimeString(date: Date): string {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day}T${hours}:${minutes}`
}

function setQuickTime(minutesOrKey: number | string) {
  const now = new Date()
  if (minutesOrKey === 'tomorrow9') {
    now.setDate(now.getDate() + 1)
    now.setHours(9, 0, 0, 0)
  } else if (minutesOrKey === 'tomorrow14') {
    now.setDate(now.getDate() + 1)
    now.setHours(14, 0, 0, 0)
  } else {
    now.setMinutes(now.getMinutes() + (minutesOrKey as number))
  }
  form.value.reminderTime = toLocalDatetimeString(now)
}

function handleSubmit() {
  // 统一提醒时间格式：将 "YYYY-MM-DD HH:mm" 转为 "YYYY-MM-DDTHH:mm"
  const data = { ...form.value }
  if (data.reminderTime) {
    // TDesign DatePicker 返回格式可能是 "YYYY-MM-DD HH:mm"，统一为 T 分隔
    data.reminderTime = data.reminderTime.replace(' ', 'T')
  }
  emit('save', data)
}
</script>

<style scoped>
.quick-times {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
  padding-left: 70px;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 4px;
}
</style>

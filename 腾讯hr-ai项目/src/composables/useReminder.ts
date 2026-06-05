import { ref, onUnmounted } from 'vue'
import { useTaskStore } from '../stores/taskStore'
import { NotifyPlugin } from 'tdesign-vue-next'

let checkTimer: ReturnType<typeof setInterval> | null = null
const notifiedSet = new Set<string>()

async function requestNotificationPermission(): Promise<boolean> {
  if (!('Notification' in window)) return false
  if (Notification.permission === 'granted') return true
  if (Notification.permission === 'denied') return false
  const perm = await Notification.requestPermission()
  return perm === 'granted'
}

function sendBrowserNotification(title: string, body: string) {
  if (Notification.permission === 'granted') {
    new Notification(title, {
      body,
      icon: '/vite.svg',
      tag: 'chronotask-reminder',
    })
  }
}

function startReminderCheck() {
  if (checkTimer) return

  checkTimer = setInterval(() => {
    const { state, markNotified, toggleComplete } = useTaskStore()
    const now = new Date()

    state.tasks.forEach(task => {
      if (task.completed || !task.reminderTime || notifiedSet.has(task.id)) return

      // reminderTime 格式为 "YYYY-MM-DDTHH:mm"（本地时间，无时区后缀）
      // 用 "T" 替换首个 "T" 以保证正确解析为本地时间
      // 部分浏览器将 "YYYY-MM-DDTHH:mm" 当作 UTC，这里显式拼接确保本地时区解析
      const parts = task.reminderTime.split(/[T :\-]/)
      const reminderDate = new Date(
        Number(parts[0]),       // year
        Number(parts[1]) - 1,   // month (0-based)
        Number(parts[2]),       // day
        Number(parts[3]),       // hours
        Number(parts[4]),       // minutes
        0, 0                    // seconds, ms
      )
      const diff = reminderDate.getTime() - now.getTime()

      // 在提醒时间前后1分钟内触发
      if (Math.abs(diff) <= 60000) {
        notifiedSet.add(task.id)
        markNotified(task.id)

        // 浏览器通知
        sendBrowserNotification(
          `⏰ 提醒：${task.title}`,
          task.description || '您设置的重要事项时间到了！'
        )

        // TDesign 通知
        NotifyPlugin.info({
          title: '⏰ 事项提醒',
          content: task.title,
          duration: 8000,
          placement: 'top-right',
          closeBtn: true,
        })
      }
    })
  }, 15000) // 每15秒检查一次
}

function stopReminderCheck() {
  if (checkTimer) {
    clearInterval(checkTimer)
    checkTimer = null
  }
}

export function useReminder() {
  const notificationSupported = ref('Notification' in window)
  const notificationGranted = ref(Notification.permission === 'granted')

  async function enable() {
    const granted = await requestNotificationPermission()
    notificationGranted.value = granted
    if (granted) {
      startReminderCheck()
    }
    return granted
  }

  function init() {
    if (Notification.permission === 'granted') {
      notificationGranted.value = true
      startReminderCheck()
    }
  }

  onUnmounted(() => {
    // 不在这里停止，因为这是全局的
  })

  return {
    notificationSupported,
    notificationGranted,
    enable,
    init,
    startReminderCheck,
    stopReminderCheck,
  }
}

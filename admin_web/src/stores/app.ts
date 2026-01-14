import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 侧边栏折叠状态
  const sidebarCollapsed = ref(false)

  // 深色模式
  const isDark = ref(localStorage.getItem('theme') === 'dark')

  // 切换侧边栏
  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  // 切换主题
  function toggleTheme() {
    isDark.value = !isDark.value
    localStorage.setItem('theme', isDark.value ? 'dark' : 'light')

    if (isDark.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  // 初始化主题
  function initTheme() {
    if (isDark.value) {
      document.documentElement.classList.add('dark')
    }
  }

  return {
    sidebarCollapsed,
    isDark,
    toggleSidebar,
    toggleTheme,
    initTheme
  }
})
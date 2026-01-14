import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getAdminInfo, logout } from '@/api/auth'
import type { LoginForm, AdminInfo } from '@/types'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('admin_token') || '')
  const userInfo = ref<AdminInfo | null>(null)

  // 登录
  async function doLogin(loginForm: LoginForm) {
    try {
      const res = await login(loginForm)
      if (res.code === 200 || res.code === 0) {
        token.value = res.data.token
        userInfo.value = res.data.userInfo
        localStorage.setItem('admin_token', res.data.token)
        localStorage.setItem('admin_info', JSON.stringify(res.data.userInfo))

        if (loginForm.remember) {
          localStorage.setItem('admin_username', loginForm.username)
        } else {
          localStorage.removeItem('admin_username')
        }

        return true
      }
      return false
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }

  // 获取用户信息
  async function getUserInfo() {
    try {
      const res = await getAdminInfo()
      if (res.code === 200 || res.code === 0) {
        userInfo.value = res.data
        localStorage.setItem('admin_info', JSON.stringify(res.data))
        return true
      }
      return false
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return false
    }
  }

  // 退出登录
  async function doLogout() {
    try {
      await logout()
    } catch (error) {
      console.error('退出登录失败:', error)
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_info')
      ElMessage.success('退出登录成功')
    }
  }

  // 初始化用户信息
  function initUserInfo() {
    const savedInfo = localStorage.getItem('admin_info')
    if (savedInfo) {
      try {
        userInfo.value = JSON.parse(savedInfo)
      } catch (error) {
        console.error('解析用户信息失败:', error)
      }
    }
  }

  return {
    token,
    userInfo,
    doLogin,
    getUserInfo,
    doLogout,
    initUserInfo
  }
})
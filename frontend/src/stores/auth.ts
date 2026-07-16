import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { login as loginApi } from '@/api/auth'
import { clearAuth, loadAuth, saveAuth } from '@/utils/storage'
import type { LoginRequest, LoginResult } from '@/types/api'

export const useAuthStore = defineStore('auth', () => {
  const auth = ref<LoginResult | null>(loadAuth())
  const token = computed(() => auth.value?.token || '')
  const username = computed(() => auth.value?.username || '')
  const roles = computed(() => auth.value?.roles || [])
  const functions = computed(() => auth.value?.functions || [])
  const isAdmin = computed(() => roles.value.includes('ADMIN'))

  async function login(request: LoginRequest) {
    const result = await loginApi(request)
    auth.value = result
    saveAuth(result)
  }

  function logout() {
    auth.value = null
    clearAuth()
  }

  function hasFunction(code?: string) {
    return !code || functions.value.includes(code)
  }

  return { auth, token, username, roles, functions, isAdmin, login, logout, hasFunction }
})

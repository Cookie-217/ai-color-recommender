import { defineStore } from 'pinia'
import { api } from '@/api/http'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('colorai_token') || '')
  const user = ref(JSON.parse(localStorage.getItem('colorai_user') || 'null'))

  function setSession(t, u) {
    token.value = t
    user.value = u
    localStorage.setItem('colorai_token', t)
    localStorage.setItem('colorai_user', JSON.stringify(u))
  }

  function clear() {
    token.value = ''
    user.value = null
    localStorage.removeItem('colorai_token')
    localStorage.removeItem('colorai_user')
  }

  async function login(username, password) {
    const { data } = await api.post('/api/auth/login', { username, password })
    setSession(data.data.token, {
      username: data.data.username,
      email: data.data.email,
      displayName: data.data.displayName
    })
    return data.data
  }

  async function register(payload) {
    await api.post('/api/auth/register', payload)
  }

  function logout() { clear() }

  return { token, user, login, register, logout, setSession, clear }
})

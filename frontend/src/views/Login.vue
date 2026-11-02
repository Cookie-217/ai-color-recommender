<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await auth.login(username.value.trim(), password.value)
    const redirect = route.query.redirect || '/gallery'
    router.push(redirect)
  } catch (e) {
    error.value = e?.response?.data?.message || '登录失败，请检查用户名或密码'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="mx-auto max-w-md py-10">
    <div class="card p-6 sm:p-8">
      <h1 class="text-2xl font-extrabold">欢迎回来 👋</h1>
      <p class="mt-1 text-sm text-slate-500 dark:text-slate-400">登录以保存和管理你的配色方案</p>
      <form class="mt-6 space-y-4" @submit.prevent="submit">
        <div>
          <label class="label">用户名</label>
          <input v-model="username" class="input" placeholder="demo" required />
        </div>
        <div>
          <label class="label">密码</label>
          <input v-model="password" type="password" class="input" placeholder="••••••" required />
        </div>
        <p v-if="error" class="text-sm text-rose-500">{{ error }}</p>
        <button class="btn-primary w-full" :disabled="loading">{{ loading ? '登录中…' : '登录' }}</button>
      </form>
      <p class="mt-4 text-center text-sm text-slate-500 dark:text-slate-400">
        还没有账号？
        <router-link to="/register" class="font-semibold text-brand-500 hover:underline">立即注册</router-link>
      </p>
      <p class="mt-3 rounded-xl bg-slate-50 px-3 py-2 text-center text-xs text-slate-400 dark:bg-white/5">
        演示账号：demo / demo123
      </p>
    </div>
  </div>
</template>

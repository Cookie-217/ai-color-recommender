<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const form = ref({ username: '', email: '', password: '', displayName: '' })
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await auth.register({ ...form.value })
    router.push('/login')
  } catch (e) {
    error.value = e?.response?.data?.message || '注册失败，请稍后再试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="mx-auto max-w-md py-10">
    <div class="card p-6 sm:p-8">
      <h1 class="text-2xl font-extrabold">创建账号 ✨</h1>
      <p class="mt-1 text-sm text-slate-500 dark:text-slate-400">加入 ColorAI，开启智能配色之旅</p>
      <form class="mt-6 space-y-4" @submit.prevent="submit">
        <div>
          <label class="label">用户名</label>
          <input v-model="form.username" class="input" placeholder="yourname" required minlength="3" />
        </div>
        <div>
          <label class="label">昵称（可选）</label>
          <input v-model="form.displayName" class="input" placeholder="小明" />
        </div>
        <div>
          <label class="label">邮箱</label>
          <input v-model="form.email" type="email" class="input" placeholder="you@example.com" required />
        </div>
        <div>
          <label class="label">密码</label>
          <input v-model="form.password" type="password" class="input" placeholder="至少 6 位" required minlength="6" />
        </div>
        <p v-if="error" class="text-sm text-rose-500">{{ error }}</p>
        <button class="btn-primary w-full" :disabled="loading">{{ loading ? '注册中…' : '注册' }}</button>
      </form>
      <p class="mt-4 text-center text-sm text-slate-500 dark:text-slate-400">
        已有账号？
        <router-link to="/login" class="font-semibold text-brand-500 hover:underline">去登录</router-link>
      </p>
    </div>
  </div>
</template>

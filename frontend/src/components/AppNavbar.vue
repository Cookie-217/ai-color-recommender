<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import ThemeToggle from './ThemeToggle.vue'

const auth = useAuthStore()
const route = useRoute()
const open = ref(false)
const isDark = ref(document.documentElement.classList.contains('dark'))

const links = [
  { name: 'home', label: '首页', icon: '🏠' },
  { name: 'agent', label: 'AI 顾问', icon: '✨' },
  { name: 'tools', label: '调色工具', icon: '🎨' },
  { name: 'gallery', label: '我的调色板', icon: '📁', auth: true }
]

const visibleLinks = computed(() => links.filter(l => !l.auth || auth.token))

function toggleTheme() {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
  localStorage.setItem('colorai_theme', isDark.value ? 'dark' : 'light')
}

function logout() {
  auth.logout()
  open.value = false
}
</script>

<template>
  <header class="sticky top-0 z-40 border-b border-slate-200/60 bg-white/70 backdrop-blur-xl dark:border-white/10 dark:bg-slate-950/60">
    <div class="mx-auto flex max-w-6xl items-center justify-between px-4 py-3 sm:px-6">
      <router-link to="/" class="flex items-center gap-2 font-extrabold tracking-tight" @click="open=false">
        <span class="grid h-9 w-9 place-items-center rounded-xl bg-gradient-to-br from-brand-500 to-accent-500 text-white shadow-soft">C</span>
        <span class="text-lg">Color<span class="gradient-text">AI</span></span>
      </router-link>

      <nav class="hidden items-center gap-1 md:flex">
        <router-link v-for="l in visibleLinks" :key="l.name" :to="{ name: l.name }"
          class="rounded-xl px-3 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100 hover:text-slate-900 dark:text-slate-300 dark:hover:bg-white/10 dark:hover:text-white"
          active-class="!bg-brand-50 !text-brand-600 dark:!bg-white/10 dark:!text-white">
          <span class="mr-1">{{ l.icon }}</span>{{ l.label }}
        </router-link>
      </nav>

      <div class="flex items-center gap-2">
        <ThemeToggle :is-dark="isDark" @toggle="toggleTheme" />
        <template v-if="auth.token">
          <span class="hidden text-sm text-slate-500 dark:text-slate-400 sm:inline">{{ auth.user?.displayName || auth.user?.username }}</span>
          <button class="btn-ghost hidden sm:inline-flex" @click="logout">退出</button>
        </template>
        <template v-else>
          <router-link to="/login" class="btn-ghost hidden sm:inline-flex">登录</router-link>
          <router-link to="/register" class="btn-primary hidden sm:inline-flex">注册</router-link>
        </template>
        <button class="btn-ghost md:hidden" @click="open = !open" aria-label="菜单">☰</button>
      </div>
    </div>

    <!-- Mobile menu -->
    <transition name="page">
      <nav v-if="open" class="border-t border-slate-200/60 px-4 py-3 md:hidden dark:border-white/10">
        <router-link v-for="l in visibleLinks" :key="l.name" :to="{ name: l.name }" @click="open=false"
          class="block rounded-xl px-3 py-2.5 text-sm font-medium text-slate-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-white/10">
          <span class="mr-2">{{ l.icon }}</span>{{ l.label }}
        </router-link>
        <div class="mt-2 border-t border-slate-200/60 pt-2 dark:border-white/10">
          <template v-if="auth.token">
            <button class="block w-full rounded-xl px-3 py-2.5 text-left text-sm font-medium text-rose-500" @click="logout">退出登录</button>
          </template>
          <template v-else>
            <router-link to="/login" @click="open=false" class="block rounded-xl px-3 py-2.5 text-sm font-medium text-slate-600 dark:text-slate-300">登录</router-link>
            <router-link to="/register" @click="open=false" class="block rounded-xl px-3 py-2.5 text-sm font-medium text-brand-600">注册</router-link>
          </template>
        </div>
      </nav>
    </transition>
  </header>
</template>

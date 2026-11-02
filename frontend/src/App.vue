<script setup>
import { onMounted } from 'vue'
import AppNavbar from '@/components/AppNavbar.vue'
import AppFooter from '@/components/AppFooter.vue'

onMounted(() => {
  // 初始化主题
  const saved = localStorage.getItem('colorai_theme')
  if (saved === 'dark' || (!saved && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
    document.documentElement.classList.add('dark')
  }
})
</script>

<template>
  <div class="flex min-h-screen flex-col">
    <AppNavbar />
    <main class="mx-auto w-full max-w-6xl flex-1 px-4 pb-16 pt-6 sm:px-6">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <AppFooter />
  </div>
</template>

<style>
.page-enter-active, .page-leave-active { transition: opacity .25s ease, transform .25s ease; }
.page-enter-from { opacity: 0; transform: translateY(8px); }
.page-leave-to { opacity: 0; transform: translateY(-8px); }
</style>

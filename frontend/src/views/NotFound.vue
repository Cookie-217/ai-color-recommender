<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div class="grid min-h-[50vh] place-items-center text-center">
    <div>
      <div class="text-6xl font-extrabold gradient-text">404</div>
      <p class="mt-3 text-slate-500 dark:text-slate-400">页面走丢了，但配色不会。</p>
      <button class="btn-primary mt-6" @click="router.push('/')">返回首页</button>
    </div>
  </div>
</template>

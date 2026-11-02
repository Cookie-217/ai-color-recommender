<script setup>
const year = new Date().getFullYear()
</script>

<template>
  <footer class="border-t border-slate-200/60 bg-white/50 dark:border-white/10 dark:bg-white/5">
    <div class="mx-auto flex max-w-6xl flex-col items-center justify-between gap-3 px-4 py-6 text-sm text-slate-500 sm:flex-row sm:px-6 dark:text-slate-400">
      <div class="flex items-center gap-2">
        <span class="grid h-6 w-6 place-items-center rounded-lg bg-gradient-to-br from-brand-500 to-accent-500 text-xs text-white">C</span>
        <span>ColorAI · 基于 Java 的 AI 色彩搭配智能推荐系统</span>
      </div>
      <div class="flex items-center gap-4">
        <a href="https://github.com" target="_blank" rel="noreferrer" class="hover:text-brand-500">GitHub</a>
        <span>© {{ year }} · 让配色更智能</span>
      </div>
    </div>
  </footer>
</template>

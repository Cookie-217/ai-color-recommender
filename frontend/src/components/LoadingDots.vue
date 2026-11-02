<script setup>
defineProps({ label: { type: String, default: '加载中' } })
</script>

<template>
  <div class="flex items-center gap-2 text-sm text-slate-400">
    <span class="flex gap-1">
      <span class="h-2 w-2 animate-bounce rounded-full bg-brand-400 [animation-delay:-0.3s]"></span>
      <span class="h-2 w-2 animate-bounce rounded-full bg-brand-400 [animation-delay:-0.15s]"></span>
      <span class="h-2 w-2 animate-bounce rounded-full bg-brand-400"></span>
    </span>
    <span>{{ label }}</span>
  </div>
</template>

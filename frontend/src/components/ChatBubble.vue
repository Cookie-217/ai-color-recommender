<script setup>
defineProps({
  role: { type: String, default: 'user' }, // user | assistant
  text: { type: String, default: '' }
})
</script>

<template>
  <div class="flex w-full" :class="role === 'user' ? 'justify-end' : 'justify-start'">
    <div
      class="max-w-[85%] rounded-2xl px-4 py-3 text-sm leading-relaxed animate-fade-up"
      :class="role === 'user'
        ? 'bg-gradient-to-br from-brand-500 to-brand-600 text-white'
        : 'bg-white text-slate-700 shadow-soft dark:bg-white/10 dark:text-slate-100'"
    >
      <p class="whitespace-pre-wrap">{{ text }}</p>
    </div>
  </div>
</template>

<script setup>
defineProps({ isDark: Boolean })
defineEmits(['toggle'])
</script>

<template>
  <button class="btn-ghost !px-3" :aria-label="isDark ? '切换浅色' : '切换深色'" @click="$emit('toggle')">
    <span v-if="isDark">🌙</span>
    <span v-else>☀️</span>
  </button>
</template>

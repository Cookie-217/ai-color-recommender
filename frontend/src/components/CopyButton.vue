<script setup>
import { ref } from 'vue'
import { copyText, normalizeHex } from '@/utils/color'

const props = defineProps({
  text: { type: String, required: true },
  label: { type: String, default: '复制' }
})
const done = ref(false)
async function copy() {
  const ok = await copyText(normalizeHex(props.text))
  if (ok) {
    done.value = true
    setTimeout(() => (done.value = false), 1200)
  }
}
</script>

<template>
  <button class="btn-ghost !px-3 !py-1.5 text-xs" @click="copy">
    <span v-if="done">✓ 已复制</span>
    <span v-else>⧉ {{ label }}</span>
  </button>
</template>

<script setup>
import { ref } from 'vue'
import { bestTextColor, copyText, normalizeHex } from '@/utils/color'

const props = defineProps({
  colors: { type: Array, required: true },
  height: { type: String, default: 'h-24 sm:h-32' },
  showHex: { type: Boolean, default: true }
})

const copied = ref(-1)
async function copy(idx, hex) {
  const ok = await copyText(normalizeHex(hex))
  if (ok) {
    copied.value = idx
    setTimeout(() => (copied.value = -1), 900)
  }
}
</script>

<template>
  <div class="flex w-full overflow-hidden rounded-2xl shadow-soft" :class="height">
    <button
      v-for="(c, i) in colors"
      :key="i"
      class="group relative flex-1 transition hover:flex-[1.4]"
      :style="{ backgroundColor: c }"
      @click="copy(i, c)"
      :title="`复制 ${normalizeHex(c)}`"
    >
      <span
        v-if="showHex"
        class="pointer-events-none absolute inset-x-0 bottom-2 text-center text-[11px] font-semibold opacity-0 transition group-hover:opacity-100"
        :style="{ color: bestTextColor(c) }"
      >{{ copied === i ? '已复制 ✓' : normalizeHex(c) }}</span>
    </button>
  </div>
</template>

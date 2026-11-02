<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { paletteApi } from '@/api/palette'
import PaletteStrip from './PaletteStrip.vue'
import CopyButton from './CopyButton.vue'
import { normalizeHex } from '@/utils/color'

const props = defineProps({
  result: { type: Object, required: true },
  showAlternatives: { type: Boolean, default: true }
})
const emit = defineEmits(['apply-alternative'])

const auth = useAuthStore()
const router = useRouter()
const saving = ref(false)
const saved = ref(false)
const saveError = ref('')

const palette = computed(() => props.result?.palette || [])

async function save() {
  if (!auth.token) {
    router.push({ name: 'login', query: { redirect: '/gallery' } })
    return
  }
  saving.value = true
  saveError.value = ''
  try {
    await paletteApi.save({
      name: props.result.name || '我的调色板',
      colors: palette.value.map(normalizeHex),
      scenario: props.result.scenario || '',
      description: props.result.description || '',
      tags: [props.result.harmonyRule, props.result.mood].filter(Boolean)
    })
    saved.value = true
    setTimeout(() => (saved.value = false), 1500)
  } catch (e) {
    saveError.value = e?.response?.data?.message || '保存失败，请重试'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div v-if="result" class="card animate-fade-up p-4 sm:p-6">
    <div class="mb-3 flex flex-wrap items-center justify-between gap-2">
      <div>
        <h3 class="text-lg font-bold">{{ result.name }}</h3>
        <p class="text-xs text-slate-500 dark:text-slate-400">
          {{ result.harmonyRule }} · 共 {{ palette.length }} 色
        </p>
      </div>
      <div class="flex items-center gap-2">
        <CopyButton :text="palette.map(normalizeHex).join(', ')" label="复制全部" />
        <button class="btn-primary !py-1.5" :disabled="saving" @click="save">
          {{ saved ? '✓ 已保存' : saving ? '保存中…' : '保存' }}
        </button>
      </div>
    </div>

    <PaletteStrip :colors="palette" height="h-28 sm:h-36" />

    <p v-if="result.description" class="mt-4 text-sm leading-relaxed text-slate-600 dark:text-slate-300">
      {{ result.description }}
    </p>

    <div v-if="result.rationale?.length" class="mt-4">
      <p class="label">AI 推荐理由</p>
      <ul class="space-y-1.5">
        <li v-for="(r, i) in result.rationale" :key="i" class="flex gap-2 text-sm text-slate-600 dark:text-slate-300">
          <span class="text-brand-500">✦</span><span>{{ r }}</span>
        </li>
      </ul>
    </div>

    <div v-if="result.accessibility?.length" class="mt-4">
      <p class="label">无障碍对比建议</p>
      <div class="flex flex-wrap gap-2">
        <span v-for="(a, i) in result.accessibility" :key="i" class="chip">{{ a }}</span>
      </div>
    </div>

    <div v-if="result.gradient?.length" class="mt-4">
      <p class="label">渐变预览</p>
      <div class="flex h-10 overflow-hidden rounded-xl">
        <div v-for="(g, i) in result.gradient" :key="i" class="flex-1" :style="{ backgroundColor: g }" />
      </div>
    </div>

    <div v-if="showAlternatives && result.alternatives?.length" class="mt-5 border-t border-slate-200/60 pt-4 dark:border-white/10">
      <p class="label">其他和谐规则（点击切换）</p>
      <div class="grid gap-3 sm:grid-cols-2">
        <button v-for="(alt, i) in result.alternatives" :key="i"
          class="group rounded-xl border border-slate-200 p-2 text-left transition hover:border-brand-300 hover:bg-brand-50/40 dark:border-white/10 dark:hover:bg-white/5"
          @click="emit('apply-alternative', alt)">
          <div class="flex h-8 overflow-hidden rounded-lg">
            <div v-for="(c, j) in alt.colors" :key="j" class="flex-1" :style="{ backgroundColor: c }" />
          </div>
          <p class="mt-1.5 text-xs font-medium">{{ alt.label }}</p>
        </button>
      </div>
    </div>

    <p v-if="saveError" class="mt-3 text-sm text-rose-500">{{ saveError }}</p>
  </div>
</template>

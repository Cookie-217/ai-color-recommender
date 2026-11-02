<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { paletteApi } from '@/api'
import PaletteStrip from '@/components/PaletteStrip.vue'
import LoadingDots from '@/components/LoadingDots.vue'
import { normalizeHex } from '@/utils/color'

const router = useRouter()
const items = ref([])
const page = ref(0)
const loading = ref(false)
const done = ref(false)
const error = ref('')

async function load(reset = false) {
  if (loading.value || done.value) return
  loading.value = true
  error.value = ''
  try {
    const data = await paletteApi.list(page.value, 12)
    if (reset) items.value = []
    items.value.push(...data.content)
    page.value++
    if (data.last || data.content.length === 0) done.value = true
  } catch (e) {
    error.value = e?.response?.data?.message || '加载失败'
  } finally {
    loading.value = false
  }
}

async function remove(id) {
  if (!confirm('确定删除这个调色板吗？')) return
  try {
    await paletteApi.remove(id)
    items.value = items.value.filter(i => i.id !== id)
  } catch (e) {
    error.value = '删除失败'
  }
}

function formatDate(s) {
  if (!s) return ''
  return new Date(s).toLocaleDateString('zh-CN')
}

onMounted(() => load(true))
</script>

<template>
  <div class="space-y-6">
    <header class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-extrabold">我的调色板</h1>
        <p class="mt-1 text-sm text-slate-500 dark:text-slate-400">收藏与管理你生成的配色方案</p>
      </div>
      <button class="btn-primary" @click="router.push('/')">✨ 去生成</button>
    </header>

    <div v-if="loading && items.length === 0" class="card grid h-40 place-items-center"><LoadingDots label="加载中" /></div>
    <p v-if="error" class="text-sm text-rose-500">{{ error }}</p>

    <div v-if="items.length" class="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
      <div v-for="p in items" :key="p.id" class="card overflow-hidden p-4">
        <PaletteStrip :colors="p.colors.map(normalizeHex)" height="h-24" />
        <div class="mt-3 flex items-start justify-between gap-2">
          <div class="min-w-0">
            <h3 class="truncate font-bold">{{ p.name }}</h3>
            <p class="truncate text-xs text-slate-500 dark:text-slate-400">{{ p.description || p.scenario }}</p>
            <div class="mt-1 flex flex-wrap gap-1">
              <span v-for="(t, i) in p.tags" :key="i" class="chip !px-2 !py-0.5 text-[10px]">{{ t }}</span>
            </div>
            <p class="mt-1 text-[11px] text-slate-400">{{ formatDate(p.createdAt) }}</p>
          </div>
          <button class="shrink-0 rounded-lg p-2 text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-500/10" @click="remove(p.id)" title="删除">🗑</button>
        </div>
      </div>
    </div>

    <div v-if="!loading && items.length === 0 && !error" class="card grid h-40 place-items-center text-slate-400">
      还没有收藏，去首页生成并保存吧～
    </div>

    <div v-if="!done && items.length" class="flex justify-center">
      <button class="btn-ghost" :disabled="loading" @click="load()">{{ loading ? '加载中…' : '加载更多' }}</button>
    </div>
  </div>
</template>

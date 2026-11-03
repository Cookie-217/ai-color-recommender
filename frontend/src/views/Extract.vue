<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { colorApi } from '@/api'
import PaletteStrip from '@/components/PaletteStrip.vue'
import CopyButton from '@/components/CopyButton.vue'
import LoadingDots from '@/components/LoadingDots.vue'
import { normalizeHex, buildCss, downloadText } from '@/utils/color'

const router = useRouter()
const mode = ref('extract') // extract | brand
const file = ref(null)
const preview = ref('')
const loading = ref(false)
const error = ref('')
const data = ref(null)

const isBrand = computed(() => mode.value === 'brand')

function onPick(e) {
  const f = e.target.files && e.target.files[0]
  if (f) handleFile(f)
}
function onDrop(e) {
  const f = e.dataTransfer.files && e.dataTransfer.files[0]
  if (f) handleFile(f)
}
function handleFile(f) {
  if (!f.type.startsWith('image/')) { error.value = '请选择图片文件（PNG / JPG）'; return }
  if (f.size > 5 * 1024 * 1024) { error.value = '图片请控制在 5MB 以内'; return }
  file.value = f
  preview.value = URL.createObjectURL(f)
  error.value = ''
  run()
}
async function run() {
  if (!file.value) return
  loading.value = true
  error.value = ''
  try {
    data.value = isBrand.value
      ? await colorApi.brand(file.value)
      : await colorApi.extract(file.value)
  } catch (e) {
    error.value = e?.response?.data?.message || '图片解析失败，请换一张试试'
    data.value = null
  } finally {
    loading.value = false
  }
}
function switchMode(m) {
  mode.value = m
  if (file.value) run()
}
function usePrimary(hex) {
  router.push({ name: 'home', query: { base: normalizeHex(hex) } })
}
function exportCss() {
  if (!data.value?.dominantColors?.length) return
  const colors = data.value.suggestedPalette.map(normalizeHex)
  downloadText('extracted-palette.css', buildCss(colors), 'text/css')
}
</script>

<template>
  <div class="space-y-8">
    <section class="relative overflow-hidden rounded-3xl bg-gradient-to-br from-accent-600 via-brand-500 to-brand-600 p-8 text-white shadow-soft sm:p-12">
      <div class="absolute -right-10 -top-10 h-48 w-48 rounded-full bg-white/20 blur-2xl"></div>
      <div class="relative max-w-2xl">
        <span class="chip !border-white/30 !bg-white/15 !text-white">📷 AI 图像取色</span>
        <h1 class="mt-4 text-3xl font-extrabold leading-tight sm:text-4xl">从一张图，提取整套配色</h1>
        <p class="mt-3 max-w-xl text-sm text-white/85 sm:text-base">
          上传图片即可自动聚类提取主色、生成协调配色方案；切换到「品牌模式」还能得到主色 / 辅助 / 点缀 / 中性色板。
        </p>
      </div>
    </section>

    <div class="grid gap-6 lg:grid-cols-[380px_1fr]">
      <!-- 上传区 -->
      <aside class="card space-y-4 p-5">
        <div class="flex gap-2">
          <button class="chip flex-1 !py-2" :class="!isBrand ? '!border-brand-400 !bg-brand-50 !text-brand-600 dark:!bg-white/10 dark:!text-white' : ''" @click="switchMode('extract')">🎨 图片取色</button>
          <button class="chip flex-1 !py-2" :class="isBrand ? '!border-brand-400 !bg-brand-50 !text-brand-600 dark:!bg-white/10 dark:!text-white' : ''" @click="switchMode('brand')">🏷️ 品牌模式</button>
        </div>

        <label
          class="flex h-56 cursor-pointer flex-col items-center justify-center rounded-2xl border-2 border-dashed border-slate-300 text-center transition hover:border-brand-400 hover:bg-brand-50/40 dark:border-white/15 dark:hover:bg-white/5"
          @dragover.prevent @drop.prevent="onDrop">
          <img v-if="preview" :src="preview" class="h-full w-full rounded-2xl object-contain" alt="预览" />
          <div v-else class="px-6">
            <div class="text-4xl">🖼️</div>
            <p class="mt-2 text-sm font-medium text-slate-600 dark:text-slate-300">点击或拖拽图片到此处</p>
            <p class="mt-1 text-xs text-slate-400">支持 PNG / JPG，单张 ≤ 5MB</p>
          </div>
          <input type="file" accept="image/*" class="hidden" @change="onPick" />
        </label>

        <p class="text-xs text-slate-500 dark:text-slate-400">
          {{ isBrand ? '品牌模式：额外输出可用于 VI 的主色 / 辅助色 / 点缀色 / 中性色板。' : '取色模式：提取主色并生成协调配色方案。' }}
        </p>
      </aside>

      <!-- 结果区 -->
      <section class="space-y-5">
        <div v-if="loading" class="card grid h-64 place-items-center"><LoadingDots label="AI 正在分析图片色彩" /></div>
        <div v-else-if="error" class="card grid h-40 place-items-center text-rose-500">{{ error }}</div>
        <div v-else-if="!data" class="card grid h-64 place-items-center text-slate-400">上传图片后，这里会显示提取结果</div>

        <template v-else>
          <div class="card p-5">
            <div class="mb-3 flex items-center justify-between">
              <h3 class="text-lg font-bold">{{ data.paletteName }}</h3>
              <CopyButton :text="data.dominantColors.map(c => c.hex).join(', ')" label="复制主色" />
            </div>
            <p class="label">主色占比</p>
            <div class="mt-2 space-y-2">
              <div v-for="(c, i) in data.dominantColors" :key="i" class="flex items-center gap-3">
                <span class="h-8 w-10 shrink-0 rounded-lg ring-1 ring-black/10" :style="{ backgroundColor: c.hex }"></span>
                <div class="flex-1">
                  <div class="h-2.5 w-full overflow-hidden rounded-full bg-slate-100 dark:bg-white/10">
                    <div class="h-full rounded-full" :style="{ width: (c.ratio * 100).toFixed(1) + '%', backgroundColor: c.hex }"></div>
                  </div>
                </div>
                <span class="w-16 text-right font-mono text-xs">{{ (c.ratio * 100).toFixed(1) }}%</span>
                <CopyButton :text="c.hex" :label="c.hex" />
                <button class="btn-ghost !px-2 !py-1 text-xs" @click="usePrimary(c.hex)">用此色生成 →</button>
              </div>
            </div>
          </div>

          <div class="card p-5" v-if="data.suggestedPalette?.length">
            <div class="mb-3 flex items-center justify-between">
              <p class="label !mt-0">AI 建议配色（基于主色）</p>
              <button class="btn-ghost !px-2 !py-1 text-xs" @click="exportCss">导出 CSS</button>
            </div>
            <PaletteStrip :colors="data.suggestedPalette" height="h-28" />
          </div>

          <div class="card p-5" v-if="data.brandBoard">
            <p class="label">品牌色板</p>
            <div class="mt-3 grid grid-cols-2 gap-3 sm:grid-cols-5">
              <div v-for="(v, k) in data.brandBoard" :key="k" class="text-center">
                <div class="h-16 rounded-xl ring-1 ring-black/10" :style="{ backgroundColor: v }"></div>
                <p class="mt-1.5 text-xs font-medium capitalize">{{ { primary:'主色', secondary:'辅助', accent:'点缀', neutralLight:'中性浅', neutralDark:'中性深' }[k] }}</p>
                <CopyButton :text="v" :label="v" />
              </div>
            </div>
          </div>
        </template>
      </section>
    </div>
  </div>
</template>

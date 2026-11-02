<script setup>
import { ref, onMounted } from 'vue'
import { colorApi } from '@/api'
import LoadingDots from '@/components/LoadingDots.vue'
import { normalizeHex, isValidHex, bestTextColor } from '@/utils/color'

const tab = ref('convert')

// Convert
const convertHex = ref('#3478F6')
const convertData = ref(null)
const convertErr = ref('')
async function doConvert() {
  if (!isValidHex(convertHex.value)) { convertErr.value = 'HEX 不合法'; return }
  convertErr.value = ''
  convertData.value = await colorApi.convert(normalizeHex(convertHex.value))
}

// Contrast
const fg = ref('#FFFFFF')
const bg = ref('#1D3557')
const contrastData = ref(null)
async function doContrast() {
  if (!isValidHex(fg.value) || !isValidHex(bg.value)) return
  contrastData.value = await colorApi.contrast(normalizeHex(fg.value), normalizeHex(bg.value))
}

// Gradient
const gFrom = ref('#FF7EB6')
const gTo = ref('#3478F6')
const gSteps = ref(6)
const gradientData = ref([])
async function doGradient() {
  if (!isValidHex(gFrom.value) || !isValidHex(gTo.value)) return
  gradientData.value = await colorApi.gradient(normalizeHex(gFrom.value), normalizeHex(gTo.value), gSteps.value)
}

onMounted(() => { doConvert(); doContrast(); doGradient() })
</script>

<template>
  <div class="space-y-6">
    <header>
      <h1 class="text-2xl font-extrabold">调色工具箱</h1>
      <p class="mt-1 text-sm text-slate-500 dark:text-slate-400">色彩转换、WCAG 无障碍对比度检测与渐变生成，全部由后端 API 实时计算。</p>
    </header>

    <div class="flex gap-2">
      <button class="chip" :class="tab === 'convert' ? '!border-brand-400 !bg-brand-50 !text-brand-600 dark:!bg-white/10 dark:!text-white' : ''" @click="tab = 'convert'">🔄 色彩转换</button>
      <button class="chip" :class="tab === 'contrast' ? '!border-brand-400 !bg-brand-50 !text-brand-600 dark:!bg-white/10 dark:!text-white' : ''" @click="tab = 'contrast'">⚖️ 对比度</button>
      <button class="chip" :class="tab === 'gradient' ? '!border-brand-400 !bg-brand-50 !text-brand-600 dark:!bg-white/10 dark:!text-white' : ''" @click="tab = 'gradient'">🌈 渐变生成</button>
    </div>

    <!-- Convert -->
    <section v-show="tab === 'convert'" class="card p-5">
      <div class="flex flex-wrap items-end gap-3">
        <div>
          <label class="label">输入 HEX</label>
          <div class="flex items-center gap-2">
            <input type="color" v-model="convertHex" class="h-10 w-12 rounded-lg border border-slate-200 dark:border-white/10" @input="doConvert" />
            <input v-model="convertHex" class="input uppercase" @keyup.enter="doConvert" />
          </div>
        </div>
        <button class="btn-primary" @click="doConvert">转换</button>
      </div>
      <p v-if="convertErr" class="mt-2 text-sm text-rose-500">{{ convertErr }}</p>
      <div v-if="convertData" class="mt-5 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <div class="rounded-xl border border-slate-200 p-4 dark:border-white/10" :style="{ backgroundColor: convertData.hex }">
          <div class="h-16"></div>
          <p class="text-center text-sm font-semibold" :style="{ color: bestTextColor(convertData.hex) }">{{ convertData.hex }}</p>
        </div>
        <dl class="space-y-2 text-sm">
          <div class="flex justify-between"><dt class="text-slate-500">RGB</dt><dd class="font-mono">{{ convertData.rgb.r }}, {{ convertData.rgb.g }}, {{ convertData.rgb.b }}</dd></div>
          <div class="flex justify-between"><dt class="text-slate-500">HSL</dt><dd class="font-mono">{{ Math.round(convertData.hsl.h) }}°, {{ Math.round(convertData.hsl.s) }}%, {{ Math.round(convertData.hsl.l) }}%</dd></div>
          <div class="flex justify-between"><dt class="text-slate-500">HSV</dt><dd class="font-mono">{{ Math.round(convertData.hsv.h) }}°, {{ Math.round(convertData.hsv.s) }}%, {{ Math.round(convertData.hsv.v) }}%</dd></div>
          <div class="flex justify-between"><dt class="text-slate-500">CMYK</dt><dd class="font-mono">{{ Math.round(convertData.cmyk.c) }}, {{ Math.round(convertData.cmyk.m) }}, {{ Math.round(convertData.cmyk.y) }}, {{ Math.round(convertData.cmyk.k) }}</dd></div>
          <div class="flex justify-between"><dt class="text-slate-500">LAB</dt><dd class="font-mono">{{ Math.round(convertData.lab.l) }}, {{ Math.round(convertData.lab.a) }}, {{ Math.round(convertData.lab.b) }}</dd></div>
          <div class="flex justify-between"><dt class="text-slate-500">相对亮度</dt><dd class="font-mono">{{ convertData.luminance }}</dd></div>
          <div class="flex justify-between"><dt class="text-slate-500">推荐文字色</dt><dd class="font-mono">{{ convertData.bestTextColor }}</dd></div>
        </dl>
      </div>
      <div v-else class="py-6"><LoadingDots label="计算中" /></div>
    </section>

    <!-- Contrast -->
    <section v-show="tab === 'contrast'" class="card space-y-5 p-5">
      <div class="flex flex-wrap items-end gap-3">
        <div><label class="label">前景（文字）</label><input type="color" v-model="fg" class="h-10 w-12 rounded-lg border border-slate-200 dark:border-white/10" @input="doContrast" /></div>
        <div><label class="label">背景</label><input type="color" v-model="bg" class="h-10 w-12 rounded-lg border border-slate-200 dark:border-white/10" @input="doContrast" /></div>
        <button class="btn-primary" @click="doContrast">检测</button>
      </div>
      <div v-if="contrastData" class="grid gap-4 sm:grid-cols-2">
        <div class="rounded-2xl p-8 text-center text-lg font-semibold shadow-soft" :style="{ backgroundColor: contrastData.background, color: contrastData.foreground }">
          可读性预览 Aa 文字示例
        </div>
        <div class="space-y-2 text-sm">
          <div class="flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3 dark:bg-white/5">
            <span>对比度</span><span class="text-xl font-bold gradient-text">{{ contrastData.ratio }}:1</span>
          </div>
          <div class="flex items-center justify-between rounded-xl px-4 py-3" :class="contrastData.aaaPass ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10' : 'bg-slate-50 dark:bg-white/5'">
            <span>WCAG 等级</span><span class="font-semibold">{{ contrastData.level }}</span>
          </div>
          <div class="grid grid-cols-2 gap-2">
            <div class="rounded-xl px-4 py-3 text-center" :class="contrastData.aaPass ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10' : 'bg-rose-50 text-rose-600 dark:bg-rose-500/10'">AA {{ contrastData.aaPass ? '✓' : '✕' }}</div>
            <div class="rounded-xl px-4 py-3 text-center" :class="contrastData.aaaPass ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10' : 'bg-rose-50 text-rose-600 dark:bg-rose-500/10'">AAA {{ contrastData.aaaPass ? '✓' : '✕' }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Gradient -->
    <section v-show="tab === 'gradient'" class="card space-y-5 p-5">
      <div class="flex flex-wrap items-end gap-3">
        <div><label class="label">起始</label><input type="color" v-model="gFrom" class="h-10 w-12 rounded-lg border border-slate-200 dark:border-white/10" @input="doGradient" /></div>
        <div><label class="label">结束</label><input type="color" v-model="gTo" class="h-10 w-12 rounded-lg border border-slate-200 dark:border-white/10" @input="doGradient" /></div>
        <div><label class="label">阶数 {{ gSteps }}</label><input type="range" min="2" max="16" v-model.number="gSteps" class="w-32 accent-brand-500" @input="doGradient" /></div>
      </div>
      <div v-if="gradientData.length" class="space-y-3">
        <div class="flex h-16 overflow-hidden rounded-2xl shadow-soft">
          <div v-for="(c, i) in gradientData" :key="i" class="flex-1" :style="{ backgroundColor: c }" />
        </div>
        <div class="flex flex-wrap gap-2">
          <span v-for="(c, i) in gradientData" :key="i" class="chip font-mono">{{ c }}</span>
        </div>
      </div>
    </section>
  </div>
</template>

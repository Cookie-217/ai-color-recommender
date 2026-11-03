<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { agentApi, colorApi } from '@/api'
import PaletteResult from '@/components/PaletteResult.vue'
import PaletteStrip from '@/components/PaletteStrip.vue'
import LoadingDots from '@/components/LoadingDots.vue'
import { randomHex, normalizeHex, isValidHex } from '@/utils/color'

const route = useRoute()

const scenarios = [
  { value: 'web', label: '网页 / UI' }, { value: 'fashion', label: '时尚穿搭' },
  { value: 'interior', label: '室内空间' }, { value: 'brand', label: '品牌识别' },
  { value: 'poster', label: '海报视觉' }, { value: 'food', label: '美食餐饮' },
  { value: 'nature', label: '自然主题' }, { value: 'social', label: '社交内容' }
]
const moods = [
  { value: 'balanced', label: '均衡' }, { value: 'warm', label: '温暖' },
  { value: 'cool', label: '清凉' }, { value: 'calm', label: '平静' },
  { value: 'energetic', label: '活力' }, { value: 'luxury', label: '高级感' },
  { value: 'playful', label: '活泼' }, { value: 'minimal', label: '极简' },
  { value: 'vintage', label: '复古' }, { value: 'tech', label: '科技' },
  { value: 'nature', label: '自然' }, { value: 'romantic', label: '浪漫' }
]
const styles = [
  { value: 'vibrant', label: '鲜艳' }, { value: 'muted', label: '柔和' },
  { value: 'pastel', label: '粉彩' }, { value: 'dark', label: '暗色' }, { value: 'light', label: '明亮' }
]
const rules = [
  { value: 'COMPLEMENTARY', label: '互补色' }, { value: 'ANALOGOUS', label: '邻近色' },
  { value: 'TRIADIC', label: '三角色' }, { value: 'TETRADIC', label: '四角色' },
  { value: 'SPLIT_COMPLEMENTARY', label: '分裂互补' }, { value: 'MONOCHROMATIC', label: '单色阶' },
  { value: 'SHADES', label: '暗色调' }, { value: 'TINTS', label: '亮色调' }, { value: 'SQUARE', label: '正方形' }
]

const baseColor = ref('#3478F6')
const scenario = ref('web')
const mood = ref('balanced')
const style = ref('vibrant')
const count = ref(5)

const loading = ref(false)
const result = ref(null)
const error = ref('')

const harmonyRule = ref('COMPLEMENTARY')
const harmonyColors = ref([])
const harmonyLoading = ref(false)

async function generate() {
  if (!isValidHex(baseColor.value)) { error.value = '请输入合法的 HEX 颜色'; return }
  error.value = ''
  loading.value = true
  try {
    const res = await agentApi.recommend({
      baseColor: normalizeHex(baseColor.value),
      scenario: scenario.value,
      mood: mood.value,
      style: style.value,
      count: count.value,
      includeAlternatives: true
    })
    result.value = res
  } catch (e) {
    error.value = e?.response?.data?.message || '推荐失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

function randomBase() { baseColor.value = randomHex() }

async function applyHarmony(rule) {
  if (!isValidHex(baseColor.value)) { error.value = '请输入合法的 HEX 颜色'; return }
  harmonyRule.value = rule
  harmonyLoading.value = true
  try {
    harmonyColors.value = await colorApi.harmony(normalizeHex(baseColor.value), rule)
  } catch (e) {
    error.value = '和谐色生成失败'
  } finally {
    harmonyLoading.value = false
  }
}

function onApplyAlternative(alt) {
  if (!result.value) return
  result.value = { ...result.value, palette: alt.colors, harmonyRule: alt.rule, name: result.value.name }
}

onMounted(() => {
  const q = route.query.base
  if (q && isValidHex(String(q))) baseColor.value = normalizeHex(String(q))
  generate()
  applyHarmony('COMPLEMENTARY')
})
</script>

<template>
  <div class="space-y-8">
    <!-- Hero -->
    <section class="relative overflow-hidden rounded-3xl bg-gradient-to-br from-brand-600 via-brand-500 to-accent-500 p-8 text-white shadow-soft sm:p-12">
      <div class="absolute -right-10 -top-10 h-48 w-48 rounded-full bg-white/20 blur-2xl"></div>
      <div class="absolute -bottom-12 -left-8 h-56 w-56 rounded-full bg-accent-400/30 blur-3xl"></div>
      <div class="relative max-w-2xl">
        <span class="chip !border-white/30 !bg-white/15 !text-white">✦ AI 驱动 · 色彩和谐引擎</span>
        <h1 class="mt-4 text-3xl font-extrabold leading-tight sm:text-4xl">
          让每一次配色<br />都恰到好处
        </h1>
        <p class="mt-3 max-w-xl text-sm text-white/85 sm:text-base">
          基于色彩理论的和谐规则 + 多维度 AI 推荐 Agent，输入一个颜色或一句话，即可获得专业、可落地的配色方案。
        </p>
        <div class="mt-6 flex flex-wrap gap-3">
          <button class="rounded-xl bg-white px-5 py-2.5 text-sm font-semibold text-brand-600 shadow hover:bg-white/90" @click="generate">✨ 立即生成配色</button>
          <button class="rounded-xl border border-white/40 px-5 py-2.5 text-sm font-semibold text-white hover:bg-white/10" @click="randomBase">🎲 随机灵感</button>
        </div>
      </div>
    </section>

    <div class="grid gap-6 lg:grid-cols-[360px_1fr]">
      <!-- Controls -->
      <aside class="card space-y-5 p-5">
        <div>
          <label class="label">基准色</label>
          <div class="flex items-center gap-3">
            <input type="color" v-model="baseColor" class="h-11 w-14 cursor-pointer rounded-xl border border-slate-200 bg-white dark:border-white/10" />
            <input v-model="baseColor" class="input uppercase" placeholder="#3478F6" @keyup.enter="generate" />
            <button class="btn-ghost !px-3" @click="randomBase" title="随机">🎲</button>
          </div>
        </div>

        <div>
          <label class="label">使用场景</label>
          <div class="flex flex-wrap gap-2">
            <button v-for="s in scenarios" :key="s.value" class="chip" :class="scenario === s.value ? '!border-brand-400 !bg-brand-50 !text-brand-600 dark:!bg-white/10 dark:!text-white' : ''" @click="scenario = s.value">{{ s.label }}</button>
          </div>
        </div>

        <div>
          <label class="label">情绪基调</label>
          <div class="flex flex-wrap gap-2">
            <button v-for="m in moods" :key="m.value" class="chip" :class="mood === m.value ? '!border-brand-400 !bg-brand-50 !text-brand-600 dark:!bg-white/10 dark:!text-white' : ''" @click="mood = m.value">{{ m.label }}</button>
          </div>
        </div>

        <div>
          <label class="label">风格</label>
          <div class="flex flex-wrap gap-2">
            <button v-for="st in styles" :key="st.value" class="chip" :class="style === st.value ? '!border-brand-400 !bg-brand-50 !text-brand-600 dark:!bg-white/10 dark:!text-white' : ''" @click="style = st.value">{{ st.label }}</button>
          </div>
        </div>

        <div>
          <label class="label">颜色数量：{{ count }}</label>
          <input type="range" min="3" max="8" v-model.number="count" class="w-full accent-brand-500" />
        </div>

        <button class="btn-primary w-full" :disabled="loading" @click="generate">
          {{ loading ? '生成中…' : '✨ 智能推荐' }}
        </button>
        <p v-if="error" class="text-sm text-rose-500">{{ error }}</p>

        <!-- Harmony explorer -->
        <div class="border-t border-slate-200/60 pt-4 dark:border-white/10">
          <p class="label">和谐规则速览（基于基准色）</p>
          <div class="mb-3 flex flex-wrap gap-2">
            <button v-for="r in rules" :key="r.value" class="chip" :class="harmonyRule === r.value ? '!border-brand-400 !bg-brand-50 !text-brand-600 dark:!bg-white/10 dark:!text-white' : ''" @click="applyHarmony(r.value)">{{ r.label }}</button>
          </div>
          <div v-if="harmonyLoading" class="py-4"><LoadingDots label="生成中" /></div>
          <PaletteStrip v-else-if="harmonyColors.length" :colors="harmonyColors" height="h-16" />
        </div>
      </aside>

      <!-- Result -->
      <section>
        <div v-if="loading" class="card flex h-64 items-center justify-center"><LoadingDots label="AI 正在构思配色方案" /></div>
        <PaletteResult v-else-if="result" :result="result" @apply-alternative="onApplyAlternative" />
        <div v-else class="card grid h-64 place-items-center text-slate-400">暂无结果，点击「智能推荐」开始</div>
      </section>
    </div>
  </div>
</template>

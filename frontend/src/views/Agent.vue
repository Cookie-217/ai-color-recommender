<script setup>
import { ref, nextTick } from 'vue'
import { agentApi } from '@/api'
import ChatBubble from '@/components/ChatBubble.vue'
import PaletteResult from '@/components/PaletteResult.vue'
import LoadingDots from '@/components/LoadingDots.vue'

const messages = ref([
  { role: 'assistant', text: '你好，我是 ColorAI 配色顾问。告诉我你的需求，例如「温暖高级感的咖啡品牌配色」，我来生成方案并解释理由。' }
])
const input = ref('')
const loading = ref(false)
const activeResult = ref(null)
const chatEnd = ref(null)

const suggestions = [
  '温暖高级感的咖啡品牌配色',
  '清新科技风的网页 UI 配色',
  '复古胶片质感的海报配色',
  '治愈系室内空间配色',
  '活力活泼的社交 App 配色'
]

async function send(text) {
  const msg = (text ?? input.value).trim()
  if (!msg || loading.value) return
  input.value = ''
  messages.value.push({ role: 'user', text: msg })
  loading.value = true
  await nextTick()
  scroll()
  try {
    const res = await agentApi.chat({ message: msg })
    messages.value.push({ role: 'assistant', text: res.reply })
    activeResult.value = res
  } catch (e) {
    messages.value.push({ role: 'assistant', text: '抱歉，生成失败了，请稍后再试。' })
  } finally {
    loading.value = false
    await nextTick()
    scroll()
  }
}

function scroll() {
  chatEnd.value?.scrollIntoView({ behavior: 'smooth' })
}
</script>

<template>
  <div class="space-y-6">
    <header>
      <h1 class="text-2xl font-extrabold">AI 配色顾问 <span class="gradient-text">Agent</span></h1>
      <p class="mt-1 text-sm text-slate-500 dark:text-slate-400">用自然语言描述需求，AI 自动解析情绪 / 场景 / 风格，生成可解释的专业配色。</p>
    </header>

    <div class="grid gap-6 lg:grid-cols-[1fr_400px]">
      <!-- Chat -->
      <section class="card flex h-[60vh] flex-col p-4">
        <div class="flex-1 space-y-3 overflow-y-auto pr-1">
          <ChatBubble v-for="(m, i) in messages" :key="i" :role="m.role" :text="m.text" />
          <div v-if="loading" class="flex justify-start"><LoadingDots label="AI 思考中" /></div>
          <div ref="chatEnd"></div>
        </div>
        <div class="mt-3 flex gap-2">
          <input v-model="input" class="input" placeholder="描述你想要的配色…" @keyup.enter="send()" />
          <button class="btn-primary shrink-0" :disabled="loading" @click="send()">发送</button>
        </div>
        <div class="mt-3 flex flex-wrap gap-2">
          <button v-for="(s, i) in suggestions" :key="i" class="chip hover:!border-brand-400 hover:!text-brand-600 dark:hover:!text-white" @click="send(s)">{{ s }}</button>
        </div>
      </section>

      <!-- Result -->
      <section>
        <PaletteResult v-if="activeResult" :result="activeResult" />
        <div v-else class="card grid h-64 place-items-center text-sm text-slate-400">对话生成配色后，这里展示方案详情</div>
      </section>
    </div>
  </div>
</template>

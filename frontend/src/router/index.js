import { createRouter, createWebHashHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  { path: '/', name: 'home', component: () => import('@/views/Home.vue'), meta: { title: '首页' } },
  { path: '/agent', name: 'agent', component: () => import('@/views/Agent.vue'), meta: { title: 'AI 配色顾问' } },
  { path: '/tools', name: 'tools', component: () => import('@/views/Tools.vue'), meta: { title: '调色工具' } },
  { path: '/extract', name: 'extract', component: () => import('@/views/Extract.vue'), meta: { title: '图片取色' } },
  { path: '/gallery', name: 'gallery', component: () => import('@/views/Gallery.vue'), meta: { title: '我的调色板', requiresAuth: true } },
  { path: '/login', name: 'login', component: () => import('@/views/Login.vue'), meta: { title: '登录' } },
  { path: '/register', name: 'register', component: () => import('@/views/Register.vue'), meta: { title: '注册' } },
  { path: '/:pathMatch(.*)*', name: 'notfound', component: () => import('@/views/NotFound.vue') }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  document.title = (to.meta.title ? to.meta.title + ' · ' : '') + 'ColorAI'
})

export default router

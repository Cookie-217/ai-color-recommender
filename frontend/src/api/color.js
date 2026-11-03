import { api } from './http'

export const colorApi = {
  convert: (hex) => api.get('/api/color/convert', { params: { hex } }).then(r => r.data.data),
  contrast: (fg, bg) => api.get('/api/color/contrast', { params: { fg, bg } }).then(r => r.data.data),
  harmony: (base, rule) => api.get('/api/color/harmony', { params: { base, rule } }).then(r => r.data.data),
  all: (base) => api.get('/api/color/all', { params: { base } }).then(r => r.data.data),
  gradient: (from, to, steps) => api.get('/api/color/gradient', { params: { from, to, steps } }).then(r => r.data.data),
  rules: () => api.get('/api/color/rules').then(r => r.data.data),
  // 图片取色 / 品牌色彩提取（multipart，浏览器自动设置 boundary）
  extract: (file) => api.post('/api/color/extract', toForm(file), { headers: { 'Content-Type': undefined } }).then(r => r.data.data),
  brand: (file) => api.post('/api/color/brand', toForm(file), { headers: { 'Content-Type': undefined } }).then(r => r.data.data)
}

function toForm(file) {
  const fd = new FormData()
  fd.append('file', file)
  return fd
}

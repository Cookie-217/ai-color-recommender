export function isValidHex(hex) {
  return /^#?([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$/.test((hex || '').trim())
}

export function normalizeHex(hex) {
  let h = (hex || '').trim()
  if (!h.startsWith('#')) h = '#' + h
  if (h.length === 4) h = '#' + h[1] + h[1] + h[2] + h[2] + h[3] + h[3]
  return h.toUpperCase()
}

export function hexToRgb(hex) {
  const h = normalizeHex(hex).slice(1)
  return {
    r: parseInt(h.slice(0, 2), 16),
    g: parseInt(h.slice(2, 4), 16),
    b: parseInt(h.slice(4, 6), 16)
  }
}

export function luminance(hex) {
  const { r, g, b } = hexToRgb(hex)
  const a = [r, g, b].map((v) => {
    v /= 255
    return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4)
  })
  return 0.2126 * a[0] + 0.7152 * a[1] + 0.0722 * a[2]
}

export function contrastRatio(a, b) {
  const l1 = luminance(a)
  const l2 = luminance(b)
  const hi = Math.max(l1, l2)
  const lo = Math.min(l1, l2)
  return (hi + 0.05) / (lo + 0.05)
}

export function bestTextColor(bg) {
  return contrastRatio(bg, '#FFFFFF') >= contrastRatio(bg, '#000000') ? '#FFFFFF' : '#000000'
}

export function randomHex() {
  const n = Math.floor(Math.random() * 0xffffff)
  return '#' + n.toString(16).padStart(6, '0').toUpperCase()
}

export async function copyText(text) {
  try {
    await navigator.clipboard.writeText(text)
    return true
  } catch (e) {
    return false
  }
}

// 生成 CSS 变量 + 渐变（可用于 :root 引入）
export function buildCss(colors, name = 'palette') {
  const root = colors.map((c, i) => `  --color-${i + 1}: ${c};`).join('\n')
  const grad = `linear-gradient(135deg, ${colors.join(', ')})`
  return `:root {\n${root}\n}\n\n.${name}-gradient {\n  background: ${grad};\n}`
}

// 生成结构化 JSON
export function buildJson(result, colors) {
  return JSON.stringify({
    name: result?.name || 'palette',
    colors: (colors || result?.palette || []).map(normalizeHex),
    description: result?.description || '',
    rationale: result?.rationale || [],
    accessibility: result?.accessibility || [],
    gradient: result?.gradient || []
  }, null, 2)
}

// 浏览器下载文本文件
export function downloadText(filename, text, mime = 'text/plain') {
  const blob = new Blob([text], { type: mime })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  a.remove()
  URL.revokeObjectURL(url)
}

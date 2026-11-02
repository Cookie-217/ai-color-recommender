import axios from 'axios'

const base = import.meta.env.VITE_API_BASE || ''

export const api = axios.create({
  baseURL: base,
  timeout: 20000,
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('colorai_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('colorai_token')
      localStorage.removeItem('colorai_user')
    }
    return Promise.reject(err)
  }
)

export default api

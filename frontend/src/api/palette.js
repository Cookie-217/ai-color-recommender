import { api } from './http'

export const paletteApi = {
  list: (page = 0, size = 12) => api.get('/api/palettes', { params: { page, size } }).then(r => r.data.data),
  recent: (limit = 6) => api.get('/api/palettes/recent', { params: { limit } }).then(r => r.data.data),
  save: (payload) => api.post('/api/palettes', payload).then(r => r.data.data),
  get: (id) => api.get(`/api/palettes/${id}`).then(r => r.data.data),
  remove: (id) => api.delete(`/api/palettes/${id}`).then(r => r.data)
}

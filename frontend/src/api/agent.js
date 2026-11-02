import { api } from './http'

export const agentApi = {
  recommend: (payload) => api.post('/api/agent/recommend', payload).then(r => r.data.data),
  chat: (payload) => api.post('/api/agent/chat', payload).then(r => r.data.data)
}

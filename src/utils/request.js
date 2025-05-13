import axios from 'axios'
import store from '@/store'
import { getToken } from '@/utils/auth'
import { Message } from 'element-ui'

// 创建 axios 实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // 根据环境变量配置代理前缀
  timeout: 5000 // 请求超时时间
})

// 请求拦截器：在每个请求头中注入 token
service.interceptors.request.use(
  config => {
    if (store.getters.token) {
      // 假设后端从 token 中提取用户信息
      config.headers['Authorization'] = 'Bearer ' + getToken()
    }
    // 确保以 JSON 格式发送
    config.headers['Content-Type'] = 'application/json'
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 20000 && res.code !== 200) {
      Message({
        message: res.message || '出错了',
        type: 'error',
        duration: 5 * 1000
      })

      // 处理登录超时等情况
      if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
        Message({
          message: '登录已失效，请重新登录',
          type: 'warning',
          duration: 3000
        })
      }

      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误：' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service

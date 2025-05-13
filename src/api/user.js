import request from '@/utils/request'

// 登录接口：POST + JSON 参数
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data // 直接传 JSON：{ username, password }
  })
}

// 获取用户信息：POST + JSON 参数（包含 token）
export function getInfo(token) {
  return request({
    url: '/user/info',
    method: 'post',
    data: { token } // 将 token 放入 JSON 请求体中
  })
}

// 登出接口
export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

import { login, logout, getInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router, { resetRouter, asyncRoutes, filterAsyncRoutes } from '@/router'

const state = {
  token: getToken(),
  username: '',
  avatar: '',
  roles: [],
  userLoaded: false
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, username) => {
    state.username = username
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  },
  SET_USER_LOADED: (state, loaded) => {
    state.userLoaded = loaded
  }
}

const actions = {
  login({ commit }, userInfo) {
    const { username, password, role } = userInfo
    return new Promise((resolve, reject) => {
      login({ username, password, role }).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.token)
        setToken(data.token)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  async getInfo({ commit, state }) {
    if (state.userLoaded) {
      return Promise.resolve() // ✅ 如果已加载则不再请求
    }

    try {
      const { data } = await getInfo(state.token)
      if (!data) throw new Error('验证失败，请重新登录')

      const { username, role } = data
      const roles = [role]

      commit('SET_NAME', username)
      commit('SET_AVATAR', 'https://default-avatar-url.com/user.png')
      commit('SET_ROLES', roles)
      commit('SET_USER_LOADED', true)

      resetRouter()
      const accessedRoutes = filterAsyncRoutes(asyncRoutes, roles)
      router.addRoutes(accessedRoutes)

      return data
    } catch (error) {
      commit('SET_USER_LOADED', false)
      throw error
    }
  },

  logout({ commit }) {
    return new Promise(resolve => {
      logout().then(() => {
        commit('SET_TOKEN', '')
        commit('SET_ROLES', [])
        commit('SET_USER_LOADED', false) // ✅ 重置状态
        removeToken()
        resetRouter()
        resolve()
      })
    })
  },

  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('SET_ROLES', [])
      commit('SET_USER_LOADED', false) // ✅ 重置状态
      removeToken()
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

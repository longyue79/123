<template>
  <div class="login-container">
    <el-form :model="loginForm" ref="loginForm" class="login-form" @submit.native.prevent>
      <h3 class="title">系统登录</h3>
      <el-form-item>
        <el-select v-model="loginForm.role" placeholder="请选择身份">
          <el-option label="管理员" value="admin" />
          <el-option label="员工" value="employee" />
        </el-select>
      </el-form-item>
      <el-form-item prop="username">
        <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="el-icon-user" />
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="el-icon-lock" />
      </el-form-item>
      <el-button type="primary" class="login-btn" @click="handleLogin">登录</el-button>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        role: ''
      },
      redirect: '/'
    }
  },
  created() {
    this.redirect = this.$route.query.redirect || '/dashboard'
  },
  methods: {
    handleLogin() {
      this.$store.dispatch('user/login', this.loginForm).then(() => {
        // 登录成功后再获取用户信息，注册路由
        this.$store.dispatch('user/getInfo').then(() => {
          this.$router.push(this.redirect)
        })
      }).catch(err => {
        this.$message.error(err || '登录失败')
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  width: 400px;
  margin: 100px auto;
  padding: 30px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.login-btn {
  width: 100%;
}

.title {
  text-align: center;
  margin-bottom: 20px;
}
</style>

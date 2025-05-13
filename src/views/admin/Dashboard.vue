<template>
  <div class="dashboard-container">
    <div class="dashboard-text">
      欢迎你，{{ role === 'admin' ? '管理员' : role === 'employee' ? '员工' : '用户' }}：{{ username }}
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'AdminDashboard',
  computed: {
    ...mapGetters(['username', 'role'])
  }
}
</script>

<style scoped>
.dashboard-container {
  margin: 30px;
}
.dashboard-text {
  font-size: 24px;
  text-align: center;
  font-weight: bold;
}
</style>

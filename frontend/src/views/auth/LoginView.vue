<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { ArrowRight, Lock, Operation, User } from '@element-plus/icons-vue'
import { register } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const mode = ref<'login' | 'register'>('login')
const loading = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({ username: '', password: '', nickname: '' })
const rules = computed<FormRules>(() => ({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    {
      validator: (_rule, value, callback) =>
        mode.value === 'register' && !/^[A-Za-z0-9_]{4,32}$/.test(value)
          ? callback(new Error('用户名须为 4 至 32 位字母、数字或下划线'))
          : callback(),
      trigger: 'blur',
    },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) =>
        mode.value === 'register' && (value.length < 8 || value.length > 32)
          ? callback(new Error('密码长度须为 8 至 32 位'))
          : callback(),
      trigger: 'blur',
    },
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
}))

async function submit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    if (mode.value === 'register') {
      await register({ username: form.username, password: form.password, nickname: form.nickname })
      ElMessage.success('注册成功，请登录')
      mode.value = 'login'
      return
    }
    await authStore.login({ username: form.username, password: form.password })
    ElMessage.success('欢迎回来')
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard'
    await router.replace(redirect)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="login-page">
    <section class="login-story">
      <div class="story-brand"><Operation /> PageForge</div>
      <div class="story-content">
        <span class="story-kicker">BUILD WITH CLARITY</span>
        <h1>把重复留给框架，<br />把判断留给团队。</h1>
        <p>统一 CRUD、权限、分页与文件能力，让每一个企业后台页面都从清晰的约定开始。</p>
      </div>
      <div class="story-metrics">
        <div><strong>22</strong><span>当前接口</span></div>
        <div><strong>5</strong><span>后端模块</span></div>
        <div><strong>JWT</strong><span>无状态认证</span></div>
      </div>
    </section>

    <section class="login-panel">
      <div class="login-card">
        <span class="mobile-brand"><Operation /> PageForge 页面工坊</span>
        <h2>{{ mode === 'login' ? '登录管理后台' : '创建普通用户' }}</h2>
        <p>{{ mode === 'login' ? '使用已配置的系统账号继续' : '注册后将自动关联 USER 角色' }}</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submit">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" size="large" placeholder="4-32 位字母、数字或下划线" :prefix-icon="User" />
          </el-form-item>
          <el-form-item v-if="mode === 'register'" label="昵称" prop="nickname">
            <el-input v-model="form.nickname" size="large" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" size="large" type="password" show-password placeholder="8-32 位密码" :prefix-icon="Lock" />
          </el-form-item>
          <el-button class="submit-button" type="primary" size="large" native-type="submit" :loading="loading">
            {{ mode === 'login' ? '进入工作台' : '完成注册' }}
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </el-form>
        <button class="mode-switch" type="button" @click="mode = mode === 'login' ? 'register' : 'login'">
          {{ mode === 'login' ? '没有账号？注册普通用户' : '已有账号？返回登录' }}
        </button>
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { changePassword } from '@/api/auth'
import PageIntro from '@/components/PageIntro.vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const rules: FormRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 32, message: '新密码长度须为 8 至 32 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: (_rule, value, callback) => value === form.newPassword ? callback() : callback(new Error('两次密码输入不一致')), trigger: 'blur' },
  ],
}

async function submit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await changePassword({ oldPassword: form.oldPassword, newPassword: form.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    authStore.logout()
    await router.replace('/login')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page-stack narrow-page">
    <PageIntro tag="ACCOUNT" title="账号安全" description="修改当前登录用户的密码。成功后本地登录状态将被清除。" />
    <section class="content-card form-card">
      <div class="security-notice">
        <strong>密码要求</strong>
        <span>新密码长度为 8 至 32 位，请勿使用容易猜测的内容。</span>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="form.oldPassword" type="password" show-password placeholder="请输入当前密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password placeholder="8 至 32 位" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">确认修改</el-button>
      </el-form>
    </section>
  </div>
</template>

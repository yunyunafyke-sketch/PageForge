<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <main class="not-found">
    <span>404</span>
    <h1>页面没有找到</h1>
    <p>地址可能已经变更，回到工作台继续使用 PageForge。</p>
    <el-button type="primary" @click="router.push('/dashboard')">返回工作台</el-button>
  </main>
</template>

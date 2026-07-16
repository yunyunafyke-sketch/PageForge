<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Connection, Files, Key, UserFilled } from '@element-plus/icons-vue'
import PageIntro from '@/components/PageIntro.vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const cards = computed(() => [
  {
    label: '可用功能',
    value: authStore.functions.length,
    note: '由角色功能并集生成',
    icon: Key,
    tone: 'indigo',
  },
  {
    label: '当前角色',
    value: authStore.roles.length,
    note: authStore.roles.join(' · ') || '未分配角色',
    icon: UserFilled,
    tone: 'green',
  },
  { label: '已接入接口', value: 22, note: '全部使用 POST', icon: Connection, tone: 'amber' },
  { label: '文件能力', value: 2, note: '上传与删除', icon: Files, tone: 'cyan' },
])

const shortcuts = computed(() => [
  { title: '外包人员', description: '查询、新增、编辑和状态维护', path: '/business/staff', show: authStore.isAdmin && authStore.hasFunction('staff:outsourced:list') },
  { title: '权限配置', description: '维护用户、角色与功能关系', path: '/system/users', show: authStore.isAdmin && authStore.hasFunction('system:user:list') },
  { title: '文件管理', description: '上传文件到 OSS 并管理文件标识', path: '/files', show: true },
  { title: '接口总览', description: '查看当前后端全部接口约定', path: '/api-catalog', show: true },
].filter((item) => item.show))
</script>

<template>
  <div class="page-stack">
    <PageIntro
      tag="OVERVIEW"
      :title="`你好，${authStore.username}`"
      description="这里汇总你的角色、功能权限与 PageForge 当前已接入能力。"
    />

    <section class="metric-grid">
      <article v-for="card in cards" :key="card.label" class="metric-card" :class="`tone-${card.tone}`">
        <div class="metric-icon"><component :is="card.icon" /></div>
        <div><span>{{ card.label }}</span><strong>{{ card.value }}</strong><small>{{ card.note }}</small></div>
      </article>
    </section>

    <section class="content-card welcome-card">
      <div class="section-heading">
        <div><span>QUICK ACCESS</span><h3>常用入口</h3></div>
        <el-tag effect="plain" type="info">JWT 无状态认证</el-tag>
      </div>
      <div class="shortcut-grid">
        <button v-for="(item, index) in shortcuts" :key="item.path" type="button" @click="router.push(item.path)">
          <span>0{{ index + 1 }}</span>
          <div><strong>{{ item.title }}</strong><small>{{ item.description }}</small></div>
          <b>→</b>
        </button>
      </div>
    </section>

    <section class="architecture-strip">
      <div><span>01</span><strong>统一请求</strong><small>所有业务接口使用 POST</small></div>
      <i>→</i>
      <div><span>02</span><strong>身份认证</strong><small>Bearer JWT 自动注入</small></div>
      <i>→</i>
      <div><span>03</span><strong>权限呈现</strong><small>functions 控制菜单与按钮</small></div>
      <i>→</i>
      <div><span>04</span><strong>统一响应</strong><small>解析 ResultModel 与 PageResult</small></div>
    </section>
  </div>
</template>

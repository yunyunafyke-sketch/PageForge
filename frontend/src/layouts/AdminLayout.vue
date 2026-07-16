<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Avatar,
  Connection,
  Files,
  Fold,
  Grid,
  Lock,
  Menu as MenuIcon,
  Operation,
  SetUp,
  SwitchButton,
  User,
  UserFilled,
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const collapsed = ref(false)

const menuItems = computed(() => {
  const items = [
    { path: '/dashboard', label: '工作台', icon: Grid },
    { path: '/business/staff', label: '外包人员', icon: UserFilled, admin: true, code: 'staff:outsourced:list' },
    { path: '/system/users', label: '用户管理', icon: User, admin: true, code: 'system:user:list' },
    { path: '/system/roles', label: '角色管理', icon: Avatar, admin: true, code: 'system:role:list' },
    { path: '/system/functions', label: '功能管理', icon: SetUp, admin: true, code: 'system:function:list' },
    { path: '/files', label: '文件管理', icon: Files },
    { path: '/security', label: '账号安全', icon: Lock },
    { path: '/api-catalog', label: '接口总览', icon: Connection },
  ]
  return items.filter((item) => (!item.admin || authStore.isAdmin) && authStore.hasFunction(item.code))
})

function logout() {
  authStore.logout()
  void router.replace('/login')
}
</script>

<template>
  <el-container class="app-shell">
    <el-aside :width="collapsed ? '76px' : '244px'" class="app-aside">
      <div class="brand" :class="{ 'is-collapsed': collapsed }">
        <div class="brand-mark"><Operation /></div>
        <div v-if="!collapsed" class="brand-copy">
          <strong>PageForge</strong>
          <span>页面工坊</span>
        </div>
      </div>
      <el-scrollbar class="menu-scroll">
        <el-menu :default-active="route.path" :collapse="collapsed" router class="side-menu">
          <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.label }}</template>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
      <button class="collapse-button" type="button" @click="collapsed = !collapsed">
        <el-icon><Fold v-if="!collapsed" /><MenuIcon v-else /></el-icon>
        <span v-if="!collapsed">收起导航</span>
      </button>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div>
          <div class="eyebrow">PAGEFORGE CONSOLE</div>
          <h1>{{ route.meta.title }}</h1>
        </div>
        <el-dropdown trigger="click">
          <div class="user-trigger">
            <el-avatar :size="38">{{ authStore.username.slice(0, 1).toUpperCase() }}</el-avatar>
            <div class="user-copy">
              <strong>{{ authStore.username }}</strong>
              <span>{{ authStore.roles.join(' · ') }}</span>
            </div>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/security')">账号安全</el-dropdown-item>
              <el-dropdown-item divided :icon="SwitchButton" @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

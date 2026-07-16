import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { loadAuth } from '@/utils/storage'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    requiresAuth?: boolean
    adminOnly?: boolean
    functionCode?: string
  }
}

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/dashboard' },
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '工作台' },
      },
      {
        path: 'system/users',
        name: 'users',
        component: () => import('@/views/system/UserView.vue'),
        meta: { title: '用户管理', adminOnly: true, functionCode: 'system:user:list' },
      },
      {
        path: 'system/roles',
        name: 'roles',
        component: () => import('@/views/system/RoleView.vue'),
        meta: { title: '角色管理', adminOnly: true, functionCode: 'system:role:list' },
      },
      {
        path: 'system/functions',
        name: 'functions',
        component: () => import('@/views/system/FunctionView.vue'),
        meta: { title: '功能管理', adminOnly: true, functionCode: 'system:function:list' },
      },
      {
        path: 'business/staff',
        name: 'staff',
        component: () => import('@/views/business/StaffView.vue'),
        meta: { title: '外包人员', adminOnly: true, functionCode: 'staff:outsourced:list' },
      },
      {
        path: 'files',
        name: 'files',
        component: () => import('@/views/files/FileView.vue'),
        meta: { title: '文件管理' },
      },
      {
        path: 'security',
        name: 'security',
        component: () => import('@/views/account/SecurityView.vue'),
        meta: { title: '账号安全' },
      },
      {
        path: 'api-catalog',
        name: 'api-catalog',
        component: () => import('@/views/api/ApiCatalogView.vue'),
        meta: { title: '接口总览' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFoundView.vue'),
    meta: { title: '页面不存在' },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach((to) => {
  const auth = loadAuth()
  document.title = `${to.meta.title || '管理后台'} - PageForge`

  if (to.meta.requiresAuth && !auth?.token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.name === 'login' && auth?.token) return { name: 'dashboard' }
  if (to.meta.adminOnly && !auth?.roles.includes('ADMIN')) return { name: 'dashboard' }
  if (
    to.meta.functionCode &&
    !auth?.roles.includes('ADMIN') &&
    !auth?.functions.includes(to.meta.functionCode)
  ) {
    return { name: 'dashboard' }
  }
  return true
})

export default router

<script setup lang="ts">
import { ref } from 'vue'
import PageIntro from '@/components/PageIntro.vue'

type ApiItem = { path: string; name: string; auth: string; request: string; response: string }
type ApiGroup = { name: string; color: string; items: ApiItem[] }

const groups: ApiGroup[] = [
  {
    name: '认证与账号', color: '#4f46e5', items: [
      { path: '/api/auth/login', name: '账号登录', auth: '公开', request: 'LoginRequest', response: 'LoginVO' },
      { path: '/api/auth/register', name: '注册普通用户', auth: '公开', request: 'RegisterRequest', response: 'Long' },
      { path: '/api/auth/change-password', name: '修改当前密码', auth: '登录', request: 'ChangePasswordRequest', response: 'Boolean' },
      { path: '/api/admin/system/user/reset-password', name: '重置用户密码', auth: 'ADMIN', request: 'ResetPasswordRequest', response: 'Boolean' },
    ],
  },
  {
    name: '用户与权限', color: '#0f766e', items: [
      { path: '/api/admin/system/user/page', name: '用户分页', auth: 'ADMIN', request: 'UserPageRequest', response: 'PageResult<SystemUserVO>' },
      { path: '/api/admin/system/user/assign-roles', name: '分配用户角色', auth: 'ADMIN', request: 'UserAssignRolesRequest', response: 'Boolean' },
      { path: '/api/admin/system/role/page', name: '角色分页', auth: 'ADMIN', request: 'RolePageRequest', response: 'PageResult<RoleVO>' },
      { path: '/api/admin/system/role/create', name: '新增角色', auth: 'ADMIN', request: 'RoleCreateRequest', response: 'Long' },
      { path: '/api/admin/system/role/update', name: '修改角色', auth: 'ADMIN', request: 'RoleUpdateRequest', response: 'Boolean' },
      { path: '/api/admin/system/role/assign-functions', name: '分配角色功能', auth: 'ADMIN', request: 'RoleAssignFunctionsRequest', response: 'Boolean' },
      { path: '/api/admin/system/function/page', name: '功能分页', auth: 'ADMIN', request: 'FunctionPageRequest', response: 'PageResult<FunctionVO>' },
      { path: '/api/admin/system/function/create', name: '新增功能', auth: 'ADMIN', request: 'FunctionCreateRequest', response: 'Long' },
      { path: '/api/admin/system/function/update', name: '修改功能', auth: 'ADMIN', request: 'FunctionUpdateRequest', response: 'Boolean' },
    ],
  },
  {
    name: '外包人员', color: '#b45309', items: [
      { path: '/api/admin/outsourced-staff/page', name: '人员分页', auth: 'ADMIN', request: 'OutsourcedStaffPageRequest', response: 'PageResult<OutsourcedStaffVO>' },
      { path: '/api/admin/outsourced-staff/detail', name: '人员详情', auth: 'ADMIN', request: 'IdRequest', response: 'OutsourcedStaffVO' },
      { path: '/api/admin/outsourced-staff/create', name: '新增人员', auth: 'ADMIN', request: 'OutsourcedStaffCreateRequest', response: 'Long' },
      { path: '/api/admin/outsourced-staff/update', name: '修改人员', auth: 'ADMIN', request: 'OutsourcedStaffUpdateRequest', response: 'Boolean' },
      { path: '/api/admin/outsourced-staff/delete', name: '删除人员', auth: 'ADMIN', request: 'IdRequest', response: 'Boolean' },
      { path: '/api/admin/outsourced-staff/batch-delete', name: '批量删除人员', auth: 'ADMIN', request: 'IdsRequest', response: 'Boolean' },
      { path: '/api/admin/outsourced-staff/change-status', name: '修改人员状态', auth: 'ADMIN', request: 'OutsourcedStaffStatusRequest', response: 'Boolean' },
    ],
  },
  {
    name: 'OSS 文件', color: '#0369a1', items: [
      { path: '/api/user/oss/upload', name: '上传文件', auth: 'ADMIN / USER', request: 'multipart/form-data', response: 'OssFileVO' },
      { path: '/api/admin/oss/delete', name: '删除文件', auth: 'ADMIN', request: 'OssDeleteRequest', response: 'Boolean' },
    ],
  },
]

const active = ref(groups[0].name)
</script>

<template>
  <div class="page-stack">
    <PageIntro tag="API CONTRACT" title="后端接口总览" description="当前 22 个接口全部使用 POST，并统一返回 ResultModel<T>。分页接口返回 PageResult<T>。">
      <el-button type="primary" plain tag="a" href="http://localhost:8080/doc.html" target="_blank">打开 Knife4j</el-button>
    </PageIntro>
    <div class="catalog-layout">
      <nav class="catalog-nav content-card">
        <button v-for="group in groups" :key="group.name" type="button" :class="{ active: active === group.name }" @click="active = group.name">
          <i :style="{ background: group.color }"></i><span>{{ group.name }}</span><b>{{ group.items.length }}</b>
        </button>
      </nav>
      <section class="catalog-content content-card">
        <template v-for="group in groups" :key="group.name">
          <div v-if="active === group.name">
            <div class="section-heading"><div><span>POST ENDPOINTS</span><h3>{{ group.name }}</h3></div><el-tag effect="plain">{{ group.items.length }} 个接口</el-tag></div>
            <div class="api-list">
              <article v-for="item in group.items" :key="item.path">
                <span class="method-badge">POST</span>
                <div class="api-primary"><strong>{{ item.name }}</strong><code>{{ item.path }}</code></div>
                <div class="api-meta"><span>请求：{{ item.request }}</span><span>响应：{{ item.response }}</span></div>
                <el-tag :type="item.auth === '公开' ? 'success' : item.auth === '登录' ? 'info' : 'warning'" effect="light">{{ item.auth }}</el-tag>
              </article>
            </div>
          </div>
        </template>
      </section>
    </div>
  </div>
</template>

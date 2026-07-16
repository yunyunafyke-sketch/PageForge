<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { assignUserRoles, pageRoles, pageUsers, resetUserPassword } from '@/api/system'
import PageIntro from '@/components/PageIntro.vue'
import { useAuthStore } from '@/stores/auth'
import type { Role, SystemUser } from '@/types/api'

const authStore = useAuthStore()
const loading = ref(false)
const users = ref<SystemUser[]>([])
const roles = ref<Role[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const roleDialog = ref(false)
const saving = ref(false)
const currentUser = ref<SystemUser | null>(null)
const selectedRoleIds = ref<number[]>([])

async function loadData() {
  loading.value = true
  try {
    const result = await pageUsers(query)
    users.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

async function loadRoles() {
  const result = await pageRoles({ pageNum: 1, pageSize: 200 })
  roles.value = result.records
}

function openRoles(user: SystemUser) {
  currentUser.value = user
  selectedRoleIds.value = [...(user.roleIds || [])]
  roleDialog.value = true
}

async function saveRoles() {
  if (!currentUser.value) return
  saving.value = true
  try {
    await assignUserRoles({ userId: currentUser.value.id, roleIds: selectedRoleIds.value })
    ElMessage.success('用户角色已更新，用户重新登录后生效')
    roleDialog.value = false
    await loadData()
  } finally {
    saving.value = false
  }
}

async function resetPassword(user: SystemUser) {
  await ElMessageBox.confirm(`确认将 ${user.username} 的密码重置为 123456？`, '重置密码', { type: 'warning' })
  await resetUserPassword(user.id)
  ElMessage.success('密码已重置为 123456')
}

function search() {
  query.pageNum = 1
  void loadData()
}

onMounted(() => Promise.all([loadData(), loadRoles()]))
</script>

<template>
  <div class="page-stack">
    <PageIntro tag="SYSTEM" title="用户管理" description="查询系统用户、覆盖分配角色，并为忘记密码的用户执行管理员重置。" />
    <section class="content-card table-card">
      <div class="toolbar">
        <el-input v-model="query.keyword" clearable placeholder="搜索用户名或昵称" :prefix-icon="Search" @keyup.enter="search" @clear="search" />
        <el-button type="primary" :icon="Search" @click="search">查询</el-button>
        <el-button :icon="Refresh" @click="query.keyword = ''; search()">重置</el-button>
      </div>
      <el-table v-loading="loading" :data="users" row-key="id">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="username" label="用户名" min-width="150" />
        <el-table-column prop="nickname" label="昵称" min-width="150" />
        <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template></el-table-column>
        <el-table-column label="角色" min-width="240"><template #default="{ row }"><div class="tag-list"><el-tag v-for="id in row.roleIds" :key="id" effect="plain">{{ roles.find((role) => role.id === id)?.roleName || `角色 #${id}` }}</el-tag><span v-if="!row.roleIds?.length" class="muted">未分配</span></div></template></el-table-column>
        <el-table-column label="操作" width="190" fixed="right">
          <template #default="{ row }">
            <el-button v-if="authStore.hasFunction('system:user:assign-role')" link type="primary" @click="openRoles(row)">分配角色</el-button>
            <el-button link type="danger" @click="resetPassword(row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next" @change="loadData" />
    </section>

    <el-dialog v-model="roleDialog" title="分配用户角色" width="520px">
      <p class="dialog-tip">将使用所选完整列表覆盖 {{ currentUser?.username }} 当前的角色；清空表示移除全部角色。</p>
      <el-checkbox-group v-model="selectedRoleIds" class="option-grid">
        <el-checkbox v-for="role in roles" :key="role.id" :value="role.id" border><strong>{{ role.roleName }}</strong><small>{{ role.roleCode }}</small></el-checkbox>
      </el-checkbox-group>
      <template #footer><el-button @click="roleDialog = false">取消</el-button><el-button type="primary" :loading="saving" @click="saveRoles">保存角色</el-button></template>
    </el-dialog>
  </div>
</template>

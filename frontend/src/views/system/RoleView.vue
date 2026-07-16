<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { assignRoleFunctions, createRole, pageFunctions, pageRoles, updateRole } from '@/api/system'
import PageIntro from '@/components/PageIntro.vue'
import { useAuthStore } from '@/stores/auth'
import type { FunctionItem, Role } from '@/types/api'

const authStore = useAuthStore()
const loading = ref(false)
const roles = ref<Role[]>([])
const functions = ref<FunctionItem[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const editDialog = ref(false)
const functionDialog = ref(false)
const saving = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<{ id?: number; roleCode: string; roleName: string; description: string }>({ roleCode: '', roleName: '', description: '' })
const currentRole = ref<Role | null>(null)
const selectedFunctionIds = ref<number[]>([])
const rules: FormRules = {
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[A-Z][A-Z0-9_]{1,31}$/, message: '使用 2-32 位大写字母、数字或下划线', trigger: 'blur' },
  ],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const result = await pageRoles(query)
    roles.value = result.records
    total.value = result.total
  } finally { loading.value = false }
}

async function loadFunctions() {
  functions.value = (await pageFunctions({ pageNum: 1, pageSize: 200 })).records
}

function openEdit(role?: Role) {
  Object.assign(form, role ? { id: role.id, roleCode: role.roleCode, roleName: role.roleName, description: role.description || '' } : { id: undefined, roleCode: '', roleName: '', description: '' })
  editDialog.value = true
}

async function saveRole() {
  await formRef.value?.validate()
  saving.value = true
  try {
    const payload = { roleCode: form.roleCode, roleName: form.roleName, description: form.description }
    if (form.id) await updateRole({ id: form.id, ...payload })
    else await createRole(payload)
    ElMessage.success(form.id ? '角色已更新' : '角色已创建')
    editDialog.value = false
    await loadData()
  } finally { saving.value = false }
}

function openFunctions(role: Role) {
  currentRole.value = role
  selectedFunctionIds.value = [...(role.functionIds || [])]
  functionDialog.value = true
}

async function saveFunctions() {
  if (!currentRole.value) return
  saving.value = true
  try {
    await assignRoleFunctions({ roleId: currentRole.value.id, functionIds: selectedFunctionIds.value })
    ElMessage.success('角色功能已更新，关联用户重新登录后生效')
    functionDialog.value = false
    await loadData()
  } finally { saving.value = false }
}

function search() { query.pageNum = 1; void loadData() }
onMounted(() => Promise.all([loadData(), loadFunctions()]))
</script>

<template>
  <div class="page-stack">
    <PageIntro tag="SYSTEM" title="角色管理" description="维护角色基础信息，并使用完整功能 ID 列表覆盖角色权限。">
      <el-button v-if="authStore.hasFunction('system:role:create')" type="primary" :icon="Plus" @click="openEdit()">新增角色</el-button>
    </PageIntro>
    <section class="content-card table-card">
      <div class="toolbar"><el-input v-model="query.keyword" clearable placeholder="搜索角色编码或名称" :prefix-icon="Search" @keyup.enter="search" @clear="search" /><el-button type="primary" :icon="Search" @click="search">查询</el-button></div>
      <el-table v-loading="loading" :data="roles" row-key="id">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleCode" label="角色编码" min-width="150"><template #default="{ row }"><code class="code-pill">{{ row.roleCode }}</code></template></el-table-column>
        <el-table-column prop="roleName" label="角色名称" min-width="140" />
        <el-table-column prop="description" label="说明" min-width="200" show-overflow-tooltip />
        <el-table-column label="功能数" width="100"><template #default="{ row }"><strong>{{ row.functionIds?.length || 0 }}</strong></template></el-table-column>
        <el-table-column label="操作" width="180" fixed="right"><template #default="{ row }"><el-button v-if="authStore.hasFunction('system:role:update')" link type="primary" @click="openEdit(row)">编辑</el-button><el-button v-if="authStore.hasFunction('system:role:assign-function')" link type="success" @click="openFunctions(row)">分配功能</el-button></template></el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next" @change="loadData" />
    </section>

    <el-dialog v-model="editDialog" :title="form.id ? '编辑角色' : '新增角色'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="角色编码" prop="roleCode"><el-input v-model="form.roleCode" placeholder="例如 MANAGER" /></el-form-item>
        <el-form-item label="角色名称" prop="roleName"><el-input v-model="form.roleName" maxlength="64" /></el-form-item>
        <el-form-item label="角色说明"><el-input v-model="form.description" type="textarea" :rows="3" maxlength="255" show-word-limit /></el-form-item>
      </el-form>
      <template #footer><el-button @click="editDialog = false">取消</el-button><el-button type="primary" :loading="saving" @click="saveRole">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="functionDialog" title="分配角色功能" width="720px">
      <p class="dialog-tip">将使用所选完整列表覆盖 {{ currentRole?.roleName }} 当前的功能；权限变化后用户需要重新登录。</p>
      <el-checkbox-group v-model="selectedFunctionIds" class="function-option-grid">
        <el-checkbox v-for="item in functions" :key="item.id" :value="item.id" border><strong>{{ item.functionName }}</strong><small>{{ item.functionCode }}</small></el-checkbox>
      </el-checkbox-group>
      <template #footer><el-button @click="functionDialog = false">取消</el-button><el-button type="primary" :loading="saving" @click="saveFunctions">保存功能</el-button></template>
    </el-dialog>
  </div>
</template>

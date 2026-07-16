<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Plus, Search } from '@element-plus/icons-vue'
import {
  batchDeleteStaff,
  changeStaffStatus,
  createStaff,
  deleteStaff,
  getStaffDetail,
  pageStaff,
  updateStaff,
} from '@/api/staff'
import PageIntro from '@/components/PageIntro.vue'
import { useAuthStore } from '@/stores/auth'
import type { OutsourcedStaff } from '@/types/api'

const authStore = useAuthStore()
const loading = ref(false)
const rows = ref<OutsourcedStaff[]>([])
const total = ref(0)
const selectedRows = ref<OutsourcedStaff[]>([])
const query = reactive<{ pageNum: number; pageSize: number; name: string; status?: number }>({ pageNum: 1, pageSize: 10, name: '', status: undefined })
const editDialog = ref(false)
const detailDialog = ref(false)
const saving = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<{ id?: number; name: string; accountId: string; phone: string; status: number }>({ name: '', accountId: '', phone: '', status: 1 })
const detail = ref<OutsourcedStaff | null>(null)
const rules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  accountId: [{ required: true, message: '请输入账号 ID', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

async function loadData() {
  loading.value = true
  try {
    const result = await pageStaff(query)
    rows.value = result.records
    total.value = result.total
  } finally { loading.value = false }
}

function search() { query.pageNum = 1; void loadData() }
function resetQuery() { Object.assign(query, { pageNum: 1, name: '', status: undefined }); void loadData() }

function openEdit(row?: OutsourcedStaff) {
  Object.assign(form, row ? { id: row.id, name: row.name, accountId: row.accountId, phone: row.phone || '', status: row.status } : { id: undefined, name: '', accountId: '', phone: '', status: 1 })
  editDialog.value = true
}

async function save() {
  await formRef.value?.validate()
  saving.value = true
  try {
    const payload = { name: form.name, accountId: form.accountId, phone: form.phone, status: form.status }
    if (form.id) await updateStaff({ id: form.id, ...payload })
    else await createStaff(payload)
    ElMessage.success(form.id ? '人员信息已更新' : '外包人员已新增')
    editDialog.value = false
    await loadData()
  } finally { saving.value = false }
}

async function showDetail(row: OutsourcedStaff) {
  detail.value = await getStaffDetail(row.id)
  detailDialog.value = true
}

async function remove(row: OutsourcedStaff) {
  await ElMessageBox.confirm(`确认删除外包人员 ${row.name}？`, '删除确认', { type: 'warning' })
  await deleteStaff(row.id)
  ElMessage.success('已删除')
  await loadData()
}

async function removeSelected() {
  if (!selectedRows.value.length) return
  await ElMessageBox.confirm(`确认批量删除选中的 ${selectedRows.value.length} 人？`, '批量删除', { type: 'warning' })
  await batchDeleteStaff(selectedRows.value.map((row) => row.id))
  ElMessage.success('批量删除成功')
  await loadData()
}

async function toggleStatus(row: OutsourcedStaff) {
  const next = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(`确认${next === 1 ? '启用' : '禁用'} ${row.name}？`, '状态变更')
  await changeStaffStatus(row.id, next)
  ElMessage.success('状态已更新')
  await loadData()
}

function formatTime(value?: string) {
  return value ? new Date(value).toLocaleString('zh-CN', { hour12: false }) : '-'
}

onMounted(loadData)
</script>

<template>
  <div class="page-stack">
    <PageIntro tag="BUSINESS" title="外包人员" description="完整呈现分页、详情、新增、修改、删除、批量删除和状态变更接口。">
      <el-button v-if="authStore.hasFunction('staff:outsourced:delete')" :icon="Delete" :disabled="!selectedRows.length" @click="removeSelected">批量删除</el-button>
      <el-button v-if="authStore.hasFunction('staff:outsourced:create')" type="primary" :icon="Plus" @click="openEdit()">新增人员</el-button>
    </PageIntro>
    <section class="content-card table-card">
      <div class="toolbar wide-toolbar">
        <el-input v-model="query.name" clearable placeholder="搜索姓名" :prefix-icon="Search" @keyup.enter="search" @clear="search" />
        <el-select v-model="query.status" clearable placeholder="全部状态"><el-option label="启用" :value="1" /><el-option label="禁用" :value="0" /></el-select>
        <el-button type="primary" :icon="Search" @click="search">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>
      <el-table v-loading="loading" :data="rows" row-key="id" @selection-change="selectedRows = $event">
        <el-table-column type="selection" width="48" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" min-width="120"><template #default="{ row }"><button class="table-link" type="button" @click="showDetail(row)">{{ row.name }}</button></template></el-table-column>
        <el-table-column prop="accountId" label="账号 ID" min-width="150"><template #default="{ row }"><code class="code-pill">{{ row.accountId }}</code></template></el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template></el-table-column>
        <el-table-column label="创建时间" min-width="180"><template #default="{ row }">{{ formatTime(row.gmtCreate) }}</template></el-table-column>
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showDetail(row)">详情</el-button>
            <el-button v-if="authStore.hasFunction('staff:outsourced:update')" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="authStore.hasFunction('staff:outsourced:update')" link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-button v-if="authStore.hasFunction('staff:outsourced:delete')" link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next" @change="loadData" />
    </section>

    <el-dialog v-model="editDialog" :title="form.id ? '编辑外包人员' : '新增外包人员'" width="540px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-row"><el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item><el-form-item label="账号 ID" prop="accountId"><el-input v-model="form.accountId" /></el-form-item></div>
        <el-form-item label="手机号"><el-input v-model="form.phone" placeholder="选填" /></el-form-item>
        <el-form-item label="状态" prop="status"><el-radio-group v-model="form.status"><el-radio-button :value="1">启用</el-radio-button><el-radio-button :value="0">禁用</el-radio-button></el-radio-group></el-form-item>
      </el-form>
      <template #footer><el-button @click="editDialog = false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="detailDialog" title="外包人员详情" width="580px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="账号 ID">{{ detail.accountId }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detail.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="detail.status === 1 ? 'success' : 'info'">{{ detail.status === 1 ? '启用' : '禁用' }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(detail.gmtCreate) }}</el-descriptions-item>
        <el-descriptions-item label="修改时间" :span="2">{{ formatTime(detail.gmtModified) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

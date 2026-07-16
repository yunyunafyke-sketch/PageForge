<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { createFunction, pageFunctions, updateFunction } from '@/api/system'
import PageIntro from '@/components/PageIntro.vue'
import { useAuthStore } from '@/stores/auth'
import type { FunctionItem } from '@/types/api'

const authStore = useAuthStore()
const loading = ref(false)
const items = ref<FunctionItem[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const dialog = ref(false)
const saving = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<{ id?: number; functionCode: string; functionName: string; description: string }>({ functionCode: '', functionName: '', description: '' })
const rules: FormRules = {
  functionCode: [
    { required: true, message: '请输入功能编码', trigger: 'blur' },
    { pattern: /^[a-z][a-z0-9-]*:[a-z][a-z0-9-]*:[a-z][a-z0-9-]*$/, message: '格式必须为 模块:资源:操作', trigger: 'blur' },
  ],
  functionName: [{ required: true, message: '请输入功能名称', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const result = await pageFunctions(query)
    items.value = result.records
    total.value = result.total
  } finally { loading.value = false }
}

function openEdit(item?: FunctionItem) {
  Object.assign(form, item ? { ...item, description: item.description || '' } : { id: undefined, functionCode: '', functionName: '', description: '' })
  dialog.value = true
}

async function save() {
  await formRef.value?.validate()
  saving.value = true
  try {
    const payload = { functionCode: form.functionCode, functionName: form.functionName, description: form.description }
    if (form.id) await updateFunction({ id: form.id, ...payload })
    else await createFunction(payload)
    ElMessage.success(form.id ? '功能已更新' : '功能已创建')
    dialog.value = false
    await loadData()
  } finally { saving.value = false }
}

function search() { query.pageNum = 1; void loadData() }
onMounted(loadData)
</script>

<template>
  <div class="page-stack">
    <PageIntro tag="SYSTEM" title="功能管理" description="功能编码采用 模块:资源:操作 格式，前端据此控制菜单和操作按钮。">
      <el-button v-if="authStore.hasFunction('system:function:create')" type="primary" :icon="Plus" @click="openEdit()">新增功能</el-button>
    </PageIntro>
    <section class="content-card table-card">
      <div class="toolbar"><el-input v-model="query.keyword" clearable placeholder="搜索功能编码或名称" :prefix-icon="Search" @keyup.enter="search" @clear="search" /><el-button type="primary" :icon="Search" @click="search">查询</el-button></div>
      <el-table v-loading="loading" :data="items" row-key="id">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="functionCode" label="功能编码" min-width="240"><template #default="{ row }"><code class="code-pill">{{ row.functionCode }}</code></template></el-table-column>
        <el-table-column prop="functionName" label="功能名称" min-width="160" />
        <el-table-column prop="description" label="说明" min-width="260" show-overflow-tooltip />
        <el-table-column label="操作" width="100" fixed="right"><template #default="{ row }"><el-button v-if="authStore.hasFunction('system:function:update')" link type="primary" @click="openEdit(row)">编辑</el-button></template></el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next" @change="loadData" />
    </section>
    <el-dialog v-model="dialog" :title="form.id ? '编辑功能' : '新增功能'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="功能编码" prop="functionCode"><el-input v-model="form.functionCode" placeholder="例如 staff:outsourced:create" /></el-form-item>
        <el-form-item label="功能名称" prop="functionName"><el-input v-model="form.functionName" maxlength="64" /></el-form-item>
        <el-form-item label="功能说明"><el-input v-model="form.description" type="textarea" :rows="3" maxlength="255" show-word-limit /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialog = false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

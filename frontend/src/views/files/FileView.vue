<script setup lang="ts">
import { ref } from 'vue'
import type { UploadFile, UploadInstance, UploadRequestOptions } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, UploadFilled } from '@element-plus/icons-vue'
import { deleteFile, uploadFile } from '@/api/oss'
import PageIntro from '@/components/PageIntro.vue'
import { useAuthStore } from '@/stores/auth'
import type { OssFile } from '@/types/api'

const authStore = useAuthStore()
const uploadRef = ref<UploadInstance>()
const directory = ref('pageforge')
const progress = ref(0)
const uploading = ref(false)
const uploadedFiles = ref<OssFile[]>([])
const deleteKey = ref('')
const deleting = ref(false)

async function handleUpload(options: UploadRequestOptions) {
  uploading.value = true
  progress.value = 0
  try {
    const result = await uploadFile(options.file, directory.value, (value) => (progress.value = value))
    uploadedFiles.value.unshift(result)
    deleteKey.value = result.objectKey
    options.onSuccess(result)
    ElMessage.success('文件上传成功')
  } catch (error) {
    options.onError(error as never)
  } finally {
    uploading.value = false
    uploadRef.value?.clearFiles()
  }
}

function beforeUpload(file: UploadFile) {
  if (file.size && file.size > 20 * 1024 * 1024) {
    ElMessage.warning('单个文件不能超过 20 MB')
    return false
  }
  return true
}

async function removeByKey(key = deleteKey.value) {
  if (!key.trim()) return ElMessage.warning('请输入文件标识')
  await ElMessageBox.confirm(`确认删除 ${key}？`, '删除 OSS 文件', { type: 'warning' })
  deleting.value = true
  try {
    await deleteFile(key)
    uploadedFiles.value = uploadedFiles.value.filter((item) => item.objectKey !== key)
    if (deleteKey.value === key) deleteKey.value = ''
    ElMessage.success('文件已删除')
  } finally {
    deleting.value = false
  }
}

function formatSize(bytes: number) {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`
}
</script>

<template>
  <div class="page-stack">
    <PageIntro tag="ALIYUN OSS" title="文件管理" description="登录用户可上传文件；删除文件仅对 ADMIN 角色开放。" />
    <div class="two-column-grid">
      <section class="content-card upload-card">
        <div class="section-heading"><div><span>UPLOAD</span><h3>上传文件</h3></div></div>
        <el-form label-position="top">
          <el-form-item label="存储目录">
            <el-input v-model="directory" placeholder="例如 pageforge/avatar" maxlength="64" />
            <small class="field-help">仅允许字母、数字、斜杠、横线和下划线</small>
          </el-form-item>
        </el-form>
        <el-upload ref="uploadRef" drag :limit="1" :show-file-list="true" :http-request="handleUpload" :before-upload="beforeUpload">
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">拖放文件到这里，或<em>点击选择</em></div>
          <template #tip><div class="el-upload__tip">单个文件不超过 20 MB</div></template>
        </el-upload>
        <el-progress v-if="uploading" :percentage="progress" :stroke-width="8" class="upload-progress" />
      </section>

      <section class="content-card upload-card" :class="{ 'is-disabled': !authStore.isAdmin }">
        <div class="section-heading"><div><span>ADMIN ONLY</span><h3>删除文件</h3></div><el-tag v-if="!authStore.isAdmin" type="info">需要 ADMIN</el-tag></div>
        <p class="card-description">根据上传结果中的 objectKey 永久删除 OSS 文件。</p>
        <el-input v-model="deleteKey" :disabled="!authStore.isAdmin" type="textarea" :rows="4" placeholder="请输入 objectKey" />
        <el-button class="delete-file-button" type="danger" :icon="Delete" :disabled="!authStore.isAdmin" :loading="deleting" @click="removeByKey()">删除文件</el-button>
      </section>
    </div>

    <section class="content-card">
      <div class="section-heading"><div><span>THIS SESSION</span><h3>本次上传结果</h3></div><small>后端暂未提供 OSS 文件列表接口</small></div>
      <el-empty v-if="!uploadedFiles.length" description="上传成功后将在这里展示文件信息" :image-size="88" />
      <el-table v-else :data="uploadedFiles">
        <el-table-column prop="originalName" label="文件名" min-width="180" />
        <el-table-column prop="objectKey" label="文件标识" min-width="260" show-overflow-tooltip />
        <el-table-column label="大小" width="110"><template #default="{ row }">{{ formatSize(row.size) }}</template></el-table-column>
        <el-table-column label="访问地址" width="110"><template #default="{ row }"><el-link :href="row.url" target="_blank" type="primary">打开文件</el-link></template></el-table-column>
        <el-table-column v-if="authStore.isAdmin" label="操作" width="90"><template #default="{ row }"><el-button link type="danger" @click="removeByKey(row.objectKey)">删除</el-button></template></el-table-column>
      </el-table>
    </section>
  </div>
</template>

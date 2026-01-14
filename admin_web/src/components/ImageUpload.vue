<template>
  <div class="image-upload">
    <el-upload
      class="avatar-uploader"
      :action="uploadAction"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :http-request="handleUpload"
      :disabled="disabled"
    >
      <img v-if="imageUrl" :src="imageUrl" class="avatar" />
      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
    </el-upload>
    <div v-if="tip" class="upload-tip">{{ tip }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { uploadUserAvatar, uploadProductImage, uploadSystemImage } from '@/api/upload'

interface Props {
  modelValue?: string
  type: 'user' | 'product' | 'system'
  tip?: string
  disabled?: boolean
  maxSize?: number // MB
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  tip: '建议上传JPG/PNG格式图片，大小不超过2MB',
  disabled: false,
  maxSize: 2
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const imageUrl = ref(props.modelValue)
const uploadAction = ref('#') // 占位符，实际使用自定义上传

watch(
  () => props.modelValue,
  (newValue) => {
    imageUrl.value = newValue
  }
)

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < props.maxSize

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB!`)
    return false
  }
  return true
}

const handleUpload = async (options: UploadRequestOptions) => {
  const file = options.file as File

  try {
    let res
    switch (props.type) {
      case 'user':
        res = await uploadUserAvatar(file)
        break
      case 'product':
        res = await uploadProductImage(file)
        break
      case 'system':
        res = await uploadSystemImage(file)
        break
    }

    if (res.code === 200 || res.code === 0) {
      imageUrl.value = res.data.url
      emit('update:modelValue', res.data.url)
      ElMessage.success('上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败，请重试')
  }
}
</script>

<style scoped>
.image-upload {
  display: inline-block;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  line-height: 1.5;
}
</style>

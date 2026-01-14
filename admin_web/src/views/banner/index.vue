<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">轮播图管理</h2>
      <el-button type="primary" @click="handleAdd">添加轮播图</el-button>
    </div>

    <el-card shadow="hover">
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.bannerType === 2 ? 'warning' : 'success'">
              {{ row.bannerType === 2 ? '视频' : '图片' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预览" width="200">
          <template #default="{ row }">
            <el-image
              v-if="row.bannerType === 1"
              :src="getImageUrl(row.image)"
              :preview-src-list="[getImageUrl(row.image)]"
              style="width: 180px; height: 90px"
              fit="cover"
            />
            <video
              v-else
              :src="getImageUrl(row.videoUrl)"
              style="width: 180px; height: 90px; object-fit: cover"
              controls
            />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="link" label="跳转链接" width="200" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑轮播图' : '添加轮播图'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="类型" prop="bannerType">
          <el-radio-group v-model="form.bannerType" @change="handleTypeChange">
            <el-radio :value="1">图片</el-radio>
            <el-radio :value="2">视频</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>

        <el-form-item v-if="form.bannerType === 1" label="图片" prop="image">
          <el-upload
            class="banner-uploader"
            :show-file-list="false"
            :http-request="handleImageUpload"
            accept="image/*"
          >
            <img v-if="form.image" :src="getImageUrl(form.image)" class="banner" />
            <el-icon v-else class="banner-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div style="color: #999; font-size: 12px; margin-top: 5px">建议尺寸：750x375</div>
        </el-form-item>

        <el-form-item v-if="form.bannerType === 2" label="视频" prop="videoUrl">
          <el-upload
            class="banner-uploader"
            :show-file-list="false"
            :http-request="handleVideoUpload"
            accept="video/*"
          >
            <video v-if="form.videoUrl" :src="getImageUrl(form.videoUrl)" class="banner" controls />
            <el-icon v-else class="banner-uploader-icon"><VideoCamera /></el-icon>
          </el-upload>
          <div style="color: #999; font-size: 12px; margin-top: 5px">
            支持mp4、webm等格式，建议大小不超过100MB
          </div>
        </el-form-item>

        <el-form-item v-if="form.bannerType === 2" label="封面图" prop="image">
          <el-upload
            class="cover-uploader"
            :show-file-list="false"
            :http-request="handleImageUpload"
            accept="image/*"
          >
            <img v-if="form.image" :src="getImageUrl(form.image)" class="cover" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div style="color: #999; font-size: 12px; margin-top: 5px">视频封面图，建议尺寸：750x375</div>
        </el-form-item>

        <el-form-item label="跳转链接">
          <el-input v-model="form.link" placeholder="请输入跳转链接（选填）" />
        </el-form-item>

        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getBannerList, createBanner, updateBanner, deleteBanner } from '@/api/other'
import { uploadSystemImage, uploadSystemVideo } from '@/api/upload'
import { formatDate, getImageUrl } from '@/utils'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, VideoCamera } from '@element-plus/icons-vue'
import type { Banner } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<Banner[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = reactive<Partial<Banner & { bannerType: number; videoUrl: string }>>({
  title: '',
  image: '',
  videoUrl: '',
  bannerType: 1,
  link: '',
  sort: 0,
  status: 1
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  bannerType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  image: [
    {
      validator: (_rule, _value, callback) => {
        if (form.bannerType === 1 && !form.image) {
          callback(new Error('请上传图片'))
        } else if (form.bannerType === 2 && !form.image) {
          callback(new Error('请上传封面图'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  videoUrl: [
    {
      validator: (_rule, _value, callback) => {
        if (form.bannerType === 2 && !form.videoUrl) {
          callback(new Error('请上传视频'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getBannerList()
    if (res.code === 200 || res.code === 0) {
      tableData.value = (res.data || []).map((item: any) => ({
        ...item,
        image: item.imageUrl || item.image,
        link: item.linkUrl || item.linkValue || item.link,
        bannerType: item.bannerType || 1
      }))
    }
  } catch (error) {
    console.error('加载轮播图列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 添加
const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    title: '',
    image: '',
    videoUrl: '',
    bannerType: 1,
    link: '',
    sort: 0,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Banner) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 类型切换
const handleTypeChange = () => {
  // 清空上传的文件
  form.image = ''
  form.videoUrl = ''
}

// 上传图片
const handleImageUpload = async (options: any) => {
  try {
    const res = await uploadSystemImage(options.file)
    if (res.code === 200 || res.code === 0) {
      form.image = res.data.url
      ElMessage.success('上传成功')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败，请重试')
  }
}

// 上传视频
const handleVideoUpload = async (options: any) => {
  try {
    ElMessage.info('视频上传中，请稍候...')
    const res = await uploadSystemVideo(options.file)
    if (res.code === 200 || res.code === 0) {
      form.videoUrl = res.data.url
      ElMessage.success('视频上传成功')
    }
  } catch (error: any) {
    console.error('上传失败:', error)
    ElMessage.error(error.response?.data?.message || '上传失败，请重试')
  }
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const submitData = {
        ...form,
        imageUrl: form.image,
        linkValue: form.link,
        linkType: form.link ? 3 : 4
      }

      if (isEdit.value) {
        await updateBanner(form.id!, submitData)
        ElMessage.success('更新成功')
      } else {
        await createBanner(submitData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 删除
const handleDelete = async (row: Banner) => {
  try {
    await ElMessageBox.confirm('确定要删除该轮播图吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteBanner(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.banner-uploader .banner {
  width: 400px;
  height: 200px;
  display: block;
  object-fit: cover;
}

.banner-uploader .banner-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 400px;
  height: 200px;
  text-align: center;
  line-height: 200px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  transition: border-color 0.3s;
}

.banner-uploader .banner-uploader-icon:hover {
  border-color: #409eff;
}

.cover-uploader .cover {
  width: 200px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.cover-uploader .cover-uploader-icon {
  font-size: 24px;
  color: #8c939d;
  width: 200px;
  height: 100px;
  text-align: center;
  line-height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  transition: border-color 0.3s;
}

.cover-uploader .cover-uploader-icon:hover {
  border-color: #409eff;
}
</style>

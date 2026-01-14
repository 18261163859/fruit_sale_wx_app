<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">新年主题配置</h2>
      <p class="page-desc">可针对普通用户和VIP用户分别设置是否启用新年主题</p>
    </div>

    <el-card shadow="hover">
      <el-form :model="form" label-width="140px" style="max-width: 600px">
        <el-form-item label="普通用户新年主题">
          <el-switch
            v-model="form.normalUserEnabled"
            active-text="启用"
            inactive-text="停用"
          />
          <div class="hint-text">启用后，普通用户将看到红金配色的新年主题</div>
        </el-form-item>

        <el-form-item label="VIP用户新年主题">
          <el-switch
            v-model="form.vipUserEnabled"
            active-text="启用"
            inactive-text="停用"
          />
          <div class="hint-text">启用后，VIP用户（星享会员/代理）将看到红金配色的新年主题</div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存配置</el-button>
          <el-button @click="loadConfig">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 主题预览 -->
    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>
        <span>主题预览</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="theme-preview normal-theme">
            <h4>普通会员主题</h4>
            <div class="preview-card">
              <div class="card-header" style="background: #2563eb">
                <span>蓝白配色</span>
              </div>
              <div class="card-body">
                <div class="tag" style="background: #dbeafe; color: #2563eb">主题标签</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="theme-preview vip-theme">
            <h4>星享会员主题</h4>
            <div class="preview-card">
              <div class="card-header" style="background: #000000">
                <span>黑金配色</span>
              </div>
              <div class="card-body">
                <div class="tag" style="background: #fbbf24; color: #000">主题标签</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="theme-preview newyear-theme">
            <h4>新年主题</h4>
            <div class="preview-card">
              <div class="card-header" style="background: #dc2626">
                <span>红金配色</span>
              </div>
              <div class="card-body">
                <div class="tag" style="background: #fbbf24; color: #b91c1c">主题标签</div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const form = ref({
  normalUserEnabled: false,
  vipUserEnabled: false
})

const saving = ref(false)

// 加载配置
const loadConfig = async () => {
  try {
    const res = await request.get('/admin/config/newyear-theme')
    if (res.data) {
      form.value.normalUserEnabled = res.data.normalUserNewYearEnabled || false
      form.value.vipUserEnabled = res.data.vipUserNewYearEnabled || false
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载配置失败')
  }
}

// 保存配置
const handleSave = async () => {
  try {
    saving.value = true
    await request.put('/admin/config/newyear-theme', form.value)
    ElMessage.success('保存成功')
    await loadConfig()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadConfig()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
}

.page-desc {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.hint-text {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.theme-preview {
  text-align: center;
}

.theme-preview h4 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #303133;
}

.preview-card {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
}

.card-header {
  padding: 16px;
  color: #fff;
  font-weight: 600;
}

.card-body {
  padding: 16px;
  background: #fff;
}

.tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}
</style>

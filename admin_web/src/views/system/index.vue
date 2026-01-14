<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">系统配置</h2>
    </div>

    <el-card v-loading="loading" shadow="hover">
      <el-form ref="formRef" :model="form" label-width="150px" style="max-width: 800px">
        <el-divider content-position="left">基础配置</el-divider>

        <el-form-item label="VIP会员价格">
          <el-input-number v-model="form.vipPrice" :min="0" :precision="2" />
          <span style="margin-left: 10px; color: #999">元/年</span>
        </el-form-item>

        <el-form-item label="VIP折扣">
          <el-input-number v-model="form.vipDiscount" :min="0" :max="10" :precision="1" />
          <span style="margin-left: 10px; color: #999">折</span>
        </el-form-item>

        <el-form-item label="基础运费">
          <el-input-number v-model="form.default_freight" :min="0" :precision="2" />
          <span style="margin-left: 10px; color: #999">元</span>
        </el-form-item>

        <el-form-item label="包邮金额">
          <el-input-number v-model="form.free_freight_threshold" :min="0" :precision="2" />
          <span style="margin-left: 10px; color: #999">元（订单满此金额包邮）</span>
        </el-form-item>

        <el-divider content-position="left">积分配置</el-divider>

        <el-form-item label="消费积分比例">
          <el-input-number v-model="form.pointsRate" :min="0" :max="100" :precision="0" />
          <span style="margin-left: 10px; color: #999">%（消费1元获得积分）</span>
        </el-form-item>

        <el-form-item label="积分抵扣比例">
          <el-input-number v-model="form.pointsDeductionRate" :min="0" :max="100" :precision="0" />
          <span style="margin-left: 10px; color: #999">积分抵扣1元</span>
        </el-form-item>

        <el-form-item label="最大积分抵扣比例">
          <el-input-number v-model="form.maxPointsDeductionRate" :min="0" :max="100" :precision="0" />
          <span style="margin-left: 10px; color: #999">%（订单最多可用积分抵扣比例）</span>
        </el-form-item>

        <el-divider content-position="left">联系方式</el-divider>

        <el-form-item label="客服电话">
          <el-input v-model="form.servicePhone" placeholder="请输入客服电话" style="width: 300px" />
        </el-form-item>

        <el-form-item label="客服微信">
          <el-input v-model="form.serviceWechat" placeholder="请输入客服微信" style="width: 300px" />
        </el-form-item>

        <el-form-item label="客服邮箱">
          <el-input v-model="form.serviceEmail" placeholder="请输入客服邮箱" style="width: 300px" />
        </el-form-item>

        <el-divider content-position="left">其他配置</el-divider>

        <el-form-item label="网站名称">
          <el-input v-model="form.siteName" placeholder="请输入网站名称" style="width: 300px" />
        </el-form-item>

        <el-form-item label="网站公告">
          <el-input
            v-model="form.siteNotice"
            type="textarea"
            :rows="4"
            placeholder="请输入网站公告"
            style="width: 600px"
          />
        </el-form-item>

        <el-divider content-position="left">主题配置</el-divider>

        <el-form-item label="普通用户新年主题">
          <el-switch
            v-model="themeConfig.normalUserEnabled"
            active-text="启用"
            inactive-text="停用"
          />
          <div style="margin-top: 8px; font-size: 12px; color: #999">
            启用后，普通用户将看到红金配色的新年主题
          </div>
        </el-form-item>

        <el-form-item label="VIP用户新年主题">
          <el-switch
            v-model="themeConfig.vipUserEnabled"
            active-text="启用"
            inactive-text="停用"
          />
          <div style="margin-top: 8px; font-size: 12px; color: #999">
            启用后，VIP用户（星享会员/代理）将看到红金配色的新年主题
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getSystemConfig, updateSystemConfig } from '@/api/other'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)

const form = reactive({
  // 基础配置
  vipPrice: 99,
  vipDiscount: 9.5,
  default_freight: 15,
  free_freight_threshold: 199,
  // 积分配置
  pointsRate: 1,
  pointsDeductionRate: 100,
  maxPointsDeductionRate: 30,
  // 联系方式
  servicePhone: '',
  serviceWechat: '',
  serviceEmail: '',
  // 其他配置
  siteName: '水果商城',
  siteNotice: ''
})

// 主题配置
const themeConfig = reactive({
  normalUserEnabled: false,
  vipUserEnabled: false
})

// 加载主题配置
const loadThemeConfig = async () => {
  try {
    const res = await request.get('/admin/config/newyear-theme')
    if (res.code === 200 && res.data) {
      themeConfig.normalUserEnabled = res.data.normalUserNewYearEnabled || false
      themeConfig.vipUserEnabled = res.data.vipUserNewYearEnabled || false
    }
  } catch (error) {
    console.error('加载主题配置失败:', error)
  }
}

// 加载配置
const loadConfig = async () => {
  loading.value = true
  try {
    const res = await getSystemConfig()
    if (res.code === 200 || res.code === 0) {
      const configs = res.data
      configs.forEach((item: any) => {
        if (item.configKey in form) {
          const value = item.configValue
          // 根据类型转换
          if (item.configType === 'number') {
            (form as any)[item.configKey] = Number(value)
          } else {
            (form as any)[item.configKey] = value
          }
        }
      })
    }
  } catch (error) {
    console.error('加载配置失败:', error)
  } finally {
    loading.value = false
  }
}

// 提交配置
const handleSubmit = async () => {
  submitLoading.value = true
  try {
    // 保存系统配置
    await updateSystemConfig(form)

    // 保存主题配置
    await request.put('/admin/config/newyear-theme', themeConfig)

    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadConfig()
  loadThemeConfig()
})
</script>
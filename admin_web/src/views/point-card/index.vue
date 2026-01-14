<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">积分卡管理</h2>
      <div style="display: flex; gap: 10px">
        <el-button type="primary" @click="generateVisible = true">批量生成</el-button>
        <el-button type="success" @click="handleExport">导出兑换码</el-button>
      </div>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="搜索">
          <el-input v-model="searchForm.keyword" placeholder="请输入兑换码" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="未使用" :value="0" />
            <el-option label="已使用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 积分卡列表 -->
    <el-card shadow="hover">
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="cardCode" label="兑换码" width="200">
          <template #default="{ row }">
            <span style="font-family: monospace; font-weight: bold">{{ row.cardCode }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="integralAmount" label="积分" width="100" />
        <el-table-column prop="cardStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.cardStatus === 0 ? 'success' : 'info'">
              {{ row.cardStatus === 0 ? '未使用' : '已使用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="使用用户" width="120">
          <template #default="{ row }">
            {{ row.username || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="useTime" label="使用时间" width="180">
          <template #default="{ row }">
            {{ row.useTime ? formatDate(row.useTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="生成时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <!-- 批量生成对话框 -->
    <el-dialog v-model="generateVisible" title="批量生成积分卡" width="500px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="生成数量">
          <el-input-number v-model="generateForm.count" :min="1" :max="1000" style="width: 100%" />
          <div style="color: #999; font-size: 12px; margin-top: 5px">最多可生成1000张</div>
        </el-form-item>
        <el-form-item label="积分额度">
          <el-input-number v-model="generateForm.points" :min="1" :max="10000" style="width: 100%" />
          <div style="color: #999; font-size: 12px; margin-top: 5px">每张卡的积分额度</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateVisible = false">取消</el-button>
        <el-button type="primary" :loading="generateLoading" @click="handleGenerate">确定生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getPointCardList, generatePointCards, exportPointCards } from '@/api/other'
import { formatDate, downloadFile } from '@/utils'
import { ElMessage } from 'element-plus'
import type { PointCard } from '@/types'

const loading = ref(false)
const generateLoading = ref(false)
const tableData = ref<PointCard[]>([])
const generateVisible = ref(false)

const searchForm = reactive({
  keyword: '',
  status: undefined
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const generateForm = reactive({
  count: 10,
  points: 100
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getPointCardList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    if (res.code === 200 || res.code === 0) {
      tableData.value = res.data.records || res.data.list || []
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载积分卡列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = undefined
  pagination.page = 1
  loadData()
}

// 批量生成
const handleGenerate = async () => {
  if (generateForm.count < 1 || generateForm.count > 1000) {
    ElMessage.warning('生成数量必须在1-1000之间')
    return
  }

  if (generateForm.points < 1) {
    ElMessage.warning('积分额度必须大于0')
    return
  }

  generateLoading.value = true
  try {
    await generatePointCards(generateForm)
    ElMessage.success(`成功生成${generateForm.count}张积分卡`)
    generateVisible.value = false
    loadData()
  } catch (error) {
    console.error('生成积分卡失败:', error)
  } finally {
    generateLoading.value = false
  }
}

// 导出
const handleExport = async () => {
  try {
    const res = await exportPointCards({ status: 0 })
    downloadFile(res.data, `积分卡兑换码_${new Date().getTime()}.xlsx`)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>
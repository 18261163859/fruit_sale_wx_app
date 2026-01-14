<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">财务管理</h2>
      <el-button type="success" @click="handleExport">导出报表</el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :xs="24" :sm="12" :lg="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="icon" style="color: #67c23a"><Money /></el-icon>
            <div class="value">{{ formatMoney(statistics.totalIncome) }}</div>
            <div class="label">总收入</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="icon" style="color: #f56c6c"><CreditCard /></el-icon>
            <div class="value">{{ formatMoney(statistics.totalCashback) }}</div>
            <div class="label">总返现</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="icon" style="color: #409eff"><Wallet /></el-icon>
            <div class="value">{{ formatMoney(statistics.totalWithdraw) }}</div>
            <div class="label">总提现</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="交易类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 120px">
            <el-option label="订单收入" :value="1" />
            <el-option label="代理返现" :value="2" />
            <el-option label="会员收入" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 财务记录列表 -->
    <el-card shadow="hover">
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="type" label="交易类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span :style="{ color: row.type === 2 || row.type === 4 ? '#f56c6c' : '#67c23a' }">
              {{ row.type === 2 || row.type === 4 ? '-' : '+' }}{{ formatMoney(row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="120">
          <template #default="{ row }">
            {{ row.balance ? formatMoney(row.balance) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="关联订单号" width="180">
          <template #default="{ row }">
            {{ row.orderNo || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150">
          <template #default="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="交易时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { getFinanceRecords, getFinanceStatistics, exportFinanceRecords } from '@/api/other'
import { formatMoney, formatDate, downloadFile } from '@/utils'
import { Money, CreditCard, Wallet } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { FinanceRecord } from '@/types'

const loading = ref(false)
const tableData = ref<FinanceRecord[]>([])
const dateRange = ref<string[]>([])

const statistics = ref({
  totalIncome: 0,
  totalCashback: 0,
  totalWithdraw: 0
})

const searchForm = reactive({
  type: undefined
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const searchParams = computed(() => ({
  ...searchForm,
  startDate: dateRange.value?.[0],
  endDate: dateRange.value?.[1]
}))

// 获取交易类型名称
const getTypeName = (type: number) => {
  const typeMap: Record<number, string> = {
    1: '订单收入',
    2: '代理返现',
    3: '会员收入'
  }
  return typeMap[type] || '未知'
}

// 获取交易类型标签类型
const getTypeTagType = (type: number): 'success' | 'danger' | 'warning' | 'info' => {
  const typeMap: Record<number, 'success' | 'danger' | 'warning' | 'info'> = {
    1: 'success',
    2: 'warning',
    3: 'success'
  }
  return typeMap[type] || 'info'
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getFinanceStatistics(searchParams.value)
    if (res.code === 200 || res.code === 0) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载财务记录
const loadData = async () => {
  loading.value = true
  try {
    const res = await getFinanceRecords({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...searchParams.value
    })
    if (res.code === 200 || res.code === 0) {
      tableData.value = res.data.records || res.data.list || []
      pagination.total = res.data.total
    }
    loadStatistics()
  } catch (error) {
    console.error('加载财务记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.type = undefined
  dateRange.value = []
  pagination.page = 1
  loadData()
}

// 导出报表
const handleExport = async () => {
  try {
    const res = await exportFinanceRecords(searchParams.value)
    downloadFile(res.data, `财务报表_${new Date().getTime()}.xlsx`)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>
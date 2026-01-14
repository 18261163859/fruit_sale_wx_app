<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">数据总览</h2>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="icon" style="color: #409eff"><ShoppingCart /></el-icon>
            <div class="value">{{ statistics.totalOrders }}</div>
            <div class="label">总订单数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="icon" style="color: #67c23a"><Money /></el-icon>
            <div class="value">{{ formatMoney(statistics.totalSales) }}</div>
            <div class="label">总销售额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="icon" style="color: #e6a23c"><User /></el-icon>
            <div class="value">{{ statistics.totalUsers }}</div>
            <div class="label">总用户数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="icon" style="color: #f56c6c"><UserFilled /></el-icon>
            <div class="value">{{ statistics.totalAgents }}</div>
            <div class="label">总代理数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 销售趋势图 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>销售趋势</span>
          <el-radio-group v-model="trendDays" size="small" @change="loadSalesTrend">
            <el-radio-button :value="7">近7天</el-radio-button>
            <el-radio-button :value="30">近30天</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="chartRef" style="width: 100%; height: 400px"></div>
    </el-card>

    <!-- 最近订单 -->
    <el-card shadow="hover">
      <template #header>
        <span>最近订单</span>
      </template>
      <el-table :data="recentOrders" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column label="用户" width="120">
          <template #default="{ row }">
            {{ row.receiverName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="商品" min-width="150">
          <template #default="{ row }">
            <div v-if="row.items && row.items.length > 0">
              {{ row.items.map((item: any) => item.productName).join(', ') }}
            </div>
            <div v-else>-</div>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.actualAmount || row.totalAmount || 0) }}
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.orderStatus)">{{ row.orderStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { getStatistics, getSalesTrend, getRecentOrders } from '@/api/dashboard'
import { formatMoney, formatDate } from '@/utils'
import { ShoppingCart, Money, User, UserFilled } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import type { Statistics } from '@/types'

const statistics = ref<Statistics>({
  totalOrders: 0,
  totalSales: 0,
  totalUsers: 0,
  totalAgents: 0,
  todayOrders: 0,
  todaySales: 0,
  todayUsers: 0
})

const trendDays = ref(7)
const recentOrders = ref<any[]>([])
const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

// 获取订单状态标签类型
const getOrderStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    '待付款': 'warning',
    '待发货': 'info',
    '已发货': 'primary',
    '待收货': 'primary',
    '已完成': 'success',
    '已取消': 'danger'
  }
  return statusMap[status] || 'info'
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getStatistics()
    if (res.code === 200 || res.code === 0) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载销售趋势
const loadSalesTrend = async () => {
  try {
    const res = await getSalesTrend(trendDays.value)
    if (res.code === 200 || res.code === 0) {
      renderChart(res.data)
    }
  } catch (error) {
    console.error('加载销售趋势失败:', error)
  }
}

// 加载最近订单
const loadRecentOrders = async () => {
  try {
    const res = await getRecentOrders(10)
    if (res.code === 200 || res.code === 0) {
      recentOrders.value = res.data
    }
  } catch (error) {
    console.error('加载最近订单失败:', error)
  }
}

// 渲染图表
const renderChart = (data: any[]) => {
  if (!chartRef.value) return

  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['销售额', '订单数']
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.date)
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额',
        position: 'left'
      },
      {
        type: 'value',
        name: '订单数',
        position: 'right'
      }
    ],
    series: [
      {
        name: '销售额',
        type: 'line',
        data: data.map(item => item.amount),
        smooth: true,
        itemStyle: {
          color: '#67c23a'
        }
      },
      {
        name: '订单数',
        type: 'bar',
        yAxisIndex: 1,
        data: data.map(item => item.count),
        itemStyle: {
          color: '#409eff'
        }
      }
    ]
  }

  chartInstance.setOption(option)
}

onMounted(() => {
  loadStatistics()
  loadSalesTrend()
  loadRecentOrders()

  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
})

onBeforeUnmount(() => {
  chartInstance?.dispose()
  window.removeEventListener('resize', () => {
    chartInstance?.resize()
  })
})
</script>
<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">订单管理</h2>
      <el-button type="success" @click="handleExport">导出订单</el-button>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="搜索">
          <el-input v-model="searchForm.keyword" placeholder="请输入订单号/用户名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
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

    <!-- 订单列表 -->
    <el-card shadow="hover">
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="receiverName" label="收货人" width="120" />
        <el-table-column prop="receiverPhone" label="手机号" width="120" />
        <el-table-column label="商品" min-width="200">
          <template #default="{ row }">
            <div v-if="row.items && row.items.length > 0" style="display: flex; flex-direction: column; gap: 8px">
              <div v-for="item in row.items" :key="item.productId" style="display: flex; align-items: center; gap: 10px">
                <el-image
                  v-if="item.productImage"
                  :src="getImageUrl(item.productImage)"
                  :preview-src-list="[getImageUrl(item.productImage)]"
                  class="image-preview"
                  fit="cover"
                  style="width: 50px; height: 50px; border-radius: 4px;"
                />
                <div>
                  <div>{{ item.productName }}</div>
                  <div style="font-size: 12px; color: #999;">数量: {{ item.quantity }}</div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="总金额" width="100">
          <template #default="{ row }">
            {{ formatMoney(row.payAmount || row.totalAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.orderStatus)">{{ getStatusText(row.orderStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="info" size="small" @click="handleViewDetail(row)">详情</el-button>
              <el-button v-if="getStatusCode(row.orderStatus) === 1" type="primary" size="small" @click="handleShip(row)">
                发货
              </el-button>
              <el-button v-if="getStatusCode(row.orderStatus) === 2" type="success" size="small" @click="handleFinish(row)">
                确认完成
              </el-button>
            </div>
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

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions v-if="currentOrder" :column="2" border>
        <el-descriptions-item label="订单号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">
          {{ [currentOrder.receiverProvince, currentOrder.receiverCity, currentOrder.receiverDistrict, currentOrder.receiverAddress].filter(Boolean).join('') }}
        </el-descriptions-item>

        <el-descriptions-item label="商品列表" :span="2">
          <div v-if="currentOrder.items && currentOrder.items.length > 0" style="display: flex; flex-direction: column; gap: 12px">
            <div v-for="item in currentOrder.items" :key="item.productId" style="display: flex; align-items: center; gap: 10px; padding: 8px; border: 1px solid #eee; border-radius: 4px;">
              <el-image
                v-if="item.productImage"
                :src="getImageUrl(item.productImage)"
                style="width: 60px; height: 60px; border-radius: 4px;"
                fit="cover"
              />
              <div style="flex: 1;">
                <div style="font-weight: bold;">{{ item.productName }}</div>
                <div style="font-size: 12px; color: #999;">单价: {{ formatMoney(item.productPrice) }} × {{ item.quantity }}</div>
              </div>
              <div style="font-weight: bold; color: #ff6b35;">{{ formatMoney(item.subtotalAmount) }}</div>
            </div>
          </div>
        </el-descriptions-item>

        <el-descriptions-item label="商品总额">{{ formatMoney(currentOrder.totalAmount) }}</el-descriptions-item>
        <el-descriptions-item label="折扣金额" v-if="currentOrder.discountAmount">{{ formatMoney(currentOrder.discountAmount) }}</el-descriptions-item>
        <el-descriptions-item label="积分抵扣" v-if="currentOrder.integralAmount">{{ formatMoney(currentOrder.integralAmount) }}</el-descriptions-item>
        <el-descriptions-item label="实付金额" :span="2">
          <span style="font-size: 18px; font-weight: bold; color: #ff6b35;">{{ formatMoney(currentOrder.payAmount || currentOrder.totalAmount) }}</span>
        </el-descriptions-item>

        <el-descriptions-item v-if="currentOrder.expressCompany" label="物流公司">
          {{ currentOrder.expressCompany }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.expressNo" label="物流单号">
          {{ currentOrder.expressNo }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.shipRemark" label="发货备注" :span="2">
          {{ currentOrder.shipRemark }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.packageBeforeImage || currentOrder.packageAfterImage" label="发货照片" :span="2">
          <div style="display: flex; gap: 10px; flex-wrap: wrap;">
            <div v-if="currentOrder.packageBeforeImage" style="text-align: center;">
              <div style="margin-bottom: 8px; font-size: 12px; color: #666;">包装前</div>
              <el-image
                :src="getImageUrl(currentOrder.packageBeforeImage)"
                :preview-src-list="[getImageUrl(currentOrder.packageBeforeImage)]"
                style="width: 150px; height: 150px; border-radius: 8px; cursor: pointer;"
                fit="cover"
              />
            </div>
            <div v-if="currentOrder.packageAfterImage" style="text-align: center;">
              <div style="margin-bottom: 8px; font-size: 12px; color: #666;">包装后</div>
              <el-image
                :src="getImageUrl(currentOrder.packageAfterImage)"
                :preview-src-list="[getImageUrl(currentOrder.packageAfterImage)]"
                style="width: 150px; height: 150px; border-radius: 8px; cursor: pointer;"
                fit="cover"
              />
            </div>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态" :span="2">
          <el-tag :type="getStatusType(currentOrder.orderStatus)">{{ getStatusText(currentOrder.orderStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下单时间" :span="2">{{ formatDate(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.payTime" label="付款时间" :span="2">
          {{ formatDate(currentOrder.payTime) }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.shipTime" label="发货时间" :span="2">
          {{ formatDate(currentOrder.shipTime) }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.completeTime" label="完成时间" :span="2">
          {{ formatDate(currentOrder.completeTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 发货对话框 -->
    <el-dialog v-model="shipVisible" title="发货" width="500px">
      <el-form ref="shipFormRef" :model="shipForm" :rules="shipRules" label-width="100px">
        <el-form-item label="物流公司" prop="expressCompany">
          <el-input v-model="shipForm.expressCompany" placeholder="请输入物流公司" />
        </el-form-item>
        <el-form-item label="物流单号" prop="expressNo">
          <el-input v-model="shipForm.expressNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleShipSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { getOrderList, getOrderDetail, shipOrder, finishOrder, exportOrders } from '@/api/order'
import { formatMoney, formatDate, getImageUrl, downloadFile } from '@/utils'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import type { Order } from '@/types'

// 订单状态枚举映射
const ORDER_STATUS_MAP = {
  'PENDING_PAYMENT': { code: 0, text: '待付款', type: 'warning' },
  'PENDING_SHIPMENT': { code: 1, text: '待发货', type: 'info' },
  'SHIPPED': { code: 2, text: '待收货', type: 'primary' },
  'COMPLETED': { code: 3, text: '已完成', type: 'success' },
  'CANCELLED': { code: 4, text: '已取消', type: 'danger' },
  '待付款': { code: 0, text: '待付款', type: 'warning' },
  '待发货': { code: 1, text: '待发货', type: 'info' },
  '已发货': { code: 2, text: '待收货', type: 'primary' },
  '已完成': { code: 3, text: '已完成', type: 'success' },
  '已取消': { code: 4, text: '已取消', type: 'danger' }
}

// 获取状态码
const getStatusCode = (status: any): number => {
  if (typeof status === 'number') return status
  if (typeof status === 'string') {
    return ORDER_STATUS_MAP[status]?.code ?? -1
  }
  return -1
}

// 获取状态文本
const getStatusText = (status: any): string => {
  if (typeof status === 'number') {
    const entry = Object.values(ORDER_STATUS_MAP).find(s => s.code === status)
    return entry?.text ?? '未知'
  }
  if (typeof status === 'string') {
    return ORDER_STATUS_MAP[status]?.text ?? status
  }
  return '未知'
}

// 获取状态类型
const getStatusType = (status: any): string => {
  if (typeof status === 'number') {
    const entry = Object.values(ORDER_STATUS_MAP).find(s => s.code === status)
    return entry?.type ?? 'info'
  }
  if (typeof status === 'string') {
    return ORDER_STATUS_MAP[status]?.type ?? 'info'
  }
  return 'info'
}

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<Order[]>([])
const detailVisible = ref(false)
const shipVisible = ref(false)
const currentOrder = ref<Order | null>(null)
const shipFormRef = ref<FormInstance>()
const dateRange = ref<string[]>([])

const searchForm = reactive({
  keyword: '',
  status: undefined
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const shipForm = reactive({
  expressCompany: '',
  expressNo: ''
})

const shipRules: FormRules = {
  expressCompany: [{ required: true, message: '请输入物流公司', trigger: 'blur' }],
  expressNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
}

const searchParams = computed(() => ({
  ...searchForm,
  startDate: dateRange.value?.[0],
  endDate: dateRange.value?.[1]
}))

// 加载订单列表
const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrderList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...searchParams.value
    })
    if (res.code === 200 || res.code === 0) {
      // Backend returns records in PageResult
      tableData.value = res.data.records || res.data.list || []
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = undefined
  dateRange.value = []
  pagination.page = 1
  loadData()
}

// 查看详情
const handleViewDetail = async (row: Order) => {
  try {
    const res = await getOrderDetail(row.id)
    if (res.code === 200 || res.code === 0) {
      currentOrder.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
  }
}

// 发货
const handleShip = (row: Order) => {
  currentOrder.value = row
  shipForm.expressCompany = ''
  shipForm.expressNo = ''
  shipVisible.value = true
}

// 提交发货
const handleShipSubmit = async () => {
  if (!shipFormRef.value || !currentOrder.value) return

  await shipFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      await shipOrder(currentOrder.value!.id, shipForm)
      ElMessage.success('发货成功')
      shipVisible.value = false
      loadData()
    } catch (error) {
      console.error('发货失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 确认完成
const handleFinish = async (row: Order) => {
  try {
    await ElMessageBox.confirm('确定要完成该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await finishOrder(row.id)
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

// 导出订单
const handleExport = async () => {
  try {
    const res = await exportOrders(searchParams.value)
    downloadFile(res.data, `订单列表_${new Date().getTime()}.xlsx`)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>
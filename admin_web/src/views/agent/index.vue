<template>
  <div class="page-container">
    <el-tabs v-model="activeTab">
      <!-- 邀请二级代理审批 -->
      <el-tab-pane label="邀请二级代理审批" name="invite-applications">
        <div class="search-form">
          <el-form :inline="true" :model="inviteSearchForm">
            <el-form-item label="审批状态">
              <el-select v-model="inviteSearchForm.status" placeholder="全部" clearable style="width: 120px">
                <el-option label="待审核" :value="0" />
                <el-option label="已通过" :value="1" />
                <el-option label="已拒绝" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadInviteApplications">查询</el-button>
              <el-button @click="resetInviteSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-card shadow="hover">
          <el-table v-loading="inviteLoading" :data="inviteApplications" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column label="邀请人" width="180">
              <template #default="{ row }">
                <div>
                  <div>{{ row.inviterNickname }}</div>
                  <div style="font-size: 12px; color: #999">{{ row.inviterPhone }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="被邀请人" width="180">
              <template #default="{ row }">
                <div>
                  <div>{{ row.inviteeNickname || '未知' }}</div>
                  <div style="font-size: 12px; color: #999">{{ row.inviteePhone }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="commissionRate" label="返现比例" width="100">
              <template #default="{ row }">
                {{ row.commissionRate}}%
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
                <el-tag v-else-if="row.status === 1" type="success">已通过</el-tag>
                <el-tag v-else type="danger">已拒绝</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="reviewTime" label="审核时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.reviewTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="rejectReason" label="拒绝原因" min-width="150">
              <template #default="{ row }">
                {{ row.rejectReason || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <div v-if="row.status === 0" class="table-actions">
                  <el-button type="success" size="small" @click="handleInviteApprove(row, 1)">通过</el-button>
                  <el-button type="danger" size="small" @click="handleInviteApprove(row, 2)">拒绝</el-button>
                </div>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 代理列表 -->
      <el-tab-pane label="代理列表" name="agents">
        <div class="search-form">
          <el-form :inline="true" :model="agentSearchForm">
            <el-form-item label="搜索">
              <el-input v-model="agentSearchForm.keyword" placeholder="请输入用户名/手机号" clearable style="width: 200px" />
            </el-form-item>
            <el-form-item label="代理等级">
              <el-select v-model="agentSearchForm.level" placeholder="全部" clearable style="width: 120px">
                <el-option label="一级代理" :value="1" />
                <el-option label="二级代理" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadAgents">查询</el-button>
              <el-button @click="resetAgentSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-card shadow="hover">
          <el-table v-loading="agentLoading" :data="agents" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="userNo" label="用户编号" width="150" />
            <el-table-column prop="nickname" label="昵称" width="120" />
            <el-table-column prop="phone" label="手机号" width="120" />
            <el-table-column prop="agentLevel" label="代理等级" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.agentLevel" :type="row.agentLevel === 1 ? 'success' : 'warning'">
                  {{ AGENT_LEVEL[row.agentLevel] }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="inviteCode" label="邀请码" width="120" />
            <el-table-column prop="commissionRate" label="佣金比例" width="100">
              <template #default="{ row }">
                {{ (row.commissionRate * 100).toFixed(0) }}%
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="成为代理时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="agentPagination.page"
            v-model:page-size="agentPagination.pageSize"
            :total="agentPagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadAgents"
            @current-change="loadAgents"
          />
        </el-card>
      </el-tab-pane>

      <!-- 返现申请审批 -->
      <el-tab-pane label="返现申请审批" name="commission-applications">
        <div class="search-form">
          <el-form :inline="true" :model="commissionAppSearchForm">
            <el-form-item label="审批状态">
              <el-select v-model="commissionAppSearchForm.status" placeholder="全部" clearable style="width: 120px">
                <el-option label="待审核" :value="0" />
                <el-option label="已通过" :value="1" />
                <el-option label="已拒绝" :value="2" />
                <el-option label="已返现" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadCommissionApplications">查询</el-button>
              <el-button @click="resetCommissionAppSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-card shadow="hover">
          <el-table v-loading="commissionAppLoading" :data="commissionApplications" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column label="申请人" width="180">
              <template #default="{ row }">
                <div>
                  <div>{{ row.agentNickname || '未知' }}</div>
                  <div style="font-size: 12px; color: #999">ID: {{ row.agentId }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="commissionAmount" label="申请金额" width="120">
              <template #default="{ row }">
                <span style="color: #ff6b6b; font-weight: bold">
                  ¥{{ row.commissionAmount?.toFixed(2) || '0.00' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="银行信息" min-width="200">
              <template #default="{ row }">
                <div>
                  <div>{{ row.bankName }}</div>
                  <div style="font-size: 12px; color: #666">账号: {{ row.bankAccount }}</div>
                  <div style="font-size: 12px; color: #666">户名: {{ row.accountName }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="150">
              <template #default="{ row }">
                {{ row.remark || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
                <el-tag v-else-if="row.status === 1" type="success">已通过</el-tag>
                <el-tag v-else-if="row.status === 2" type="danger">已拒绝</el-tag>
                <el-tag v-else-if="row.status === 3" type="info">已返现</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="reviewTime" label="审核时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.reviewTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="transferTime" label="返现时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.transferTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="rejectReason" label="拒绝原因" min-width="150">
              <template #default="{ row }">
                {{ row.rejectReason || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="240" fixed="right">
              <template #default="{ row }">
                <div v-if="row.status === 0" class="table-actions">
                  <el-button type="success" size="small" @click="handleCommissionApprove(row, 1)">通过</el-button>
                  <el-button type="danger" size="small" @click="handleCommissionApprove(row, 2)">拒绝</el-button>
                </div>
                <div v-else-if="row.status === 1" class="table-actions">
                  <el-button type="primary" size="small" @click="handleMarkAsTransferred(row)">标记为已返现</el-button>
                </div>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 返现记录 -->
      <el-tab-pane label="返现记录" name="commission">
        <div class="search-form">
          <el-form :inline="true" :model="commissionSearchForm">
            <el-form-item label="代理ID">
              <el-input v-model="commissionSearchForm.agentId" placeholder="请输入代理用户ID" clearable style="width: 200px" type="number" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadCommissionRecords">查询</el-button>
              <el-button @click="resetCommissionSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-card shadow="hover">
          <el-table v-loading="commissionLoading" :data="commissionRecords" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="orderNo" label="订单编号" width="180" />
            <el-table-column prop="agentId" label="代理ID" width="100" />
            <el-table-column prop="agentLevel" label="代理级别" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.agentLevel === 3" type="success">一级代理</el-tag>
                <el-tag v-else-if="row.agentLevel === 4" type="warning">二级代理</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="orderAmount" label="订单金额" width="120">
              <template #default="{ row }">
                ¥{{ row.orderAmount?.toFixed(2) || '0.00' }}
              </template>
            </el-table-column>
            <el-table-column prop="commissionRate" label="返现比例" width="100">
              <template #default="{ row }">
                {{ (row.commissionRate * 100).toFixed(0) }}%
              </template>
            </el-table-column>
            <el-table-column prop="commissionAmount" label="返现金额" width="120">
              <template #default="{ row }">
                <span style="color: #67c23a; font-weight: bold">
                  ¥{{ row.commissionAmount?.toFixed(2) || '0.00' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.status === 0" type="warning">待结算</el-tag>
                <el-tag v-else-if="row.status === 1" type="success">已结算</el-tag>
                <el-tag v-else type="info">已取消</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="settleTime" label="结算时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.settleTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="commissionPagination.page"
            v-model:page-size="commissionPagination.pageSize"
            :total="commissionPagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadCommissionRecords"
            @current-change="loadCommissionRecords"
          />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 拒绝邀请申请对话框 -->
    <el-dialog v-model="inviteRejectVisible" title="拒绝邀请申请" width="500px">
      <el-form :model="inviteRejectForm" label-width="80px">
        <el-form-item label="拒绝理由">
          <el-input v-model="inviteRejectForm.reason" type="textarea" :rows="4" placeholder="请输入拒绝理由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inviteRejectVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmInviteReject">确定</el-button>
      </template>
    </el-dialog>

    <!-- 拒绝返现申请对话框 -->
    <el-dialog v-model="commissionRejectVisible" title="拒绝返现申请" width="500px">
      <el-form :model="commissionRejectForm" label-width="80px">
        <el-form-item label="拒绝理由">
          <el-input v-model="commissionRejectForm.reason" type="textarea" :rows="4" placeholder="请输入拒绝理由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="commissionRejectVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCommissionReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { getAgentList, getCommissionRecords, getInviteApplications, reviewInviteApplication, getCommissionApplications, reviewCommissionApplication, markAsTransferred } from '@/api/agent'
import { formatDate, AGENT_LEVEL } from '@/utils'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('invite-applications')
const agentLoading = ref(false)
const commissionLoading = ref(false)
const inviteLoading = ref(false)
const commissionAppLoading = ref(false)
const agents = ref<any[]>([])
const commissionRecords = ref<any[]>([])
const inviteApplications = ref<any[]>([])
const commissionApplications = ref<any[]>([])
const inviteRejectVisible = ref(false)
const commissionRejectVisible = ref(false)
const currentInviteApplication = ref<any>(null)
const currentCommissionApplication = ref<any>(null)

const agentSearchForm = reactive({
  keyword: '',
  level: undefined
})

const commissionSearchForm = reactive({
  agentId: ''
})

const inviteSearchForm = reactive({
  status: undefined
})

const commissionAppSearchForm = reactive({
  status: undefined
})

const agentPagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const commissionPagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const inviteRejectForm = reactive({
  reason: ''
})

const commissionRejectForm = reactive({
  reason: ''
})

// 加载代理列表
const loadAgents = async () => {
  agentLoading.value = true
  try {
    const res = await getAgentList({
      page: agentPagination.page,
      pageSize: agentPagination.pageSize,
      ...agentSearchForm
    })
    if (res.code === 200 || res.code === 0) {
      agents.value = res.data.records || res.data.list || []
      agentPagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载代理列表失败:', error)
  } finally {
    agentLoading.value = false
  }
}

// 加载返现记录
const loadCommissionRecords = async () => {
  commissionLoading.value = true
  try {
    const params: any = {
      current: commissionPagination.page,
      size: commissionPagination.pageSize
    }
    if (commissionSearchForm.agentId) {
      params.agentId = Number(commissionSearchForm.agentId)
    }
    const res = await getCommissionRecords(params)
    if (res.code === 200 || res.code === 0) {
      commissionRecords.value = res.data.records || res.data.list || []
      commissionPagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载返现记录失败:', error)
  } finally {
    commissionLoading.value = false
  }
}

const resetAgentSearch = () => {
  agentSearchForm.keyword = ''
  agentSearchForm.level = undefined
  agentPagination.page = 1
  loadAgents()
}

const resetCommissionSearch = () => {
  commissionSearchForm.agentId = ''
  commissionPagination.page = 1
  loadCommissionRecords()
}

// 加载邀请申请列表
const loadInviteApplications = async () => {
  inviteLoading.value = true
  try {
    const res = await getInviteApplications(inviteSearchForm.status)
    if (res.code === 200 || res.code === 0) {
      inviteApplications.value = res.data || []
    }
  } catch (error) {
    console.error('加载邀请申请列表失败:', error)
  } finally {
    inviteLoading.value = false
  }
}

// 重置邀请申请搜索
const resetInviteSearch = () => {
  inviteSearchForm.status = undefined
  loadInviteApplications()
}

// 审批邀请申请
const handleInviteApprove = async (row: any, status: number) => {
  if (status === 1) {
    try {
      await ElMessageBox.confirm('确定要通过该邀请申请吗？通过后被邀请人将成为二级代理。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      await reviewInviteApplication({
        applicationId: row.id,
        status: 1
      })
      ElMessage.success('审批成功')
      loadInviteApplications()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('审批失败:', error)
      }
    }
  } else {
    currentInviteApplication.value = row
    inviteRejectForm.reason = ''
    inviteRejectVisible.value = true
  }
}

// 确认拒绝邀请申请
const confirmInviteReject = async () => {
  if (!inviteRejectForm.reason.trim()) {
    ElMessage.warning('请输入拒绝理由')
    return
  }

  if (!currentInviteApplication.value) return

  try {
    await reviewInviteApplication({
      applicationId: currentInviteApplication.value.id,
      status: 2,
      rejectReason: inviteRejectForm.reason
    })
    ElMessage.success('审批成功')
    inviteRejectVisible.value = false
    loadInviteApplications()
  } catch (error) {
    console.error('审批失败:', error)
  }
}

// 加载返现申请列表
const loadCommissionApplications = async () => {
  commissionAppLoading.value = true
  try {
    const res = await getCommissionApplications(commissionAppSearchForm.status)
    if (res.code === 200 || res.code === 0) {
      commissionApplications.value = res.data || []
    }
  } catch (error) {
    console.error('加载返现申请列表失败:', error)
  } finally {
    commissionAppLoading.value = false
  }
}

// 重置返现申请搜索
const resetCommissionAppSearch = () => {
  commissionAppSearchForm.status = undefined
  loadCommissionApplications()
}

// 审批返现申请
const handleCommissionApprove = async (row: any, status: number) => {
  if (status === 1) {
    try {
      await ElMessageBox.confirm('确定要通过该返现申请吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      await reviewCommissionApplication({
        applicationId: row.id,
        status: 1
      })
      ElMessage.success('审批成功')
      loadCommissionApplications()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('审批失败:', error)
      }
    }
  } else {
    currentCommissionApplication.value = row
    commissionRejectForm.reason = ''
    commissionRejectVisible.value = true
  }
}

// 确认拒绝返现申请
const confirmCommissionReject = async () => {
  if (!commissionRejectForm.reason.trim()) {
    ElMessage.warning('请输入拒绝理由')
    return
  }

  if (!currentCommissionApplication.value) return

  try {
    await reviewCommissionApplication({
      applicationId: currentCommissionApplication.value.id,
      status: 2,
      rejectReason: commissionRejectForm.reason
    })
    ElMessage.success('审批成功')
    commissionRejectVisible.value = false
    loadCommissionApplications()
  } catch (error) {
    console.error('审批失败:', error)
  }
}

// 标记为已返现
const handleMarkAsTransferred = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要将该返现申请标记为已返现吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await markAsTransferred(row.id)
    ElMessage.success('已标记为已返现')
    loadCommissionApplications()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

watch(activeTab, (newVal) => {
  if (newVal === 'agents') {
    loadAgents()
  } else if (newVal === 'commission') {
    loadCommissionRecords()
  } else if (newVal === 'invite-applications') {
    loadInviteApplications()
  } else if (newVal === 'commission-applications') {
    loadCommissionApplications()
  }
})

onMounted(() => {
  loadInviteApplications()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.table-actions {
  display: flex;
  gap: 8px;
}

.image-preview {
  width: 60px;
  height: 40px;
  cursor: pointer;
}
</style>

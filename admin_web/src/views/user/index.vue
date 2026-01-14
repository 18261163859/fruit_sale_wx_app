<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="搜索">
          <el-input v-model="searchForm.keyword" placeholder="请输入用户名/手机号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="代理等级">
          <el-select v-model="searchForm.agentLevel" placeholder="全部" clearable style="width: 120px">
            <el-option label="普通用户" :value="0" />
            <el-option label="一级代理" :value="1" />
            <el-option label="二级代理" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="VIP等级">
          <el-select v-model="searchForm.vipLevel" placeholder="全部" clearable style="width: 120px">
            <el-option label="普通用户" :value="0" />
            <el-option label="VIP会员" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 用户列表 -->
    <el-card shadow="hover">
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userNo" label="用户编号" width="150" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="agentLevel" label="代理等级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.agentLevel > 0 ? 'success' : 'info'">
              {{ AGENT_LEVEL[row.agentLevel || 0] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="上级代理" width="120">
          <template #default="{ row }">
            <span v-if="row.agentLevel === 2 && row.parentAgentId">
              <el-link type="primary" @click="showParentAgent(row.parentAgentId)">
                查看上级
              </el-link>
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="userType" label="VIP等级" width="100">
          <template #default="{ row }">
            <el-tag :type="isVip(row) ? 'warning' : 'info'">
              {{ isVip(row) ? 'VIP会员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="integralBalance" label="积分" width="100" />
        <el-table-column prop="totalConsumeAmount" label="消费金额" width="100">
          <template #default="{ row }">
            {{ formatMoney(row.totalConsumeAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="isShipper" label="发货人员" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isShipper === 1 ? 'warning' : 'info'">
              {{ row.isShipper === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button type="info" size="small" @click="handleViewDetail(row)">详情</el-button>
              <el-button
                :type="row.status === 1 ? 'danger' : 'success'"
                size="small"
                @click="handleToggleStatus(row)"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
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

    <!-- 用户详情对话框 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <el-descriptions v-if="currentUser" :column="2" border>
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户编号">{{ currentUser.userNo }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="代理等级">{{ AGENT_LEVEL[currentUser.agentLevel || 0] }}</el-descriptions-item>
        <el-descriptions-item label="VIP等级">{{ isVip(currentUser) ? 'VIP会员' : '普通用户' }}</el-descriptions-item>
        <el-descriptions-item label="积分">{{ currentUser.integralBalance }}</el-descriptions-item>
        <el-descriptions-item label="发货人员">{{ currentUser.isShipper === 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="消费金额">{{ formatMoney(currentUser.totalConsumeAmount) }}</el-descriptions-item>
        <el-descriptions-item label="邀请码">{{ currentUser.inviteCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">{{ formatDate(currentUser.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 编辑用户对话框 -->
    <el-dialog v-model="editVisible" title="编辑用户" width="600px">
      <el-form v-if="editForm" :model="editForm" label-width="120px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="editForm.userType" placeholder="请选择用户类型" style="width: 100%">
            <el-option label="普通用户" :value="1" />
            <el-option label="VIP会员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="代理等级">
          <el-select v-model="editForm.agentLevel" placeholder="请选择代理等级" style="width: 100%">
            <el-option label="非代理" :value="0" />
            <el-option label="一级代理" :value="1" />
            <el-option label="二级代理" :value="2" />
          </el-select>
          <el-alert
            v-if="editForm.agentLevel === 2"
            title="注意：二级代理的佣金比例是根据一级代理的佣金去分成的"
            type="warning"
            :closable="false"
            style="margin-top: 10px"
          />
        </el-form-item>
        <el-form-item label="选择上级代理" v-if="editForm.agentLevel === 2">
          <el-select
            v-model="editForm.parentAgentId"
            placeholder="请选择一级代理"
            style="width: 100%"
            :disabled="!!editForm.parentAgentInfo"
          >
            <el-option
              v-for="agent in firstLevelAgents"
              :key="agent.id"
              :label="`${agent.nickname} (${agent.phone})`"
              :value="agent.id"
            />
          </el-select>
          <div v-if="editForm.parentAgentInfo" style="margin-top: 8px; color: #909399; font-size: 12px;">
            当前上级: {{ editForm.parentAgentInfo.nickname }} ({{ editForm.parentAgentInfo.phone }})
          </div>
        </el-form-item>
        <el-form-item label="佣金比例" v-if="editForm.agentLevel > 0">
          <el-input-number v-model="editForm.commissionRate" :min="0" :max="1" :step="0.01" :precision="2" />
          <span style="margin-left: 10px">{{ (editForm.commissionRate * 100).toFixed(0) }}%</span>
        </el-form-item>
        <el-form-item label="积分余额">
          <el-input-number v-model="editForm.integralBalance" :min="0" />
        </el-form-item>
        <el-form-item label="发货人员">
          <el-switch
            v-model="editForm.isShipper"
            :active-value="1"
            :inactive-value="0"
            active-text="是"
            inactive-text="否"
          />
          <el-alert
            title="发货人员可以在小程序端访问发货管理功能"
            type="info"
            :closable="false"
            style="margin-top: 10px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="editForm.status"
            :active-value="1"
            :inactive-value="0"
            active-text="正常"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { getUserList, setFirstAgent, updateUserStatus, updateUser } from '@/api/user'
import { formatMoney, formatDate, AGENT_LEVEL, VIP_LEVEL } from '@/utils'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { User } from '@/types'

const loading = ref(false)
const tableData = ref<User[]>([])
const detailVisible = ref(false)
const editVisible = ref(false)
const currentUser = ref<User | null>(null)
const editForm = ref<any>(null)

// 计算一级代理列表
const firstLevelAgents = computed(() => {
  return tableData.value.filter((u: User) => u.agentLevel === 1)
})

const searchForm = reactive({
  keyword: '',
  agentLevel: undefined,
  vipLevel: undefined
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    if (res.code === 200 || res.code === 0) {
      tableData.value = res.data.records || res.data.list || []
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.agentLevel = undefined
  searchForm.vipLevel = undefined
  pagination.page = 1
  loadData()
}

// 设为一级代理
const handleSetAgent = async (row: User) => {
  try {
    await ElMessageBox.confirm('确定要将该用户设为一级代理吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await setFirstAgent(row.id)
    ElMessage.success('设置成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('设置一级代理失败:', error)
    }
  }
}

// 查看详情
const handleViewDetail = (row: User) => {
  currentUser.value = row
  detailVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row: User) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'

  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await updateUserStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新用户状态失败:', error)
    }
  }
}

// 编辑用户
const handleEdit = async (row: User) => {
  // 获取代理等级
  let agentLevel = row.agentLevel || 0

  // 获取上级代理信息(如果是二级代理)
  let parentAgentInfo = null
  if (agentLevel === 2 && row.parentAgentId) {
    const parentAgent = tableData.value.find((u: User) => u.id === row.parentAgentId)
    if (parentAgent) {
      parentAgentInfo = {
        nickname: parentAgent.nickname,
        phone: parentAgent.phone,
        userNo: parentAgent.userNo
      }
    }
  }

  editForm.value = {
    id: row.id,
    nickname: row.nickname,
    phone: row.phone,
    userType: row.userType || 1,
    agentLevel: agentLevel,
    commissionRate: row.commissionRate || 0,
    integralBalance: row.integralBalance,
    status: row.status,
    isShipper: row.isShipper || 0,
    parentAgentId: row.parentAgentId,
    parentAgentInfo: parentAgentInfo
  }
  editVisible.value = true
}

// 查看上级代理
const showParentAgent = (parentAgentId: number) => {
  const parentAgent = tableData.value.find((u: User) => u.id === parentAgentId)
  if (parentAgent) {
    handleViewDetail(parentAgent)
  } else {
    ElMessage.warning('未找到上级代理信息')
  }
}

// 保存编辑
const handleSaveEdit = async () => {
  try {
    // 验证二级代理必须选择上级
    if (editForm.value.agentLevel === 2) {
      if (!editForm.value.parentAgentId) {
        ElMessage.warning('请选择上级一级代理')
        return
      }
    }

    // 设置上级代理ID
    let parentAgentId = null
    if (editForm.value.agentLevel === 2) {
      parentAgentId = editForm.value.parentAgentId
    }

    const submitData = {
      ...editForm.value,
      userType: editForm.value.userType,
      agentLevel: editForm.value.agentLevel,
      parentAgentId: parentAgentId,
      isShipper: editForm.value.isShipper
    }

    await updateUser(submitData.id, submitData)
    ElMessage.success('保存成功')
    editVisible.value = false
    loadData()
  } catch (error) {
    console.error('保存用户信息失败:', error)
  }
}

// 检查用户是否是VIP会员（基于userType）
const isVip = (user: User) => {
  return user && user.userType === 2
}

onMounted(() => {
  loadData()
})
</script>
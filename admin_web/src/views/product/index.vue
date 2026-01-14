<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">商品管理</h2>
      <el-button type="primary" @click="handleAdd">添加商品</el-button>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="搜索">
          <el-input v-model="searchForm.keyword" placeholder="请输入商品名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="全部" clearable style="width: 150px">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="已上架" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button @click="manageCategoryVisible = true">分类管理</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 商品列表 -->
    <el-card shadow="hover">
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="封面" width="80">
          <template #default="{ row }">
            <el-image
              :src="getImageUrl(row.cover)"
              :preview-src-list="[getImageUrl(row.cover)]"
              class="image-preview"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="150" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            {{ formatMoney(row.price) }}
          </template>
        </el-table-column>
        <el-table-column prop="vipPrice" label="VIP价" width="100">
          <template #default="{ row }">
            {{ formatMoney(row.vipPrice) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="sales" label="销量" width="100">
          <template #default="{ row }">
            {{ row.sales }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isRecommend" label="推荐" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isRecommend === 1 ? 'warning' : 'info'">
              {{ row.isRecommend === 1 ? '推荐' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="460" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button type="info" size="small" @click="handleManageSpec(row)">规格</el-button>
              <el-button type="success" size="small" @click="handleEditSales(row)">修改销量</el-button>
              <el-button
                :type="row.status === 1 ? 'warning' : 'success'"
                size="small"
                @click="handleToggleStatus(row)"
              >
                {{ row.status === 1 ? '下架' : '上架' }}
              </el-button>
              <el-button
                :type="row.isRecommend === 1 ? 'info' : 'warning'"
                size="small"
                @click="handleToggleRecommend(row)"
              >
                {{ row.isRecommend === 1 ? '取消推荐' : '设为推荐' }}
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 添加/编辑商品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑商品' : '添加商品'"
      width="1200px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="VIP价格" prop="vipPrice">
              <el-input-number v-model="form.vipPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品封面" prop="cover">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :http-request="handleUploadCover"
            accept="image/*"
          >
            <img v-if="form.cover" :src="getImageUrl(form.cover)" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="商品简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品简介" />
        </el-form-item>

        <el-form-item label="商品详情" prop="detail">
          <QuillEditor
            v-model:content="form.detail"
            content-type="html"
            :options="editorOptions"
            class="product-detail-editor"
            @ready="onEditorReady"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分类管理对话框 -->
    <el-dialog v-model="manageCategoryVisible" title="分类管理" width="600px">
      <el-button type="primary" size="small" style="margin-bottom: 15px" @click="handleAddCategory">
        添加分类
      </el-button>
      <el-table :data="categories" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEditCategory(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加/编辑分类对话框 -->
    <el-dialog v-model="categoryDialogVisible" :title="isCategoryEdit ? '编辑分类' : '添加分类'" width="400px">
      <el-form :model="categoryForm" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="categoryForm.sort" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitCategory">确定</el-button>
      </template>
    </el-dialog>

    <!-- 规格管理对话框 -->
    <el-dialog v-model="specDialogVisible" title="规格管理" width="900px">
      <div style="margin-bottom: 15px">
        <el-button type="primary" size="small" @click="handleAddSpec">添加规格</el-button>
      </div>
      <el-table :data="specList" v-loading="specLoading" stripe>
        <el-table-column prop="specName" label="规格名称" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            {{ formatMoney(row.price) }}
          </template>
        </el-table-column>
        <el-table-column prop="vipPrice" label="VIP价格" width="100">
          <template #default="{ row }">
            {{ row.vipPrice ? formatMoney(row.vipPrice) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEditSpec(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteSpec(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加/编辑规格对话框 -->
    <el-dialog v-model="specFormDialogVisible" :title="isSpecEdit ? '编辑规格' : '添加规格'" width="500px">
      <el-form :model="specForm" label-width="100px">
        <el-form-item label="规格名称" required>
          <el-input v-model="specForm.specName" placeholder="如: 1斤装、5斤装" />
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number v-model="specForm.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="VIP价格">
          <el-input-number v-model="specForm.vipPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" required>
          <el-input-number v-model="specForm.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="specForm.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="specForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="specFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitSpec">确定</el-button>
      </template>
    </el-dialog>

    <!-- 修改销量对话框 -->
    <el-dialog v-model="salesDialogVisible" title="修改销量" width="400px">
      <el-form :model="salesForm" label-width="80px">
        <el-form-item label="商品名称">
          <span>{{ salesForm.productName }}</span>
        </el-form-item>
        <el-form-item label="当前销量">
          <span>{{ salesForm.currentSales }}</span>
        </el-form-item>
        <el-form-item label="新销量" required>
          <el-input-number v-model="salesForm.newSales" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="salesDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitSales">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import type Quill from 'quill'
import {
  getProductList,
  createProduct,
  updateProduct,
  deleteProduct,
  updateProductStatus,
  updateProductRecommend,
  updateProductSales,
  getCategoryList,
  createCategory,
  updateCategory,
  deleteCategory,
  getProductSpecs,
  addProductSpec,
  updateProductSpec,
  deleteProductSpec,
  type ProductSpec
} from '@/api/product'
import { uploadProductImage } from '@/api/upload'
import { formatMoney, getImageUrl } from '@/utils'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { Product, Category } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<Product[]>([])
const categories = ref<Category[]>([])
const dialogVisible = ref(false)
const manageCategoryVisible = ref(false)
const categoryDialogVisible = ref(false)
const isEdit = ref(false)
const isCategoryEdit = ref(false)
const formRef = ref<FormInstance>()

// 规格管理相关
const specDialogVisible = ref(false)
const specFormDialogVisible = ref(false)
const specLoading = ref(false)
const specList = ref<ProductSpec[]>([])
const isSpecEdit = ref(false)
const currentProduct = ref<Product | null>(null)

// 销量管理相关
const salesDialogVisible = ref(false)
const salesForm = reactive({
  productId: 0,
  productName: '',
  currentSales: 0,
  newSales: 0
})

// Quill 编辑器配置
const editorOptions = {
  theme: 'snow',
  modules: {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      ['blockquote', 'code-block'],
      [{ header: 1 }, { header: 2 }],
      [{ list: 'ordered' }, { list: 'bullet' }],
      [{ script: 'sub' }, { script: 'super' }],
      [{ indent: '-1' }, { indent: '+1' }],
      [{ size: ['small', false, 'large', 'huge'] }],
      [{ header: [1, 2, 3, 4, 5, 6, false] }],
      [{ color: [] }, { background: [] }],
      [{ align: [] }],
      ['link', 'image'],
      ['clean']
    ]
  },
  placeholder: '请输入商品详情，支持粘贴图片...'
}

// 编辑器准备就绪
const onEditorReady = (quill: Quill) => {
  // 监听粘贴事件
  const editorElement = quill.root
  editorElement.addEventListener('paste', async (e: ClipboardEvent) => {
    const clipboardData = e.clipboardData
    if (!clipboardData) return

    const items = clipboardData.items
    for (let i = 0; i < items.length; i++) {
      const item = items[i]
      if (!item) continue

      // 检查是否为图片
      if (item.type.indexOf('image') !== -1) {
        e.preventDefault() // 阻止默认粘贴行为

        const file = item.getAsFile()
        if (!file) continue

        try {
          // 显示加载提示
          ElMessage.info('正在上传图片...')

          // 上传到 MinIO
          const res = await uploadProductImage(file)
          if (res.code === 200 || res.code === 0) {
            // 获取光标位置
            const range = quill.getSelection(true)
            // 插入图片
            quill.insertEmbed(range.index, 'image', res.data.url)
            // 移动光标到图片后面
            quill.setSelection(range.index + 1, 0)

            ElMessage.success('图片上传成功')
          } else {
            ElMessage.error('图片上传失败')
          }
        } catch (error) {
          console.error('上传图片失败:', error)
          ElMessage.error('图片上传失败，请重试')
        }

        break
      }
    }
  })
}

const searchForm = reactive({
  keyword: '',
  categoryId: undefined,
  status: undefined
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const form = reactive<Partial<Product>>({
  name: '',
  categoryId: undefined,
  cover: '',
  price: 0,
  vipPrice: 0,
  stock: 0,
  description: '',
  detail: ''
})

const categoryForm = reactive<Partial<Category>>({
  name: '',
  sort: 0
})

const specForm = reactive<Partial<ProductSpec>>({
  specName: '',
  price: 0,
  vipPrice: 0,
  stock: 0,
  sortOrder: 0,
  status: 1,
  productId: 0
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  cover: [{ required: true, message: '请上传商品封面', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

// 加载商品列表
const loadData = async () => {
  loading.value = true
  try {
    const res = await getProductList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    if (res.code === 200 || res.code === 0) {
      // Map backend fields to frontend fields
      const records = res.data.records || res.data.list || []
      tableData.value = records.map((item: any) => ({
        ...item,
        name: item.productName || item.name,
        cover: item.mainImage || item.cover,
        sales: item.salesCount || item.sales || 0,
        images: item.images || (item.mainImage ? [item.mainImage] : [])
      }))
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    if (res.code === 200 || res.code === 0) {
      // Map backend categoryName to frontend name
      categories.value = res.data.map((item: any) => ({
        ...item,
        name: item.categoryName || item.name,
        sort: item.sortOrder || item.sort || 0
      }))
    }
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.categoryId = undefined
  searchForm.status = undefined
  pagination.page = 1
  loadData()
}

// 添加商品
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑商品
const handleEdit = (row: Product) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 上传封面
const handleUploadCover = async (options: any) => {
  try {
    const res = await uploadProductImage(options.file)
    if (res.code === 200 || res.code === 0) {
      form.cover = res.data.url
      ElMessage.success('上传成功')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败，请重试')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      // 映射字段名：前端 name -> 后端 productName
      const submitData = {
        ...form,
        productName: form.name,
        mainImage: form.cover,
        vipPrice: form.vipPrice || form.price,  // 如果没有VIP价格，使用普通价格
        images: Array.isArray(form.images) ? form.images.join(',') : form.cover  // 将数组转换为逗号分隔的字符串
      }

      if (isEdit.value) {
        await updateProduct(form.id!, submitData)
        ElMessage.success('更新成功')
      } else {
        await createProduct(submitData)
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

// 切换状态
const handleToggleStatus = async (row: Product) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'

  try {
    await ElMessageBox.confirm(`确定要${action}该商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      center: true
    })

    await updateProductStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
    }
  }
}

// 切换推荐状态
const handleToggleRecommend = async (row: Product) => {
  const newRecommend = row.isRecommend === 1 ? 0 : 1
  const action = newRecommend === 1 ? '设为推荐' : '取消推荐'

  try {
    await ElMessageBox.confirm(`确定要${action}该商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      center: true
    })

    await updateProductRecommend(row.id, newRecommend)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新推荐状态失败:', error)
    }
  }
}

// 删除商品
const handleDelete = async (row: Product) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      center: true
    })

    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    name: '',
    categoryId: undefined,
    cover: '',
    price: 0,
    vipPrice: 0,
    stock: 0,
    description: '',
    detail: ''
  })
}

// 分类管理
const handleAddCategory = () => {
  isCategoryEdit.value = false
  categoryForm.name = ''
  categoryForm.sort = 0
  categoryDialogVisible.value = true
}

const handleEditCategory = (row: Category) => {
  isCategoryEdit.value = true
  Object.assign(categoryForm, row)
  categoryDialogVisible.value = true
}

const handleSubmitCategory = async () => {
  if (!categoryForm.name) {
    ElMessage.warning('请输入分类名称')
    return
  }

  try {
    // Map frontend fields to backend fields
    const submitData = {
      ...categoryForm,
      categoryName: categoryForm.name,
      sortOrder: categoryForm.sort
    }

    if (isCategoryEdit.value) {
      await updateCategory(categoryForm.id!, submitData)
      ElMessage.success('更新成功')
    } else {
      await createCategory(submitData)
      ElMessage.success('创建成功')
    }
    categoryDialogVisible.value = false
    loadCategories()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

const handleDeleteCategory = async (row: Category) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      center: true
    })

    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// ==================== 规格管理 ====================

// 管理规格
const handleManageSpec = async (row: Product) => {
  currentProduct.value = row
  specDialogVisible.value = true
  await loadSpecList(row.id)
}

// 加载规格列表
const loadSpecList = async (productId: number) => {
  specLoading.value = true
  try {
    const res = await getProductSpecs(productId)
    if (res.code === 200 || res.code === 0) {
      specList.value = res.data || []
    }
  } catch (error) {
    console.error('加载规格列表失败:', error)
  } finally {
    specLoading.value = false
  }
}

// 添加规格
const handleAddSpec = () => {
  isSpecEdit.value = false
  Object.assign(specForm, {
    specName: '',
    price: 0,
    vipPrice: 0,
    stock: 0,
    sortOrder: 0,
    status: 1,
    productId: currentProduct.value?.id || 0
  })
  specFormDialogVisible.value = true
}

// 编辑规格
const handleEditSpec = (row: ProductSpec) => {
  isSpecEdit.value = true
  Object.assign(specForm, row)
  specFormDialogVisible.value = true
}

// 提交规格
const handleSubmitSpec = async () => {
  if (!specForm.specName) {
    ElMessage.warning('请输入规格名称')
    return
  }
  if (!specForm.price) {
    ElMessage.warning('请输入价格')
    return
  }
  if (!specForm.stock && specForm.stock !== 0) {
    ElMessage.warning('请输入库存')
    return
  }

  try {
    if (isSpecEdit.value) {
      await updateProductSpec(specForm.id!, specForm)
      ElMessage.success('更新成功')
    } else {
      await addProductSpec(currentProduct.value!.id, specForm)
      ElMessage.success('添加成功')
    }
    specFormDialogVisible.value = false
    await loadSpecList(currentProduct.value!.id)
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败，请重试')
  }
}

// 删除规格
const handleDeleteSpec = async (row: ProductSpec) => {
  try {
    await ElMessageBox.confirm('确定要删除该规格吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      center: true
    })

    await deleteProductSpec(row.id!)
    ElMessage.success('删除成功')
    await loadSpecList(currentProduct.value!.id)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// ==================== 销量管理 ====================

// 修改销量
const handleEditSales = (row: Product) => {
  salesForm.productId = row.id
  salesForm.productName = row.name
  salesForm.currentSales = row.sales || 0
  salesForm.newSales = row.sales || 0
  salesDialogVisible.value = true
}

// 提交销量修改
const handleSubmitSales = async () => {
  if (salesForm.newSales === undefined || salesForm.newSales < 0) {
    ElMessage.warning('请输入有效的销量')
    return
  }

  try {
    await updateProductSales(salesForm.productId, salesForm.newSales)
    ElMessage.success('修改成功')
    salesDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('修改销量失败:', error)
    ElMessage.error('修改失败，请重试')
  }
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.avatar-uploader .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  transition: border-color 0.3s;
}

.avatar-uploader .avatar-uploader-icon:hover {
  border-color: #409eff;
}

/* Quill 编辑器样式调整 */
.product-detail-editor {
  width: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
}

.product-detail-editor :deep(.ql-toolbar.ql-snow) {
  border: none;
  border-bottom: 1px solid #dcdfe6;
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
  background-color: #f5f7fa;
  padding: 8px;
}

.product-detail-editor :deep(.ql-container.ql-snow) {
  height: 400px;
  overflow-y: auto;
  border: none;
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
  font-size: 14px;
}

.product-detail-editor :deep(.ql-editor) {
  min-height: 380px;
  font-size: 14px;
  line-height: 1.8;
  padding: 15px;
}

.product-detail-editor :deep(.ql-editor.ql-blank::before) {
  font-style: normal;
  color: #c0c4cc;
  left: 15px;
}

.product-detail-editor :deep(.ql-editor p) {
  margin: 0 0 10px 0;
}

.product-detail-editor :deep(.ql-editor img) {
  max-width: 100%;
  height: auto;
  cursor: pointer;
  margin: 10px 0;
}

/* 确保表单项占满宽度 */
:deep(.el-form-item) {
  width: 100%;
}

:deep(.el-form-item__content) {
  width: 100%;
  display: block;
}
</style>
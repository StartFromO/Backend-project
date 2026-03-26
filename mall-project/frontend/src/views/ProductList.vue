<template>
  <div class="product-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">商品管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加商品
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入商品名称搜索"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
        <el-button @click="resetSearch">重置</el-button>
      </div>

      <!-- 商品表格 -->
      <el-table
        :data="productStore.products"
        v-loading="productStore.loading"
        stripe
        border
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="商品图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              :src="row.imageUrl || 'https://via.placeholder.com/80'"
              :preview-src-list="[row.imageUrl]"
              style="width: 60px; height: 60px"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column label="价格" width="120">
          <template #default="{ row }">
            <div class="price-info">
              <span class="current-price">¥{{ row.price }}</span>
              <span v-if="row.originalPrice" class="original-price">
                ¥{{ row.originalPrice }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center" />
        <el-table-column prop="sales" label="销量" width="80" align="center" />
        <el-table-column label="热门" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isHot ? 'danger' : 'info'" size="small">
              {{ row.isHot ? '热门' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              :type="row.isHot ? 'warning' : 'success'" 
              link 
              @click="toggleHot(row)"
            >
              {{ row.isHot ? '取消热门' : '设为热门' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="productStore.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useProductStore } from '@/stores/product'
import type { Product } from '@/types/product'
import {Plus} from "@element-plus/icons-vue";

const router = useRouter()
const productStore = useProductStore()

const searchKeyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)

// 获取商品列表
const fetchData = async () => {
  await productStore.fetchProducts(pageNum.value, pageSize.value)
}

// 搜索
const handleSearch = async () => {
  if (searchKeyword.value.trim()) {
    await productStore.searchProducts(searchKeyword.value, pageNum.value, pageSize.value)
  } else {
    await fetchData()
  }
}

// 重置搜索
const resetSearch = async () => {
  searchKeyword.value = ''
  pageNum.value = 1
  await fetchData()
}

// 添加商品
const handleAdd = () => {
  router.push('/products/add')
}

// 编辑商品
const handleEdit = (row: Product) => {
  router.push(`/products/edit/${row.id}`)
}

// 删除商品
const handleDelete = async (row: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品 "${row.name}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await productStore.removeProduct(row.id)
    ElMessage.success('删除成功')
  } catch (error) {
    // 取消删除
  }
}

// 切换热门状态
const toggleHot = async (row: Product) => {
  try {
    await productStore.setHotStatus(row.id, !row.isHot)
    ElMessage.success(row.isHot ? '已取消热门' : '已设为热门')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchData()
}

// 页码变化
const handleCurrentChange = (val: number) => {
  pageNum.value = val
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 16px;
  font-weight: bold;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.price-info {
  display: flex;
  flex-direction: column;
}

.current-price {
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  color: #909399;
  font-size: 12px;
  text-decoration: line-through;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

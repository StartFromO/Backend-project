<template>
  <div class="search-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><Search /></el-icon>
            商品搜索
          </span>
        </div>
      </template>

      <!-- 搜索表单 -->
      <div class="search-form">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入商品名称或关键词"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="4">
            <el-select v-model="searchForm.category" placeholder="全部分类" clearable>
              <el-option label="手机数码" value="手机数码" />
              <el-option label="电脑办公" value="电脑办公" />
              <el-option label="平板电脑" value="平板电脑" />
              <el-option label="智能穿戴" value="智能穿戴" />
              <el-option label="家用电器" value="家用电器" />
              <el-option label="游戏娱乐" value="游戏娱乐" />
              <el-option label="电子阅读" value="电子阅读" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-input-number
              v-model="searchForm.minPrice"
              :min="0"
              :precision="2"
              placeholder="最低价格"
              style="width: 45%"
            />
            <span style="margin: 0 10px">-</span>
            <el-input-number
              v-model="searchForm.maxPrice"
              :min="0"
              :precision="2"
              placeholder="最高价格"
              style="width: 45%"
            />
          </el-col>
          <el-col :span="6">
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 搜索结果统计 -->
      <div v-if="hasSearched" class="search-stats">
        找到 <span class="highlight">{{ productStore.total }}</span> 件相关商品
        <span v-if="searchForm.keyword">
          ，关键词："<span class="highlight">{{ searchForm.keyword }}</span>"
        </span>
        <span v-if="searchForm.category">
          ，分类："<span class="highlight">{{ searchForm.category }}</span>"
        </span>
      </div>

      <!-- 搜索结果列表 -->
      <div v-if="hasSearched" v-loading="productStore.loading">
        <div v-if="productStore.products.length === 0" class="empty-result">
          <el-empty description="未找到相关商品" />
        </div>
        <div v-else>
          <!-- 列表视图 -->
          <el-table :data="productStore.products" stripe border class="result-table">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column label="商品图片" width="100" align="center">
              <template #default="{ row }">
                <el-image
                  :src="row.imageUrl || 'https://via.placeholder.com/80'"
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
            <el-table-column prop="sales" label="销量" width="80" align="center" />
            <el-table-column prop="stock" label="库存" width="80" align="center" />
            <el-table-column label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                  {{ row.status === 1 ? '上架' : '下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="viewDetail(row)">
                  查看详情
                </el-button>
                <el-button type="success" link @click="editProduct(row)">
                  编辑
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
        </div>
      </div>

      <!-- 推荐搜索 -->
      <div v-if="!hasSearched" class="hot-search">
        <div class="section-title">热门搜索</div>
        <div class="tag-list">
          <el-tag
            v-for="tag in hotSearchTags"
            :key="tag"
            class="search-tag"
            effect="plain"
            @click="quickSearch(tag)"
          >
            {{ tag }}
          </el-tag>
        </div>

        <div class="section-title" style="margin-top: 30px">热门商品推荐</div>
        <el-row :gutter="20">
          <el-col 
            :xs="24" :sm="12" :md="8" :lg="6" 
            v-for="product in hotProducts" 
            :key="product.id"
          >
            <el-card class="product-card" shadow="hover" @click="viewDetail(product)">
              <el-image
                :src="product.imageUrl || 'https://via.placeholder.com/300'"
                style="width: 100%; height: 180px"
                fit="cover"
              />
              <div class="card-content">
                <div class="card-title">{{ product.name }}</div>
                <div class="card-category">{{ product.category }}</div>
                <div class="card-price">
                  <span class="current-price">¥{{ product.price }}</span>
                  <span class="sales">已售 {{ product.sales }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 商品详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="商品详情"
      width="600px"
    >
      <div v-if="selectedProduct" class="product-detail">
        <el-image
          :src="selectedProduct.imageUrl"
          style="width: 100%; height: 300px"
          fit="cover"
        />
        <h3>{{ selectedProduct.name }}</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="分类">{{ selectedProduct.category }}</el-descriptions-item>
          <el-descriptions-item label="价格">
            <span class="price">¥{{ selectedProduct.price }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="原价">
            ¥{{ selectedProduct.originalPrice || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="销量">{{ selectedProduct.sales }}</el-descriptions-item>
          <el-descriptions-item label="库存">{{ selectedProduct.stock }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="selectedProduct.status === 1 ? 'success' : 'info'">
              {{ selectedProduct.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <div class="description">
          <div class="label">商品描述：</div>
          <div class="content">{{ selectedProduct.description }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '@/stores/product'
import * as productApi from '@/api/product'
import type { Product } from '@/types/product'

const router = useRouter()
const productStore = useProductStore()

const hasSearched = ref(false)
const detailVisible = ref(false)
const selectedProduct = ref<Product | null>(null)
const hotProducts = ref<Product[]>([])

const searchForm = reactive({
  keyword: '',
  category: '',
  minPrice: undefined as number | undefined,
  maxPrice: undefined as number | undefined
})

const pageNum = ref(1)
const pageSize = ref(10)

const hotSearchTags = ['手机', '电脑', '耳机', '平板', '手表', '游戏', '家电']


// 搜索
const handleSearch = async () => {
  hasSearched.value = true
  await productStore.fetchProducts(pageNum.value, pageSize.value)

  // 如果有搜索条件，进行过滤
  if (searchForm.keyword || searchForm.category || searchForm.minPrice || searchForm.maxPrice) {
    const res = await productApi.advancedSearch({
      keyword: searchForm.keyword,
      category: searchForm.category,
      minPrice: searchForm.minPrice,
      maxPrice: searchForm.maxPrice,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    productStore.products = res.data.list
    productStore.pageResult = res.data
  }
}

// 重置表单
const resetForm = () => {
  searchForm.keyword = ''
  searchForm.category = ''
  searchForm.minPrice = undefined
  searchForm.maxPrice = undefined
  hasSearched.value = false
  pageNum.value = 1
}

// 快速搜索
const quickSearch = (tag: string) => {
  searchForm.keyword = tag
  handleSearch()
}

// 查看详情
const viewDetail = (row: Product) => {
  selectedProduct.value = row
  detailVisible.value = true
}

// 编辑商品
const editProduct = (row: Product) => {
  router.push(`/products/edit/${row.id}`)
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  handleSearch()
}

// 页码变化
const handleCurrentChange = (val: number) => {
  pageNum.value = val
  handleSearch()
}

// 加载热门商品
const loadHotProducts = async () => {
  const res = await productApi.getHotProducts()
  hotProducts.value = res.data.slice(0, 4)
}

onMounted(() => {
  loadHotProducts()
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
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.search-stats {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #ecf5ff;
  border-radius: 4px;
  color: #606266;
}

.highlight {
  color: #409eff;
  font-weight: bold;
}

.empty-result {
  padding: 60px 0;
}

.result-table {
  margin-top: 20px;
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

.hot-search {
  margin-top: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.search-tag {
  cursor: pointer;
  
  &:hover {
    color: #409eff;
    border-color: #409eff;
  }
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
  
  &:hover {
    transform: translateY(-5px);
  }
  
  :deep(.el-card__body) {
    padding: 0;
  }
}

.card-content {
  padding: 15px;
}

.card-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-category {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
}

.card-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sales {
  font-size: 12px;
  color: #67c23a;
}

.product-detail {
  h3 {
    margin: 15px 0;
    font-size: 18px;
  }
  
  .price {
    color: #f56c6c;
    font-weight: bold;
    font-size: 16px;
  }
  
  .description {
    margin-top: 20px;
    
    .label {
      font-weight: bold;
      margin-bottom: 10px;
    }
    
    .content {
      color: #606266;
      line-height: 1.6;
      padding: 15px;
      background-color: #f5f7fa;
      border-radius: 4px;
    }
  }
}
</style>

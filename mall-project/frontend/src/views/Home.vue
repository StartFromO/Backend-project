<template>
  <div class="home">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #409EFF;">
              <el-icon size="32" color="#fff"><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalProducts }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #67C23A;">
              <el-icon size="32" color="#fff"><StarFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.hotProducts }}</div>
              <div class="stat-label">热门商品</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #E6A23C;">
              <el-icon size="32" color="#fff"><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalSales }}</div>
              <div class="stat-label">总销量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #F56C6C;">
              <el-icon size="32" color="#fff"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.categories }}</div>
              <div class="stat-label">商品分类</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门商品和最新商品 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热门推荐</span>
              <el-button type="primary" text @click="$router.push('/hot')">
                查看更多
              </el-button>
            </div>
          </template>
          <el-table :data="hotProducts" v-loading="loading" stripe>
            <el-table-column prop="name" label="商品名称" show-overflow-tooltip />
            <el-table-column prop="price" label="价格" width="100">
              <template #default="{ row }">
                <span style="color: #f56c6c; font-weight: bold;">
                  ¥{{ row.price }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="sales" label="销量" width="80" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新上架</span>
              <el-button type="primary" text @click="$router.push('/products')">
                查看更多
              </el-button>
            </div>
          </template>
          <el-table :data="latestProducts" v-loading="loading" stripe>
            <el-table-column prop="name" label="商品名称" show-overflow-tooltip />
            <el-table-column prop="category" label="分类" width="100" />
            <el-table-column prop="stock" label="库存" width="80" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card class="quick-actions">
      <template #header>
        <span>快捷操作</span>
      </template>
      <div class="action-buttons">
        <el-button type="primary" size="large" @click="$router.push('/products/add')">
          <el-icon><Plus /></el-icon>
          添加商品
        </el-button>
        <el-button type="success" size="large" @click="$router.push('/products')">
          <el-icon><Goods /></el-icon>
          商品管理
        </el-button>
        <el-button type="warning" size="large" @click="$router.push('/search')">
          <el-icon><Search /></el-icon>
          商品搜索
        </el-button>
        <el-button type="danger" size="large" @click="$router.push('/hot')">
          <el-icon><StarFilled /></el-icon>
          热门推荐
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useProductStore } from '@/stores/product'
//import type { Product } from '@/types/product'

const productStore = useProductStore()
const loading = ref(false)

// 统计数据
const statistics = computed(() => {
  const products = productStore.products
  const hotProducts = products.filter(p => p.isHot)
  const totalSales = products.reduce((sum, p) => sum + p.sales, 0)
  const categories = new Set(products.map(p => p.category)).size
  
  return {
    totalProducts: products.length,
    hotProducts: hotProducts.length,
    totalSales,
    categories: categories || 5
  }
})

// 热门商品
const hotProducts = computed(() => {
  return productStore.products
    .filter(p => p.isHot)
    .sort((a, b) => b.sales - a.sales)
    .slice(0, 5)
})

// 最新商品
const latestProducts = computed(() => {
  return [...productStore.products]
    .sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
    .slice(0, 5)
})

onMounted(async () => {
  loading.value = true
  await productStore.fetchProducts(1, 20)
  loading.value = false
})
</script>

<style scoped>
.statistics {
  margin-bottom: 20px;
}

.stat-card {
  :deep(.el-card__body) {
    padding: 20px;
  }
}

.stat-item {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.content-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.quick-actions {
  .action-buttons {
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
    
    .el-button {
      min-width: 150px;
    }
  }
}
</style>

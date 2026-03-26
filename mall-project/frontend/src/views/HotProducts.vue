<template>
  <div class="hot-products">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><StarFilled /></el-icon>
            热门推荐
          </span>
          <el-button type="primary" @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <!-- 热门商品统计 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="8">
          <div class="stat-box">
            <div class="stat-number">{{ hotProducts.length }}</div>
            <div class="stat-label">热门商品数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-box">
            <div class="stat-number">{{ totalSales }}</div>
            <div class="stat-label">总销量</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-box">
            <div class="stat-number">¥{{ totalRevenue }}</div>
            <div class="stat-label">总销售额</div>
          </div>
        </el-col>
      </el-row>

      <!-- 热门商品列表 -->
      <el-table
        :data="hotProducts"
        v-loading="loading"
        stripe
        border
      >
        <el-table-column type="index" label="排名" width="70" align="center">
          <template #default="{ $index }">
            <div class="rank" :class="{ 'top3': $index < 3 }">
              {{ $index + 1 }}
            </div>
          </template>
        </el-table-column>
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
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="100" sortable>
          <template #default="{ row }">
            <span class="sales">{{ row.sales }}</span>
          </template>
        </el-table-column>
        <el-table-column label="销售额" width="120">
          <template #default="{ row }">
            <span class="revenue">¥{{ (row.price * row.sales).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">
              查看详情
            </el-button>
            <el-button type="warning" link @click="cancelHot(row)">
              取消热门
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 商品卡片展示 -->
      <div class="section-title">热门商品展示</div>
      <el-row :gutter="20" class="product-cards">
        <el-col 
          :xs="24" :sm="12" :md="8" :lg="6" 
          v-for="product in hotProducts.slice(0, 8)" 
          :key="product.id"
        >
          <el-card class="product-card" shadow="hover">
            <div class="card-image">
              <el-image
                :src="product.imageUrl || 'https://via.placeholder.com/300'"
                style="width: 100%; height: 180px"
                fit="cover"
              />
              <div class="hot-badge">HOT</div>
            </div>
            <div class="card-content">
              <div class="card-title">{{ product.name }}</div>
              <div class="card-category">{{ product.category }}</div>
              <div class="card-price">
                <span class="current-price">¥{{ product.price }}</span>
                <span v-if="product.originalPrice" class="original-price">
                  ¥{{ product.originalPrice }}
                </span>
              </div>
              <div class="card-sales">
                <el-icon><SoldOut /></el-icon>
                已售 {{ product.sales }}
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
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
        <p class="description">{{ selectedProduct.description }}</p>
        <div class="detail-info">
          <div class="info-item">
            <span class="label">价格：</span>
            <span class="value price">¥{{ selectedProduct.price }}</span>
          </div>
          <div class="info-item">
            <span class="label">分类：</span>
            <span class="value">{{ selectedProduct.category }}</span>
          </div>
          <div class="info-item">
            <span class="label">销量：</span>
            <span class="value">{{ selectedProduct.sales }}</span>
          </div>
          <div class="info-item">
            <span class="label">库存：</span>
            <span class="value">{{ selectedProduct.stock }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useProductStore } from '@/stores/product'
import type { Product } from '@/types/product'

const router = useRouter()
const productStore = useProductStore()

const loading = ref(false)
const detailVisible = ref(false)
const selectedProduct = ref<Product | null>(null)

// 热门商品列表
const hotProducts = computed(() => {
  return productStore.hotProducts
})

// 总销量
const totalSales = computed(() => {
  return hotProducts.value.reduce((sum, p) => sum + p.sales, 0)
})

// 总销售额
const totalRevenue = computed(() => {
  return hotProducts.value
    .reduce((sum, p) => sum + p.price * p.sales, 0)
    .toFixed(2)
})

// 刷新数据
const refreshData = async () => {
  loading.value = true
  await productStore.fetchHotProducts()
  loading.value = false
}

// 查看详情
const viewDetail = (row: Product) => {
  selectedProduct.value = row
  detailVisible.value = true
}

// 取消热门
const cancelHot = async (row: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消 "${row.name}" 的热门状态吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await productStore.setHotStatus(row.id, false)
    ElMessage.success('已取消热门状态')
    refreshData()
  } catch (error) {
    // 取消操作
  }
}

onMounted(() => {
  refreshData()
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

.stats-row {
  margin-bottom: 30px;
}

.stat-box {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  color: #fff;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.rank {
  width: 28px;
  height: 28px;
  line-height: 28px;
  text-align: center;
  border-radius: 50%;
  background-color: #909399;
  color: #fff;
  font-weight: bold;
  margin: 0 auto;
}

.rank.top3 {
  background-color: #f56c6c;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.sales {
  color: #67c23a;
  font-weight: bold;
}

.revenue {
  color: #e6a23c;
  font-weight: bold;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin: 30px 0 20px;
  padding-left: 10px;
  border-left: 4px solid #409eff;
}

.product-cards {
  margin-top: 20px;
}

.product-card {
  margin-bottom: 20px;
  
  :deep(.el-card__body) {
    padding: 0;
  }
}

.card-image {
  position: relative;
}

.hot-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: linear-gradient(135deg, #f56c6c 0%, #e6a23c 100%);
  color: #fff;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
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
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.current-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.original-price {
  color: #909399;
  font-size: 12px;
  text-decoration: line-through;
}

.card-sales {
  font-size: 12px;
  color: #67c23a;
  display: flex;
  align-items: center;
  gap: 4px;
}

.product-detail {
  h3 {
    margin: 15px 0;
    font-size: 18px;
  }
  
  .description {
    color: #606266;
    line-height: 1.6;
    margin-bottom: 20px;
  }
  
  .detail-info {
    .info-item {
      margin-bottom: 10px;
      
      .label {
        color: #909399;
      }
      
      .value {
        font-weight: bold;
        
        &.price {
          color: #f56c6c;
          font-size: 18px;
        }
      }
    }
  }
}
</style>

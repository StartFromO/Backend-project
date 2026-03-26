import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Product, PageResult } from '@/types/product'
import * as productApi from '@/api/product'

/**
 * 商品状态管理
 */
export const useProductStore = defineStore('product', () => {
  // State
  const products = ref<Product[]>([])
  const hotProducts = ref<Product[]>([])
  const currentProduct = ref<Product | null>(null)
  const loading = ref(false)
  const pageResult = ref<PageResult<Product> | null>(null)

  // Getters
  const total = computed(() => pageResult.value?.total || 0)
  const totalPages = computed(() => pageResult.value?.totalPages || 0)

  // Actions
  
  // 获取商品列表
  const fetchProducts = async (pageNum = 1, pageSize = 10) => {
    loading.value = true
    try {
      const res = await productApi.getAllProducts(pageNum, pageSize)
      products.value = res.data.list
      pageResult.value = res.data
      return res.data
    } finally {
      loading.value = false
    }
  }

  // 获取热门商品
  const fetchHotProducts = async () => {
    loading.value = true
    try {
      const res = await productApi.getHotProducts()
      hotProducts.value = res.data
      return res.data
    } finally {
      loading.value = false
    }
  }

  // 搜索商品
  const searchProducts = async (keyword: string, pageNum = 1, pageSize = 10) => {
    loading.value = true
    try {
      const res = await productApi.searchProducts(keyword, pageNum, pageSize)
      products.value = res.data.list
      pageResult.value = res.data
      return res.data
    } finally {
      loading.value = false
    }
  }

  // 获取商品详情
  const fetchProductDetail = async (id: number) => {
    loading.value = true
    try {
      const res = await productApi.getProductById(id)
      currentProduct.value = res.data
      return res.data
    } finally {
      loading.value = false
    }
  }

  // 添加商品
  const createProduct = async (data: any) => {
    const res = await productApi.addProduct(data)
    return res.data
  }

  // 更新商品
  const editProduct = async (id: number, data: any) => {
    const res = await productApi.updateProduct(id, data)
    return res.data
  }

  // 删除商品
  const removeProduct = async (id: number) => {
    await productApi.deleteProduct(id)
    // 从列表中移除
    products.value = products.value.filter(p => p.id !== id)
  }

  // 设置热门状态
  const setHotStatus = async (id: number, isHot: boolean) => {
    const res = await productApi.updateHotStatus(id, isHot)
    // 更新本地数据
    const index = products.value.findIndex(p => p.id === id)
    if (index !== -1) {
      products.value[index].isHot = isHot
    }
    return res.data
  }

  return {
    products,
    hotProducts,
    currentProduct,
    loading,
    pageResult,
    total,
    totalPages,
    fetchProducts,
    fetchHotProducts,
    searchProducts,
    fetchProductDetail,
    createProduct,
    editProduct,
    removeProduct,
    setHotStatus
  }
})

import request from '@/utils/request'
import type { Product, ProductDTO, PageResult, Result, SearchParams } from '@/types/product'

/**
 * 商品API
 */

// 获取所有商品（分页）
export const getAllProducts = (pageNum = 1, pageSize = 10) => {
  return request.get<Result<PageResult<Product>>>('/products', {
    params: { pageNum, pageSize }
  })
}

// 根据ID获取商品
export const getProductById = (id: number) => {
  return request.get<Result<Product>>(`/products/${id}`)
}

// 添加商品
export const addProduct = (data: ProductDTO) => {
  return request.post<Result<Product>>('/products', data)
}

// 更新商品
export const updateProduct = (id: number, data: ProductDTO) => {
  return request.put<Result<Product>>(`/products/${id}`, data)
}

// 删除商品
export const deleteProduct = (id: number) => {
  return request.delete<Result<void>>(`/products/${id}`)
}

// 搜索商品
export const searchProducts = (keyword: string, pageNum = 1, pageSize = 10) => {
  return request.get<Result<PageResult<Product>>>('/products/search', {
    params: { keyword, pageNum, pageSize }
  })
}

// 高级搜索
export const advancedSearch = (params: SearchParams) => {
  return request.get<Result<PageResult<Product>>>('/products/advanced-search', { params })
}

// 获取热门商品
export const getHotProducts = () => {
  return request.get<Result<Product[]>>('/products/hot')
}

// 分页获取热门商品
export const getHotProductsPage = (pageNum = 1, pageSize = 10) => {
  return request.get<Result<PageResult<Product>>>('/products/hot/page', {
    params: { pageNum, pageSize }
  })
}

// 根据分类获取商品
export const getProductsByCategory = (category: string, pageNum = 1, pageSize = 10) => {
  return request.get<Result<PageResult<Product>>>(`/products/category/${category}`, {
    params: { pageNum, pageSize }
  })
}

// 设置商品热门状态
export const updateHotStatus = (id: number, isHot: boolean) => {
  return request.put<Result<Product>>(`/products/${id}/hot`, null, {
    params: { isHot }
  })
}

// 增加销量
export const increaseSales = (id: number, quantity = 1) => {
  return request.post<Result<void>>(`/products/${id}/sales`, null, {
    params: { quantity }
  })
}

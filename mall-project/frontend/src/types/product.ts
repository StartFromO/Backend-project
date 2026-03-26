/**
 * 商品类型定义
 */

export interface Product {
  id: number
  name: string
  description: string
  price: number
  originalPrice: number
  stock: number
  category: string
  imageUrl: string
  isHot: boolean
  sales: number
  status: number
  createTime: string
  updateTime: string
}

export interface ProductDTO {
  name: string
  description: string
  price: number
  originalPrice: number
  stock: number
  category: string
  imageUrl: string
  isHot: boolean
  status: number
}

export interface PageResult<T> {
  pageNum: number
  pageSize: number
  total: number
  totalPages: number
  list: T[]
}

export interface Result<T> {
  code: number
  message: string
  data: T
}

export interface SearchParams {
  keyword?: string
  category?: string
  minPrice?: number
  maxPrice?: number
  pageNum: number
  pageSize: number
}

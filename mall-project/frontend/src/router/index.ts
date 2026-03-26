import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/products',
        name: 'ProductList',
        component: () => import('@/views/ProductList.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: '/products/add',
        name: 'AddProduct',
        component: () => import('@/views/ProductForm.vue'),
        meta: { title: '添加商品' }
      },
      {
        path: '/products/edit/:id',
        name: 'EditProduct',
        component: () => import('@/views/ProductForm.vue'),
        meta: { title: '编辑商品' }
      },
      {
        path: '/hot',
        name: 'HotProducts',
        component: () => import('@/views/HotProducts.vue'),
        meta: { title: '热门推荐' }
      },
      {
        path: '/search',
        name: 'Search',
        component: () => import('@/views/Search.vue'),
        meta: { title: '商品搜索' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

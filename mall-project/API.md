# 商城管理系统 API 文档

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **跨域支持**: 已启用 CORS

## 响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

## 商品接口

### 1. 获取商品列表（分页）

**请求**
- **Method**: GET
- **URL**: `/products`
- **参数**:
  - `pageNum` (可选): 页码，默认 1
  - `pageSize` (可选): 每页大小，默认 10

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 100,
    "totalPages": 10,
    "list": [
      {
        "id": 1,
        "name": "iPhone 15 Pro Max",
        "description": "苹果最新旗舰手机",
        "price": 9999.00,
        "originalPrice": 10999.00,
        "stock": 100,
        "category": "手机数码",
        "imageUrl": "https://example.com/image.jpg",
        "isHot": true,
        "sales": 856,
        "status": 1,
        "createTime": "2024-01-15T10:30:00",
        "updateTime": "2024-01-15T10:30:00"
      }
    ]
  }
}
```

### 2. 根据ID获取商品

**请求**
- **Method**: GET
- **URL**: `/products/{id}`

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "iPhone 15 Pro Max",
    "description": "苹果最新旗舰手机",
    "price": 9999.00,
    "originalPrice": 10999.00,
    "stock": 100,
    "category": "手机数码",
    "imageUrl": "https://example.com/image.jpg",
    "isHot": true,
    "sales": 856,
    "status": 1,
    "createTime": "2024-01-15T10:30:00",
    "updateTime": "2024-01-15T10:30:00"
  }
}
```

### 3. 添加商品

**请求**
- **Method**: POST
- **URL**: `/products`
- **Body**:

```json
{
  "name": "iPhone 15 Pro Max",
  "description": "苹果最新旗舰手机",
  "price": 9999.00,
  "originalPrice": 10999.00,
  "stock": 100,
  "category": "手机数码",
  "imageUrl": "https://example.com/image.jpg",
  "isHot": true,
  "status": 1
}
```

**响应示例**
```json
{
  "code": 200,
  "message": "商品添加成功",
  "data": {
    "id": 1,
    "name": "iPhone 15 Pro Max",
    ...
  }
}
```

### 4. 更新商品

**请求**
- **Method**: PUT
- **URL**: `/products/{id}`
- **Body**: 同添加商品

**响应示例**
```json
{
  "code": 200,
  "message": "商品更新成功",
  "data": {
    "id": 1,
    "name": "iPhone 15 Pro Max",
    ...
  }
}
```

### 5. 删除商品

**请求**
- **Method**: DELETE
- **URL**: `/products/{id}`

**响应示例**
```json
{
  "code": 200,
  "message": "商品删除成功",
  "data": null
}
```

### 6. 搜索商品

**请求**
- **Method**: GET
- **URL**: `/products/search`
- **参数**:
  - `keyword` (必填): 搜索关键词
  - `pageNum` (可选): 页码，默认 1
  - `pageSize` (可选): 每页大小，默认 10

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 5,
    "totalPages": 1,
    "list": [...]
  }
}
```

### 7. 高级搜索

**请求**
- **Method**: GET
- **URL**: `/products/advanced-search`
- **参数**:
  - `keyword` (可选): 关键词
  - `category` (可选): 分类
  - `minPrice` (可选): 最低价格
  - `maxPrice` (可选): 最高价格
  - `pageNum` (可选): 页码，默认 1
  - `pageSize` (可选): 每页大小，默认 10

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 3,
    "totalPages": 1,
    "list": [...]
  }
}
```

### 8. 获取热门商品

**请求**
- **Method**: GET
- **URL**: `/products/hot`

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "iPhone 15 Pro Max",
      "isHot": true,
      "sales": 856,
      ...
    }
  ]
}
```

### 9. 分页获取热门商品

**请求**
- **Method**: GET
- **URL**: `/products/hot/page`
- **参数**:
  - `pageNum` (可选): 页码，默认 1
  - `pageSize` (可选): 每页大小，默认 10

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 5,
    "totalPages": 1,
    "list": [...]
  }
}
```

### 10. 根据分类获取商品

**请求**
- **Method**: GET
- **URL**: `/products/category/{category}`
- **参数**:
  - `pageNum` (可选): 页码，默认 1
  - `pageSize` (可选): 每页大小，默认 10

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 8,
    "totalPages": 1,
    "list": [...]
  }
}
```

### 11. 设置商品热门状态

**请求**
- **Method**: PUT
- **URL**: `/products/{id}/hot`
- **参数**:
  - `isHot` (必填): true/false

**响应示例**
```json
{
  "code": 200,
  "message": "热门状态更新成功",
  "data": {
    "id": 1,
    "isHot": true,
    ...
  }
}
```

### 12. 增加商品销量

**请求**
- **Method**: POST
- **URL**: `/products/{id}/sales`
- **参数**:
  - `quantity` (可选): 增加数量，默认 1

**响应示例**
```json
{
  "code": 200,
  "message": "销量更新成功",
  "data": null
}
```

## 错误码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 商品分类

系统预设以下商品分类：

- 手机数码
- 电脑办公
- 平板电脑
- 智能穿戴
- 家用电器
- 游戏娱乐
- 电子阅读

## 商品状态

| 状态值 | 说明 |
|--------|------|
| 0 | 下架 |
| 1 | 上架 |

## 测试数据

系统启动时会自动初始化以下测试数据：

1. iPhone 15 Pro Max - 手机数码 - ¥9999
2. MacBook Pro 14英寸 - 电脑办公 - ¥14999
3. AirPods Pro 2 - 手机数码 - ¥1899
4. iPad Air 5 - 平板电脑 - ¥4799
5. Apple Watch Series 9 - 智能穿戴 - ¥2999
6. Sony WH-1000XM5 - 手机数码 - ¥2499
7. Nintendo Switch OLED - 游戏娱乐 - ¥2199
8. Dyson V15 Detect - 家用电器 - ¥4999
9. 小米14 Pro - 手机数码 - ¥4999
10. 华为Mate 60 Pro - 手机数码 - ¥6999
11. 戴森吹风机 HD15 - 家用电器 - ¥3199
12. Kindle Paperwhite 5 - 电子阅读 - ¥1099

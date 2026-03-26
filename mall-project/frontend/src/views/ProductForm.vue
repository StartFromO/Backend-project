<template>
  <div class="product-form">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">{{ isEdit ? '编辑商品' : '添加商品' }}</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
                <el-option label="手机数码" value="手机数码" />
                <el-option label="电脑办公" value="电脑办公" />
                <el-option label="平板电脑" value="平板电脑" />
                <el-option label="智能穿戴" value="智能穿戴" />
                <el-option label="家用电器" value="家用电器" />
                <el-option label="游戏娱乐" value="游戏娱乐" />
                <el-option label="电子阅读" value="电子阅读" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品价格" prop="price">
              <el-input-number
                v-model="form.price"
                :min="0.01"
                :precision="2"
                :step="1"
                style="width: 100%"
                placeholder="请输入价格"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品原价" prop="originalPrice">
              <el-input-number
                v-model="form.originalPrice"
                :min="0"
                :precision="2"
                :step="1"
                style="width: 100%"
                placeholder="请输入原价（可选）"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存数量" prop="stock">
              <el-input-number
                v-model="form.stock"
                :min="0"
                :step="1"
                style="width: 100%"
                placeholder="请输入库存"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品状态">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="热门商品">
          <el-switch
            v-model="form.isHot"
            active-text="热门"
            inactive-text="普通"
          />
        </el-form-item>

        <el-form-item label="商品图片" prop="imageUrl">
          <el-input
            v-model="form.imageUrl"
            placeholder="请输入图片URL"
          >
            <template #append>
              <el-button @click="previewImage">预览</el-button>
            </template>
          </el-input>
          <div v-if="form.imageUrl" class="image-preview">
            <el-image
              :src="form.imageUrl"
              style="width: 150px; height: 150px"
              fit="cover"
            />
          </div>
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入商品描述"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '立即添加' }}
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useProductStore } from '@/stores/product'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()

const formRef = ref<FormInstance>()
const submitting = ref(false)

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  name: '',
  description: '',
  price: undefined as number | undefined,
  originalPrice: undefined as number | undefined,
  stock: undefined as number | undefined,
  category: '',
  imageUrl: '',
  isHot: false,
  status: 1
})

const rules: FormRules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ]
}

// 预览图片
const previewImage = () => {
  if (!form.imageUrl) {
    ElMessage.warning('请先输入图片URL')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const data = {
          ...form,
          price: Number(form.price),
          originalPrice: form.originalPrice ? Number(form.originalPrice) : undefined,
          stock: Number(form.stock)
        }
        
        if (isEdit.value) {
          await productStore.editProduct(Number(route.params.id), data)
          ElMessage.success('修改成功')
        } else {
          await productStore.createProduct(data)
          ElMessage.success('添加成功')
        }
        router.push('/products')
      } catch (error) {
        ElMessage.error(isEdit.value ? '修改失败' : '添加失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
}

// 加载商品详情
const loadProductDetail = async () => {
  if (isEdit.value) {
    try {
      const product = await productStore.fetchProductDetail(Number(route.params.id))
      Object.assign(form, {
        name: product.name,
        description: product.description,
        price: product.price,
        originalPrice: product.originalPrice,
        stock: product.stock,
        category: product.category,
        imageUrl: product.imageUrl,
        isHot: product.isHot,
        status: product.status
      })
    } catch (error) {
      ElMessage.error('加载商品详情失败')
    }
  }
}

onMounted(() => {
  loadProductDetail()
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

.form {
  max-width: 800px;
  margin: 0 auto;
}

.image-preview {
  margin-top: 10px;
  padding: 10px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  display: inline-block;
}
</style>

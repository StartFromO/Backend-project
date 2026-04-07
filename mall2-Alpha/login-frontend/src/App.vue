<template>
  <div v-if="!isLoggedIn" class="container">
    <div class="form-container">
      <h1>{{ currentForm === 'login' ? '登录' : currentForm === 'register' ? '注册' : '忘记密码' }}</h1>
      
      <!-- 登录表单 -->
      <form v-if="currentForm === 'login'" @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">用户名</label>
          <input type="text" id="username" v-model="loginForm.username" required />
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="loginForm.password" required />
        </div>
        <button type="submit" class="btn">登录</button>
        <div class="form-footer">
          <span>还没有账号？</span>
          <a href="#" @click.prevent="currentForm = 'register'">立即注册</a>
          <span class="divider">|</span>
          <a href="#" @click.prevent="currentForm = 'forgot'">忘记密码？</a>
        </div>
      </form>
      
      <!-- 注册表单 -->
      <form v-else-if="currentForm === 'register'" @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="reg-username">用户名</label>
          <input type="text" id="reg-username" v-model="registerForm.username" required />
        </div>
        <div class="form-group">
          <label for="reg-password">密码</label>
          <div class="password-container">
            <input :type="registerForm.showPassword ? 'text' : 'password'" id="reg-password" v-model="registerForm.password" required />
            <button type="button" class="toggle-password-btn" @click="registerForm.showPassword = !registerForm.showPassword">
              {{ registerForm.showPassword ? '隐藏' : '显示' }}
            </button>
          </div>
          <small>密码必须包含大小写字母、数字和特殊字符，长度至少8位</small>
        </div>
        <div class="form-group">
          <label for="reg-confirm-password">确认密码</label>
          <div class="password-container">
            <input :type="registerForm.showConfirmPassword ? 'text' : 'password'" id="reg-confirm-password" v-model="registerForm.confirmPassword" required />
            <button type="button" class="toggle-password-btn" @click="registerForm.showConfirmPassword = !registerForm.showConfirmPassword">
              {{ registerForm.showConfirmPassword ? '隐藏' : '显示' }}
            </button>
          </div>
        </div>
        <div class="form-group">
          <label for="reg-phone">手机号码</label>
          <div class="phone-code-container">
            <input type="tel" id="reg-phone" v-model="registerForm.phone" required />
          </div>
        </div>
        <div class="form-group">
          <label for="reg-code">验证码</label>
          <div class="phone-code-container">
            <input type="text" id="reg-code" v-model="registerForm.code" required />
            <button type="button" class="code-btn" @click="sendRegisterCode" :disabled="countdown > 0">
              {{ countdown > 0 ? `${countdown}秒后重新发送` : '发送验证码' }}
            </button>
          </div>
        </div>
        <button type="submit" class="btn">注册</button>
        <div class="form-footer">
          <span>已有账号？</span>
          <a href="#" @click.prevent="currentForm = 'login'">立即登录</a>
        </div>
      </form>
      
      <!-- 忘记密码表单 -->
      <form v-else-if="currentForm === 'forgot'" @submit.prevent="handleForgotPassword">
        <div class="form-group">
          <label for="forgot-phone">手机号码</label>
          <div class="phone-code-container">
            <input type="tel" id="forgot-phone" v-model="forgotForm.phone" required />
            <button type="button" class="code-btn" @click="sendCode" :disabled="countdown > 0">
              {{ countdown > 0 ? `${countdown}秒后重新发送` : '发送验证码' }}
            </button>
          </div>
        </div>
        <div class="form-group">
          <label for="forgot-code">验证码</label>
          <input type="text" id="forgot-code" v-model="forgotForm.code" required />
        </div>
        <div class="form-group">
          <label for="forgot-password">新密码</label>
          <input type="password" id="forgot-password" v-model="forgotForm.newPassword" required />
          <small>密码必须包含大小写字母、数字和特殊字符，长度至少8位</small>
        </div>
        <button type="submit" class="btn">重置密码</button>
        <div class="form-footer">
          <span>想起密码了？</span>
          <a href="#" @click.prevent="currentForm = 'login'">立即登录</a>
        </div>
      </form>
    </div>
  </div>
  
  <!-- 商城界面 -->
  <div v-else class="mall-container">
    <!-- 导航栏 -->
    <nav class="nav">
      <div class="nav-left">
        <h1>商品商城</h1>
      </div>
      <div class="nav-center">
        <div class="search-box">
          <input type="text" v-model="searchKeyword" placeholder="搜索商品..." />
          <button class="search-btn" @click="searchProducts">搜索</button>
        </div>
      </div>
      <div class="nav-right">
        <span class="welcome">欢迎，{{ userInfo.username }}</span>
        <button class="logout-btn" @click="logout">退出登录</button>
        <button class="order-btn" @click="showOrderList = !showOrderList">我的订单</button>
        <button class="cart-btn" @click="showCart = !showCart">购物车 ({{ cartCount }})</button>
      </div>
    </nav>
    
    <!-- 购物车侧边栏 -->
    <div v-if="showCart" class="cart-sidebar">
      <div class="cart-header">
        <h3>购物车</h3>
        <button class="close-btn" @click="showCart = false">×</button>
      </div>
      <div class="cart-content">
        <div v-if="cartItems.length === 0" class="empty-cart">
          购物车为空
        </div>
        <div v-else class="cart-item" v-for="item in cartItems" :key="item.cart.id">
          <div class="cart-item-info">
            <h4>{{ item.product.name }}</h4>
            <p>¥{{ item.product.price }}</p>
          </div>
          <div class="cart-item-quantity">
            <button @click="updateCartQuantity(item.product.id, item.cart.quantity - 1)" :disabled="item.cart.quantity <= 1">-</button>
            <span>{{ item.cart.quantity }}</span>
            <button @click="updateCartQuantity(item.product.id, item.cart.quantity + 1)">+</button>
          </div>
          <button class="remove-btn" @click="removeFromCart(item.product.id)">删除</button>
        </div>
      </div>
      <div v-if="cartItems.length > 0" class="cart-footer">
        <button class="buy-btn" @click="buyItems">立即购买</button>
      </div>
    </div>
    
    <!-- 订单列表侧边栏 -->
    <div v-if="showOrderList" class="order-sidebar">
      <div class="order-header">
        <h3>我的订单</h3>
        <button class="close-btn" @click="showOrderList = false">×</button>
      </div>
      <div class="order-content">
        <!-- 订单状态筛选 -->
        <div class="order-filter">
          <button 
            v-for="status in orderStatusList" 
            :key="status"
            :class="['filter-btn', { active: selectedOrderStatus === status }]"
            @click="selectedOrderStatus = status; loadOrders()"
          >
            {{ status }}
          </button>
        </div>
        
        <!-- 订单列表 -->
        <div v-if="orders.length === 0" class="empty-order">
          暂无订单
        </div>
        <div v-else class="order-list">
          <div v-for="order in orders" :key="order.id" class="order-item">
            <div class="order-header-info">
              <span class="order-number">订单号：{{ order.orderNumber }}</span>
              <span class="order-status">{{ order.status }}</span>
            </div>
            <div class="order-details">
              <p>总金额：¥{{ order.totalAmount }}</p>
              <p>创建时间：{{ order.createdAt }}</p>
              <p v-if="order.expressCompany">快递公司：{{ order.expressCompany }}</p>
              <p v-if="order.expressNumber">快递单号：{{ order.expressNumber }}</p>
            </div>
            <div class="order-actions">
              <button v-if="order.status === '待收货'" class="action-btn" @click="confirmReceipt(order.id)">确认收货</button>
              <button v-if="order.status === '待付款'" class="action-btn" @click="cancelOrder(order.id)">取消订单</button>
              <button class="action-btn" @click="viewLogistics(order.id)">查看物流</button>
              <button v-if="order.status === '已完成'" class="action-btn" @click="refundOrder(order.id)">退款</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 商品列表 -->
    <div class="products-container">
      <div class="products-header">
        <h2>商品列表</h2>
        <button class="hot-btn" @click="showHotProducts">热门商品</button>
      </div>
      
      <!-- 分类导航 -->
      <div class="categories-nav">
        <button 
          class="category-btn" 
          :class="{ active: selectedCategory === null }"
          @click="selectedCategory = null; loadProducts()"
        >
          全部商品
        </button>
        <button 
          v-for="category in categories" 
          :key="category.id"
          class="category-btn"
          :class="{ active: selectedCategory === category.id }"
          @click="loadProductsByCategory(category.id)"
        >
          {{ category.name }}
        </button>
      </div>
      
      <div class="products-grid">
        <div class="product-card" v-for="product in products" :key="product.id">
          <div class="product-image">
            <img :src="product.image" :alt="product.name" />
          </div>
          <div class="product-info">
            <h3>{{ product.name }}</h3>
            <p>{{ product.description }}</p>
            <div class="product-price">¥{{ product.price }}</div>
            <div class="product-stats">
              <span>销量: {{ product.sales }}</span>
              <span>库存: {{ product.stock }}</span>
              <span v-if="product.isHot" class="hot-tag">热门</span>
            </div>
            <button class="add-to-cart-btn" @click="addToCart(product.id)">加入购物车</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'App',
  data() {
    return {
      // 登录相关
      isLoggedIn: false,
      userInfo: {
        username: '',
        phone: ''
      },
      currentForm: 'login',
      loginForm: {
        username: '',
        password: ''
      },
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        phone: '',
        code: '',
        showPassword: false,
        showConfirmPassword: false
      },
      forgotForm: {
        phone: '',
        code: '',
        newPassword: ''
      },
      countdown: 0,
      timer: null,
      
      // 商城相关
      showCart: false,
      searchKeyword: '',
      products: [],
      categories: [],
      selectedCategory: null,
      cartItems: [],
      cartCount: 0,
      // 订单相关
      showOrderList: false,
      orders: [],
      orderStatusList: ['全部', '待付款', '待收货', '已完成', '已取消'],
      selectedOrderStatus: '全部'
    }
  },
  methods: {
    // 登录
    async handleLogin() {
      try {
        const response = await axios.post('http://localhost:8080/api/user/login', this.loginForm)
        if (response.data.code === 200) {
          this.isLoggedIn = true
          this.userInfo = response.data.data
          this.loadProducts()
          this.loadCategories()
          this.loadCart()
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        alert('登录失败，请稍后重试')
        console.error('登录失败:', error)
      }
    },
    
    // 注册
    async handleRegister() {
      try {
        // 验证密码和确认密码
        if (this.registerForm.password !== this.registerForm.confirmPassword) {
          alert('两次密码输入不一致，请重新输入')
          return
        }
        
        // 验证密码规则
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/
        if (!passwordRegex.test(this.registerForm.password)) {
          alert('密码必须包含大小写字母、数字和特殊字符，长度至少8位')
          return
        }
        
        // 先验证短信验证码
        const verifyResponse = await axios.post('http://localhost:8080/api/sms/verify-code', {
          phoneNumber: this.registerForm.phone,
          code: this.registerForm.code
        })
        
        if (verifyResponse.data.code !== 200) {
          alert(verifyResponse.data.message)
          return
        }
        
        // 验证码验证成功后进行注册
        const registerResponse = await axios.post('http://localhost:8080/api/user/register', this.registerForm)
        if (registerResponse.data.code === 200) {
          alert('注册成功！')
          this.currentForm = 'login'
        } else {
          alert(registerResponse.data.message)
        }
      } catch (error) {
        alert('注册失败，请稍后重试')
        console.error('注册失败:', error)
      }
    },
    
    // 发送验证码（忘记密码）
    async sendCode() {
      if (!this.forgotForm.phone) {
        alert('请输入手机号码')
        return
      }
      
      try {
        const response = await axios.post('http://localhost:8080/api/user/send-code', null, {
          params: { phone: this.forgotForm.phone }
        })
        if (response.data.code === 200) {
          alert('验证码已发送，请注意查收')
          this.startCountdown()
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        alert('发送验证码失败，请稍后重试')
        console.error('发送验证码失败:', error)
      }
    },
    
    // 发送验证码（注册）
    async sendRegisterCode() {
      if (!this.registerForm.phone) {
        alert('请输入手机号码')
        return
      }
      
      try {
        const response = await axios.post('http://localhost:8080/api/sms/send-code', {
          phoneNumber: this.registerForm.phone
        })
        if (response.data.code === 200) {
          alert('验证码已发送，请注意查收')
          this.startCountdown()
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        alert('发送验证码失败，请稍后重试')
        console.error('发送验证码失败:', error)
      }
    },
    
    // 开始倒计时
    startCountdown() {
      this.countdown = 60
      this.timer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--
        } else {
          clearInterval(this.timer)
        }
      }, 1000)
    },
    
    // 忘记密码
    async handleForgotPassword() {
      try {
        const response = await axios.post('http://localhost:8080/api/user/forgot-password', this.forgotForm)
        if (response.data.code === 200) {
          alert('密码重置成功！')
          this.currentForm = 'login'
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        alert('密码重置失败，请稍后重试')
        console.error('密码重置失败:', error)
      }
    },
    
    // 退出登录
    logout() {
      this.isLoggedIn = false
      this.userInfo = {}
      this.products = []
      this.cartItems = []
      this.cartCount = 0
    },
    
    // 加载分类列表
    async loadCategories() {
      try {
        const response = await axios.get('http://localhost:8080/api/category/list')
        if (response.data.code === 200) {
          this.categories = response.data.data
          console.log('分类数据:', this.categories)
        }
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    },
    
    // 加载商品列表
    async loadProducts() {
      try {
        const response = await axios.get('http://localhost:8080/api/product/list')
        if (response.data.code === 200) {
          this.products = response.data.data
        }
      } catch (error) {
        console.error('加载商品失败:', error)
      }
    },
    
    // 根据分类加载商品
    async loadProductsByCategory(categoryId) {
      this.selectedCategory = categoryId
      try {
        const response = await axios.get(`http://localhost:8080/api/category/products/${categoryId}`)
        if (response.data.code === 200) {
          this.products = response.data.data
        }
      } catch (error) {
        console.error('加载分类商品失败:', error)
      }
    },
    
    // 加载热门商品
    async showHotProducts() {
      this.selectedCategory = null
      try {
        const response = await axios.get('http://localhost:8080/api/product/hot')
        if (response.data.code === 200) {
          this.products = response.data.data
        }
      } catch (error) {
        console.error('加载热门商品失败:', error)
      }
    },
    
    // 搜索商品
    async searchProducts() {
      this.selectedCategory = null
      if (!this.searchKeyword) {
        this.loadProducts()
        return
      }
      try {
        const response = await axios.get('http://localhost:8080/api/product/search', {
          params: { keyword: this.searchKeyword }
        })
        if (response.data.code === 200) {
          this.products = response.data.data
        }
      } catch (error) {
        console.error('搜索商品失败:', error)
      }
    },
    
    // 加载购物车
    async loadCart() {
      try {
        const response = await axios.get('http://localhost:8080/api/cart/list', {
          params: { username: this.userInfo.username }
        })
        if (response.data.code === 200) {
          this.cartItems = response.data.data
          this.cartCount = this.cartItems.length
        }
      } catch (error) {
        console.error('加载购物车失败:', error)
      }
    },
    
    // 添加到购物车
    async addToCart(productId) {
      try {
        const response = await axios.post('http://localhost:8080/api/cart/add', null, {
          params: {
            username: this.userInfo.username,
            productId: productId,
            quantity: 1
          }
        })
        if (response.data.code === 200) {
          this.loadCart()
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        console.error('添加到购物车失败:', error)
      }
    },
    
    // 更新购物车数量
    async updateCartQuantity(productId, quantity) {
      try {
        const response = await axios.put('http://localhost:8080/api/cart/update', null, {
          params: {
            username: this.userInfo.username,
            productId: productId,
            quantity: quantity
          }
        })
        if (response.data.code === 200) {
          this.loadCart()
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        console.error('更新购物车失败:', error)
      }
    },
    
    // 从购物车移除
    async removeFromCart(productId) {
      try {
        const response = await axios.delete('http://localhost:8080/api/cart/remove', {
          params: {
            username: this.userInfo.username,
            productId: productId
          }
        })
        if (response.data.code === 200) {
          this.loadCart()
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        console.error('移除购物车商品失败:', error)
      }
    },
    
    // 购买商品
    async buyItems() {
      try {
        const address = prompt('请输入收货地址：')
        if (!address) return
        
        const response = await axios.post('http://localhost:8080/api/order/create', {
          username: this.userInfo.username,
          address: address
        })
        if (response.data.code === 200) {
          alert('购买成功！')
          this.loadCart()
          this.loadProducts()
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        console.error('购买失败:', error)
        alert('购买失败，请稍后重试')
      }
    },
    
    // 加载订单列表
    async loadOrders() {
      try {
        const response = await axios.get('http://localhost:8080/api/order/list', {
          params: {
            username: this.userInfo.username,
            status: this.selectedOrderStatus === '全部' ? '' : this.selectedOrderStatus
          }
        })
        if (response.data.code === 200) {
          this.orders = response.data.data
        }
      } catch (error) {
        console.error('加载订单失败:', error)
      }
    },
    
    // 确认收货
    async confirmReceipt(orderId) {
      try {
        const response = await axios.post(`http://localhost:8080/api/order/confirm-receipt/${orderId}`)
        if (response.data.code === 200) {
          alert('确认收货成功！')
          this.loadOrders()
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        console.error('确认收货失败:', error)
        alert('确认收货失败，请稍后重试')
      }
    },
    
    // 查看物流
    async viewLogistics(orderId) {
      try {
        const response = await axios.get(`http://localhost:8080/api/order/logistics/${orderId}`)
        if (response.data.code === 200) {
          alert('物流信息：' + response.data.data)
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        console.error('查看物流失败:', error)
        alert('查看物流失败，请稍后重试')
      }
    },
    
    // 取消订单
    async cancelOrder(orderId) {
      try {
        const response = await axios.post(`http://localhost:8080/api/order/cancel/${orderId}`)
        if (response.data.code === 200) {
          alert('订单取消成功！')
          this.loadOrders()
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        console.error('取消订单失败:', error)
        alert('取消订单失败，请稍后重试')
      }
    },
    
    // 退款
    async refundOrder(orderId) {
      try {
        const reason = prompt('请输入退款原因：')
        if (!reason) return
        
        const response = await axios.post(`http://localhost:8080/api/order/refund/${orderId}`, {
          reason: reason
        })
        if (response.data.code === 200) {
          alert('退款申请成功！')
          this.loadOrders()
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        console.error('退款申请失败:', error)
        alert('退款申请失败，请稍后重试')
      }
    }
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  }
}
</script>

<style scoped>
/* 登录注册样式 */
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.form-container {
  background-color: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

input:focus {
  outline: none;
  border-color: #ff0000;
}

small {
  display: block;
  margin-top: 5px;
  font-size: 12px;
  color: #999;
}

.phone-code-container {
  display: flex;
  gap: 10px;
}

.phone-code-container input {
  flex: 1;
}

.code-btn {
  padding: 0 15px;
  background-color: #ff0000;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.code-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 密码容器样式 */
.password-container {
  display: flex;
  gap: 10px;
}

.password-container input {
  flex: 1;
}

/* 切换密码可见性按钮 */
.toggle-password-btn {
  padding: 0 15px;
  background-color: #f0f0f0;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.toggle-password-btn:hover {
  background-color: #e0e0e0;
}

.btn {
  width: 100%;
  padding: 12px;
  background-color: #ff0000;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 10px;
}

.btn:hover {
  background-color: #ff0000;
}

.form-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #666;
}

.form-footer a {
  color: #ff0000;
  text-decoration: none;
  margin-left: 5px;
}

.form-footer a:hover {
  text-decoration: underline;
}

.divider {
  margin: 0 10px;
}

/* 商城样式 */
.mall-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #ff0000;
  color: white;
  padding: 15px 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.nav-left h1 {
  margin: 0;
  font-size: 24px;
}

.nav-center {
  flex: 1;
  max-width: 500px;
  margin: 0 50px;
}

.search-box {
  display: flex;
  width: 100%;
}

.search-box input {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 4px 0 0 4px;
  font-size: 16px;
}

.search-btn {
  padding: 0 20px;
  background-color: #ff0000;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome {
  font-size: 16px;
}

.logout-btn, .cart-btn {
  padding: 8px 16px;
  background-color: #ff0000;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.logout-btn:hover, .cart-btn:hover {
  background-color: #ff0000;
}

/* 购物车和订单侧边栏 */
.cart-sidebar, .order-sidebar {
  position: fixed;
  top: 0;
  right: 0;
  width: 350px;
  height: 100vh;
  background-color: white;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.cart-header, .order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.cart-header h3, .order-header h3 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.cart-content, .order-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.empty-cart, .empty-order {
  text-align: center;
  color: #999;
  margin-top: 50px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.cart-item-info h4 {
  margin: 0 0 5px 0;
  font-size: 14px;
}

.cart-item-info p {
  margin: 0;
  color: #ff0000;
  font-weight: bold;
}

.cart-item-quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-item-quantity button {
  width: 24px;
  height: 24px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.remove-btn {
  background-color: #ff0000;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 5px 10px;
  cursor: pointer;
  font-size: 12px;
}

.cart-footer {
  padding: 20px;
  border-top: 1px solid #eee;
  background-color: white;
}

.buy-btn {
  width: 100%;
  padding: 12px;
  background-color: #ff0000;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s;
}

.buy-btn:hover {
  background-color: #ff0000;
}

/* 订单相关样式 */
.order-filter {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 5px 10px;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.filter-btn.active {
  background-color: #ff0000;
  color: white;
  border-color: #ff0000;
}

.order-list {
  margin-top: 20px;
}

.order-item {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
}

.order-header-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-number {
  font-size: 14px;
  color: #666;
}

.order-status {
  font-size: 14px;
  font-weight: bold;
  color: #ff0000;
}

.order-details {
  margin-bottom: 15px;
}

.order-details p {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
}

.order-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.action-btn {
  padding: 5px 10px;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.action-btn:hover {
  background-color: #f5f5f5;
}

/* 导航栏按钮样式 */
.order-btn {
  background-color: #ff0000;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 10px;
  font-size: 14px;
}

.order-btn:hover {
  background-color: #ff0000;
}

/* 商品列表 */
.products-container {
  padding: 30px;
}

.products-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.products-header h2 {
  margin: 0;
  color: #333;
}

.hot-btn {
  padding: 8px 16px;
  background-color: #ff0000;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 分类导航 */
.categories-nav {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.category-btn {
  padding: 8px 16px;
  background-color: white;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.category-btn:hover {
  background-color: #f5f5f5;
  border-color: #ff0000;
}

.category-btn.active {
  background-color: #ff0000;
  color: white;
  border-color: #ff0000;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.product-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: transform 0.2s;
}

.product-image {
  width: 100%;
  height: 200px;
  margin-bottom: 15px;
  overflow: hidden;
  border-radius: 4px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.product-info p {
  margin: 0 0 15px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #ff0000;
  margin-bottom: 10px;
}

.product-stats {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin-bottom: 15px;
}

.hot-tag {
  background-color: #FF5722;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 10px;
}

.add-to-cart-btn {
  width: 100%;
  padding: 10px;
  background-color: #ff0000;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.add-to-cart-btn:hover {
  background-color: #ff0000;
}
</style>
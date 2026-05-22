<template>
  <div class="profile-page">
    <PageHeader title="个人中心" subtitle="管理您的账户信息和资质认证" />

    <div class="profile-content">
      <div class="profile-sidebar">
        <GlassCard class="user-card">
          <div class="user-avatar">
            <el-upload
              class="avatar-upload"
              :show-file-list="false"
              :http-request="handleAvatarUpload"
              :before-upload="beforeAvatarUpload"
              accept="image/*"
            >
              <el-avatar :size="80" :src="avatarUrl" class="avatar-clickable">
                {{ authStore.user?.nickname?.charAt(0) }}
              </el-avatar>
              <el-button text type="primary" :loading="avatarUploading">更换头像</el-button>
            </el-upload>
          </div>
          <h3 class="user-name">{{ authStore.user?.nickname }}</h3>
          <p class="user-phone">{{ authStore.user?.phone }}</p>
          <StatusTag
            :text="verifyStatusText"
            :type="verifyStatusType"
            size="large"
          />
        </GlassCard>

        <GlassCard class="menu-card">
          <div class="menu-list">
            <div
              v-for="item in menuItems"
              :key="item.key"
              class="menu-item"
              :class="{ 'menu-item--active': activeMenu === item.key }"
              @click="activeMenu = item.key"
            >
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.label }}</span>
            </div>
          </div>
        </GlassCard>
      </div>

      <div class="profile-main">
        <!-- 基本信息 -->
        <GlassCard v-if="activeMenu === 'info'" title="基本信息">
          <el-form
            ref="infoFormRef"
            :model="infoForm"
            :rules="infoRules"
            label-width="100px"
            label-position="left"
          >
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="infoForm.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSaveInfo">保存修改</el-button>
            </el-form-item>
          </el-form>
        </GlassCard>

        <!-- 实名认证 -->
        <GlassCard v-if="activeMenu === 'verify'" title="飞行资质认证">
          <div v-if="qualification && qualification.auditStatus === 1" class="verify-success">
            <div class="verify-icon">
              <el-icon :size="48"><Select /></el-icon>
            </div>
            <h4>已完成资质认证</h4>
            <p>证件号码：{{ maskIdCard(qualification.certificateNo) }}</p>
            <p>证件类型：{{ qualification.certificateType || '飞行执照' }}</p>
            <p>有效期至：{{ qualification.validEndDate }}</p>
          </div>

          <div v-else-if="qualification && qualification.auditStatus === 0" class="verify-pending">
            <div class="verify-icon verify-icon--pending">
              <el-icon :size="48"><Clock /></el-icon>
            </div>
            <h4>认证审核中</h4>
            <p class="pending-tip">您提交的资质正在审核，请耐心等待</p>

            <div class="submitted-info">
              <h5>已提交的认证信息</h5>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">证件号码</span>
                  <span class="info-value">{{ qualification.certificateNo }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">证件类型</span>
                  <span class="info-value">{{ qualification.certificateType || '飞行执照' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">有效期</span>
                  <span class="info-value">{{ qualification.validStartDate }} 至 {{ qualification.validEndDate }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">提交时间</span>
                  <span class="info-value">{{ formatDateTime(qualification.createdTime) }}</span>
                </div>
              </div>
              <div v-if="qualification.certificateImage" class="certificate-image">
                <span class="info-label">资质证书</span>
                <el-image
                  :src="getImageUrl(qualification.certificateImage)"
                  :preview-src-list="[getImageUrl(qualification.certificateImage)]"
                  fit="cover"
                  class="cert-img"
                />
              </div>
            </div>
          </div>

          <div v-else-if="qualification && qualification.auditStatus === 2" class="verify-rejected">
            <div class="verify-icon verify-icon--rejected">
              <el-icon :size="48"><CloseBold /></el-icon>
            </div>
            <h4>认证未通过</h4>
            <p>原因：{{ qualification.auditRemark || '资料不完整，请重新提交' }}</p>
            <el-button type="primary" @click="handleResubmit">重新认证</el-button>
          </div>

          <el-form
            v-else
            ref="verifyFormRef"
            :model="verifyForm"
            :rules="verifyRules"
            label-width="120px"
            label-position="left"
          >
            <el-alert type="info" :closable="false" class="verify-alert">
              <template #default>
                完成飞行资质认证后方可租赁无人机设备，请确保信息真实有效
              </template>
            </el-alert>

            <el-form-item label="证件号码" prop="certificateNo">
              <el-input v-model="verifyForm.certificateNo" placeholder="请输入飞行执照号码" />
            </el-form-item>
            <el-form-item label="证件类型" prop="certificateType">
              <el-select v-model="verifyForm.certificateType" placeholder="请选择证件类型">
                <el-option label="CAAC无人机驾驶执照" value="CAAC无人机驾驶执照" />
                <el-option label="AOPA合格证" value="AOPA合格证" />
                <el-option label="UTC培训合格证" value="UTC培训合格证" />
                <el-option label="其他飞行资质" value="其他飞行资质" />
              </el-select>
            </el-form-item>
            <el-form-item label="有效期开始" prop="validStartDate">
              <el-date-picker
                v-model="verifyForm.validStartDate"
                type="date"
                placeholder="选择开始日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            <el-form-item label="有效期结束" prop="validEndDate">
              <el-date-picker
                v-model="verifyForm.validEndDate"
                type="date"
                placeholder="选择结束日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            <el-form-item label="资质证书" prop="certificateImage">
              <el-upload
                class="certificate-upload"
                action="#"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleCertificateChange"
                accept="image/*"
              >
                <div v-if="certificateUrl" class="certificate-preview">
                  <img :src="certificateUrl" alt="资质证书" />
                  <div class="preview-mask">点击更换</div>
                </div>
                <div v-else class="certificate-placeholder">
                  <el-icon :size="32"><Plus /></el-icon>
                  <span>上传资质证书</span>
                </div>
              </el-upload>
              <div class="upload-tip">支持jpg、png格式，大小不超过5MB</div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="handleSubmitVerify">
                提交认证
              </el-button>
            </el-form-item>
          </el-form>
        </GlassCard>

        <!-- 修改密码 -->
        <GlassCard v-if="activeMenu === 'password'" title="修改密码">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            label-position="left"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="changingPassword" @click="handleChangePassword">
                确认修改
              </el-button>
            </el-form-item>
          </el-form>
        </GlassCard>

        <!-- 我的订单 -->
        <GlassCard v-if="activeMenu === 'orders'" title="我的订单">
          <div v-loading="ordersLoading" class="orders-section">
            <div v-if="orderList.length === 0 && !ordersLoading" class="empty-state">
              <el-icon :size="48"><Document /></el-icon>
              <p>暂无订单</p>
              <el-button type="primary" @click="router.push('/drones')">去租赁</el-button>
            </div>
            <div v-else class="order-list">
              <div v-for="order in orderList" :key="order.id" class="order-item" @click="router.push(`/orders/${order.id}`)">
                <div class="order-header">
                  <span class="order-no">{{ order.orderNo }}</span>
                  <StatusTag :text="getOrderStatusText(order.orderStatus)" :type="getOrderStatusType(order.orderStatus)" size="small" />
                </div>
                <div class="order-body">
                  <span class="drone-model">{{ order.droneModel }}</span>
                  <span class="order-amount">¥{{ order.totalAmount }}</span>
                </div>
                <div class="order-footer">
                  <span class="order-time">{{ formatDateTime(order.createdTime) }}</span>
                </div>
              </div>
            </div>
            <div v-if="orderTotal > 5" class="view-more">
              <el-button text type="primary" @click="router.push('/orders')">查看全部订单</el-button>
            </div>
          </div>
        </GlassCard>

        <!-- 租赁历史 -->
        <GlassCard v-if="activeMenu === 'history'" title="租赁历史">
          <div v-loading="ordersLoading" class="history-section">
            <div v-if="completedOrders.length === 0 && !ordersLoading" class="empty-state">
              <el-icon :size="48"><List /></el-icon>
              <p>暂无租赁记录</p>
            </div>
            <el-timeline v-else>
              <el-timeline-item
                v-for="order in completedOrders"
                :key="order.id"
                :timestamp="formatDateTime(order.createdTime)"
                placement="top"
              >
                <div class="history-item" @click="router.push(`/orders/${order.id}`)">
                  <h4>{{ order.droneModel }}</h4>
                  <p>租赁周期：{{ formatDate(order.rentalStartTime) }} ~ {{ formatDate(order.rentalEndTime) }}</p>
                  <p>订单金额：¥{{ order.totalAmount }}</p>
                  <StatusTag :text="getOrderStatusText(order.orderStatus)" :type="getOrderStatusType(order.orderStatus)" size="small" />
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </GlassCard>

        <!-- 诚信记录 -->
        <GlassCard v-if="activeMenu === 'credit'" title="诚信记录">
          <div class="credit-section">
            <div class="credit-overview">
              <div class="credit-score">
                <div class="score-circle" :class="creditStatusClass">
                  <span class="score-value">{{ creditScoreText }}</span>
                </div>
                <p class="score-label">诚信等级</p>
              </div>
              <div class="credit-info">
                <div class="credit-item">
                  <el-icon :color="authStore.user?.creditStatus === 1 ? '#22c55e' : '#ef4444'">
                    <component :is="authStore.user?.creditStatus === 1 ? Select : CloseBold" />
                  </el-icon>
                  <span>账户状态：{{ authStore.user?.creditStatus === 1 ? '正常' : '受限' }}</span>
                </div>
                <div class="credit-item">
                  <el-icon color="#3b82f6"><Document /></el-icon>
                  <span>历史订单：{{ orderTotal }} 笔</span>
                </div>
                <div class="credit-item">
                  <el-icon color="#f59e0b"><ChatDotRound /></el-icon>
                  <span>评价次数：{{ commentTotal }} 次</span>
                </div>
              </div>
            </div>
            <el-divider />
            <div class="credit-tips">
              <h5>诚信守则</h5>
              <ul>
                <li>按时归还租赁设备</li>
                <li>爱护设备，避免损坏</li>
                <li>如实反馈设备问题</li>
                <li>遵守飞行安全规定</li>
              </ul>
            </div>
          </div>
        </GlassCard>

        <!-- 我的评论 -->
        <GlassCard v-if="activeMenu === 'comments'" title="我的评论">
          <div v-loading="commentsLoading" class="comments-section">
            <div v-if="commentList.length === 0 && !commentsLoading" class="empty-state">
              <el-icon :size="48"><ChatDotRound /></el-icon>
              <p>暂无评论</p>
            </div>
            <div v-else class="comment-list">
              <div v-for="comment in commentList" :key="comment.id" class="comment-item">
                <div class="comment-header">
                  <el-rate :model-value="comment.rating" disabled />
                  <span class="comment-time">{{ formatDateTime(comment.createdTime) }}</span>
                </div>
                <p class="comment-content">{{ comment.content }}</p>
                <div class="comment-footer">
                  <span class="comment-drone">设备：{{ comment.droneModel || '无人机' }}</span>
                  <el-button text type="danger" size="small" @click="handleDeleteComment(comment.id)">删除</el-button>
                </div>
              </div>
            </div>
            <el-pagination
              v-if="commentTotal > 10"
              v-model:current-page="commentPage"
              :total="commentTotal"
              :page-size="10"
              layout="prev, pager, next"
              @current-change="fetchMyComments"
              class="pagination"
            />
          </div>
        </GlassCard>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { User, CircleCheck, Lock, Select, Clock, CloseBold, Plus, Document, Medal, List, ChatDotRound } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/common/PageHeader.vue'
import GlassCard from '@/components/common/GlassCard.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { useAuthStore } from '@/stores/auth'
import { updateUserInfo, updatePassword, submitQualification, uploadCertificate, getQualification, getMyOrders } from '@/api/user'
import { upload } from '@/api/request'
import { getMyComments, deleteMyComment } from '@/api/comment'

const router = useRouter()
const authStore = useAuthStore()

const activeMenu = ref('info')
const saving = ref(false)
const submitting = ref(false)
const changingPassword = ref(false)
const avatarUploading = ref(false)
const certificateUrl = ref('')
const certificateFile = ref(null)
const qualification = ref(null)

// 头像URL计算
const avatarUrl = computed(() => {
  const avatar = authStore.user?.avatar
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  return `/api${avatar}`
})

// 订单相关
const ordersLoading = ref(false)
const orderList = ref([])
const orderTotal = ref(0)
const orderPage = ref(1)

// 评论相关
const commentsLoading = ref(false)
const commentList = ref([])
const commentTotal = ref(0)
const commentPage = ref(1)

const menuItems = [
  { key: 'info', label: '基本信息', icon: markRaw(User) },
  { key: 'verify', label: '实名认证', icon: markRaw(CircleCheck) },
  { key: 'orders', label: '我的订单', icon: markRaw(Document) },
  { key: 'history', label: '租赁历史', icon: markRaw(List) },
  { key: 'credit', label: '诚信记录', icon: markRaw(Medal) },
  { key: 'comments', label: '我的评论', icon: markRaw(ChatDotRound) },
  { key: 'password', label: '修改密码', icon: markRaw(Lock) }
]

const verifyStatusText = computed(() => {
  if (!qualification.value) return '未认证'
  const statusMap = {
    0: '审核中',
    1: '已认证',
    2: '认证失败'
  }
  return statusMap[qualification.value.auditStatus] || '未认证'
})

const verifyStatusType = computed(() => {
  if (!qualification.value) return 'default'
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'error'
  }
  return typeMap[qualification.value.auditStatus] || 'default'
})

// 已完成的订单（用于租赁历史）
const completedOrders = computed(() => {
  return orderList.value.filter(o => o.orderStatus >= 4)
})

// 诚信等级
const creditScoreText = computed(() => {
  return authStore.user?.creditStatus === 1 ? '良好' : '受限'
})

const creditStatusClass = computed(() => {
  return authStore.user?.creditStatus === 1 ? 'score-good' : 'score-bad'
})

// 订单状态映射
const getOrderStatusText = (status) => {
  const map = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '租赁中', 4: '已归还', 5: '已取消', 6: '已退款' }
  return map[status] || '未知'
}

const getOrderStatusType = (status) => {
  const map = { 0: 'warning', 1: 'info', 2: 'info', 3: 'primary', 4: 'success', 5: 'default', 6: 'default' }
  return map[status] || 'default'
}

// 格式化日期
const formatDate = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.substring(0, 10)
}

// 基本信息表单
const infoFormRef = ref(null)
const infoForm = reactive({
  nickname: '',
  phone: '',
  email: ''
})

const infoRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度为2-20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 实名认证表单
const verifyFormRef = ref(null)
const verifyForm = reactive({
  certificateNo: '',
  certificateType: '',
  certificateImage: '',
  validStartDate: '',
  validEndDate: ''
})

const verifyRules = {
  certificateNo: [
    { required: true, message: '请输入证件号码', trigger: 'blur' }
  ],
  certificateType: [
    { required: true, message: '请选择证件类型', trigger: 'change' }
  ],
  validStartDate: [
    { required: true, message: '请选择有效期开始日期', trigger: 'change' }
  ],
  validEndDate: [
    { required: true, message: '请选择有效期结束日期', trigger: 'change' }
  ]
}

// 修改密码表单
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const maskIdCard = (idCard) => {
  if (!idCard) return ''
  return idCard.replace(/^(.{4})(.+)(.{4})$/, '$1****$3')
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.replace('T', ' ').substring(0, 19)
}

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `/api${url}`
}

// 头像上传前校验
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}

// 头像上传处理
const handleAvatarUpload = async (options) => {
  const { file } = options
  avatarUploading.value = true
  try {
    // 上传图片
    const uploadRes = await upload('/common/upload', file)
    const avatarPath = uploadRes.data

    // 更新用户头像
    await updateUserInfo({ avatar: avatarPath })
    authStore.updateUserInfo({ avatar: avatarPath })
    ElMessage.success('头像更新成功')
  } catch (error) {
    ElMessage.error('头像上传失败')
  } finally {
    avatarUploading.value = false
  }
}

const handleSaveInfo = async () => {
  if (!infoFormRef.value) return

  await infoFormRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      await updateUserInfo(infoForm)
      authStore.updateUserInfo(infoForm)
      ElMessage.success('保存成功')
    } catch (error) {
      // 错误已处理
    } finally {
      saving.value = false
    }
  })
}

const handleCertificateChange = (file) => {
  if (file.raw.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过5MB')
    return
  }
  certificateFile.value = file.raw
  certificateUrl.value = URL.createObjectURL(file.raw)
}

// 获取资质信息
const fetchQualification = async () => {
  try {
    const res = await getQualification()
    qualification.value = res.data
  } catch (error) {
    // 未提交过资质，忽略错误
    qualification.value = null
  }
}

// 重新提交认证
const handleResubmit = () => {
  qualification.value = null
}

const handleSubmitVerify = async () => {
  if (!verifyFormRef.value) return

  await verifyFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      let certificateImageUrl = ''

      // 如果有上传证书图片，先上传
      if (certificateFile.value) {
        const uploadRes = await uploadCertificate(certificateFile.value)
        certificateImageUrl = uploadRes.data
      }

      // 提交认证
      await submitQualification({
        ...verifyForm,
        certificateImage: certificateImageUrl
      })

      ElMessage.success('认证资料已提交，请等待审核')
      fetchQualification()
    } catch (error) {
      // 错误已处理
    } finally {
      submitting.value = false
    }
  })
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    changingPassword.value = true
    try {
      await updatePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      })
      ElMessage.success('密码修改成功')
      passwordFormRef.value.resetFields()
    } catch (error) {
      // 错误已处理
    } finally {
      changingPassword.value = false
    }
  })
}

// 获取我的订单
const fetchMyOrders = async () => {
  ordersLoading.value = true
  try {
    const res = await getMyOrders({ page: orderPage.value, pageSize: 10 })
    orderList.value = res.data?.records || []
    orderTotal.value = res.data?.total || 0
  } catch (error) {
    console.error('获取订单失败:', error)
  } finally {
    ordersLoading.value = false
  }
}

// 获取我的评论
const fetchMyComments = async () => {
  commentsLoading.value = true
  try {
    const res = await getMyComments({ pageNum: commentPage.value, pageSize: 10 })
    commentList.value = res.data?.records || []
    commentTotal.value = res.data?.total || 0
  } catch (error) {
    console.error('获取评论失败:', error)
  } finally {
    commentsLoading.value = false
  }
}

// 删除评论
const handleDeleteComment = async (id) => {
  try {
    await deleteMyComment(id)
    ElMessage.success('删除成功')
    fetchMyComments()
  } catch (error) {
    // 错误已处理
  }
}

onMounted(async () => {
  // 刷新用户信息以获取最新的诚信状态等数据
  await authStore.fetchUserInfo()

  if (authStore.user) {
    infoForm.nickname = authStore.user.nickname || ''
    infoForm.phone = authStore.user.phone || ''
    infoForm.email = authStore.user.email || ''
  }
  fetchQualification()
  fetchMyOrders()
  fetchMyComments()
})
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100%;
}

.profile-content {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.profile-sidebar {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
  position: sticky;
  top: 96px;
}

.profile-main {
  flex: 1;
  min-width: 0;
}

// 用户卡片
.user-card {
  text-align: center;
  padding: 32px;
}

.user-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;

  :deep(.el-upload) {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }
}

.avatar-clickable {
  cursor: pointer;
  transition: all 0.3s;
  border: 3px solid transparent;

  &:hover {
    border-color: #3b82f6;
    transform: scale(1.05);
  }
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 4px;
}

.user-phone {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 16px;
}

// 菜单卡片
.menu-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 12px;
  font-size: 15px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    color: #0f172a;
    background: #e2e8f0;
  }

  &--active {
    color: #3b82f6;
    background: rgba(59, 130, 246, 0.15);
  }
}

// 表单样式
:deep(.el-form) {
  max-width: 500px;
}

// 认证成功
.verify-success,
.verify-pending,
.verify-rejected {
  text-align: center;
  padding: 40px;

  h4 {
    font-size: 20px;
    font-weight: 600;
    color: #0f172a;
    margin: 20px 0 12px;
  }

  p {
    font-size: 14px;
    color: #64748b;
    margin: 0 0 8px;
  }
}

.verify-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(34, 197, 94, 0.2);
  color: #22c55e;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;

  &--pending {
    background: rgba(245, 158, 11, 0.2);
    color: #f59e0b;
  }

  &--rejected {
    background: rgba(239, 68, 68, 0.2);
    color: #ef4444;
  }
}

.verify-alert {
  margin-bottom: 24px;
}

// 已提交的认证信息
.pending-tip {
  margin-bottom: 24px !important;
}

.submitted-info {
  text-align: left;
  margin-top: 24px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;

  h5 {
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
    margin: 0 0 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #e2e8f0;
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 13px;
  color: #64748b;
}

.info-value {
  font-size: 14px;
  color: #0f172a;
  font-weight: 500;
}

.certificate-image {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;

  .cert-img {
    width: 200px;
    height: 140px;
    border-radius: 8px;
    cursor: pointer;
  }
}

// 证书上传
.certificate-upload {
  width: 200px;
  height: 140px;
}

.certificate-placeholder {
  width: 200px;
  height: 140px;
  border: 2px dashed rgba(148, 163, 184, 0.3);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    border-color: #3b82f6;
    color: #3b82f6;
  }
}

.certificate-preview {
  width: 200px;
  height: 140px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  cursor: pointer;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .preview-mask {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    opacity: 0;
    transition: opacity 0.3s;
  }

  &:hover .preview-mask {
    opacity: 1;
  }
}

.upload-tip {
  font-size: 12px;
  color: #64748b;
  margin-top: 8px;
}

// 通用空状态
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #64748b;

  p {
    margin: 16px 0;
    font-size: 14px;
  }
}

// 我的订单
.orders-section {
  min-height: 200px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: rgba(59, 130, 246, 0.1);
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.order-no {
  font-size: 13px;
  color: #64748b;
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.drone-model {
  font-size: 15px;
  font-weight: 500;
  color: #0f172a;
}

.order-amount {
  font-size: 16px;
  font-weight: 600;
  color: #3b82f6;
}

.order-footer {
  .order-time {
    font-size: 12px;
    color: #64748b;
  }
}

.view-more {
  text-align: center;
  margin-top: 16px;
}

// 租赁历史
.history-section {
  min-height: 200px;
}

.history-item {
  background: #f8fafc;
  padding: 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: rgba(59, 130, 246, 0.1);
  }

  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
    margin: 0 0 8px;
  }

  p {
    font-size: 13px;
    color: #64748b;
    margin: 0 0 4px;
  }
}

// 诚信记录
.credit-section {
  padding: 20px 0;
}

.credit-overview {
  display: flex;
  gap: 40px;
  align-items: center;
}

.credit-score {
  text-align: center;
}

.score-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;

  &.score-good {
    background: rgba(34, 197, 94, 0.2);
    border: 3px solid #22c55e;
  }

  &.score-bad {
    background: rgba(239, 68, 68, 0.2);
    border: 3px solid #ef4444;
  }
}

.score-value {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
}

.score-label {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.credit-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.credit-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  color: #0f172a;
}

.credit-tips {
  h5 {
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
    margin: 0 0 12px;
  }

  ul {
    margin: 0;
    padding-left: 20px;
  }

  li {
    font-size: 14px;
    color: #64748b;
    line-height: 2;
  }
}

// 我的评论
.comments-section {
  min-height: 200px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.comment-time {
  font-size: 12px;
  color: #64748b;
}

.comment-content {
  font-size: 14px;
  color: #0f172a;
  line-height: 1.6;
  margin: 0 0 12px;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-drone {
  font-size: 13px;
  color: #64748b;
}

.pagination {
  margin-top: 20px;
  justify-content: center;
}

// 响应式
@media (max-width: 1024px) {
  .profile-content {
    flex-direction: column;
  }

  .profile-sidebar {
    width: 100%;
    position: static;
  }

  .menu-list {
    flex-direction: row;
    overflow-x: auto;
  }

  .menu-item {
    white-space: nowrap;
  }

  .credit-overview {
    flex-direction: column;
    gap: 24px;
  }
}
</style>

// pages/order-detail/order-detail.js
const { getOrderDetailByOrderNo, cancelOrder, payOrder, confirmReceipt } = require('../../api/order.js');

Page({
  data: {
    orderNo: null,
    order: null,
    step: 0, // 订单进度：0-下单, 1-支付, 2-发货, 3-完成
    loading: true
  },

  onLoad(options) {
    if (options.orderNo) {
      this.setData({ orderNo: options.orderNo });
      this.loadOrderDetail();
    } else if (options.id) {
      // 兼容旧版 id 参数
      this.setData({ orderNo: options.id });
      this.loadOrderDetail();
    }
  },

  onShow() {
    // 每次显示页面时刷新订单数据
    if (this.data.orderNo) {
      this.loadOrderDetail();
    }
  },

  // 加载订单详情
  async loadOrderDetail() {
    try {
      this.setData({ loading: true });

      const res = await getOrderDetailByOrderNo(this.data.orderNo);

      if (res.code === 200) {
        const order = this.processOrder(res.data);
        const step = this.getOrderStep(order.orderStatus);

        console.log('订单状态:', order.orderStatus, '进度步骤:', step);

        this.setData({
          order: order,
          step: step
        });
      } else {
        wx.showToast({
          title: res.message || '加载失败',
          icon: 'none'
        });
        // 加载失败后返回
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      }
    } catch (err) {
      console.error('加载订单详情失败:', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } finally {
      this.setData({ loading: false });
    }
  },

  // 处理订单数据
  processOrder(order) {
    console.log('原始订单状态:', order.orderStatus);

    // 订单状态映射（后端返回的是中文字符串）
    const statusMap = {
      '待付款': { text: '待付款', class: 'pending-payment', tip: '请尽快完成支付', code: 0 },
      'PENDING_PAYMENT': { text: '待付款', class: 'pending-payment', tip: '请尽快完成支付', code: 0 },
      '待发货': { text: '待发货', class: 'pending-ship', tip: '商家正在备货发货', code: 1 },
      'PENDING_SHIPMENT': { text: '待发货', class: 'pending-ship', tip: '商家正在备货发货', code: 1 },
      '已发货': { text: '待收货', class: 'shipped', tip: '商品正在配送中', code: 2 },
      'SHIPPED': { text: '待收货', class: 'shipped', tip: '商品正在配送中', code: 2 },
      '已完成': { text: '已完成', class: 'completed', tip: '订单已完成', code: 3 },
      'COMPLETED': { text: '已完成', class: 'completed', tip: '订单已完成', code: 3 },
      '已取消': { text: '已取消', class: 'cancelled', tip: '订单已取消', code: 4 },
      'CANCELLED': { text: '已取消', class: 'cancelled', tip: '订单已取消', code: 4 }
    };

    const statusInfo = statusMap[order.orderStatus] || { text: '未知', class: '', tip: '', code: -1 };

    console.log('状态映射结果:', statusInfo);

    return Object.assign({}, order, {
      statusText: statusInfo.text,
      statusClass: statusInfo.class,
      statusTip: statusInfo.tip,
      orderStatus: statusInfo.code,
      items: order.items
    });
  },

  // 获取订单进度步骤
  getOrderStep(orderStatus) {
    // 0-待付款 1-待发货 2-待收货 3-已完成 4-已取消
    if (orderStatus === 4) return -1; // 已取消
    return orderStatus;
  },

  // 取消订单
  cancelOrder() {
    wx.showModal({
      title: '提示',
      content: '确定要取消这个订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '取消中...' });
            const result = await cancelOrder(this.data.order.id);

            if (result.code === 200) {
              wx.showToast({
                title: '订单已取消',
                icon: 'success'
              });

              // 刷新订单详情
              setTimeout(() => {
                this.loadOrderDetail();
              }, 1500);
            } else {
              wx.showToast({
                title: result.message || '取消失败',
                icon: 'none'
              });
            }
          } catch (err) {
            console.error('取消订单失败:', err);
            wx.showToast({
              title: '取消失败',
              icon: 'none'
            });
          } finally {
            wx.hideLoading();
          }
        }
      }
    });
  },

  // 支付订单
  payOrder() {
    const { order } = this.data;
    const amount = order.payAmount || order.actualAmount || order.totalAmount;

    console.log(order)

    // 跳转到支付页面
    wx.navigateTo({
      url: `/pages/payment/payment?orderId=${order.id}&orderNo=${order.orderNo}&amount=${amount}&createTime=${order.createTime}`
    });
  },

  // 确认收货
  confirmReceipt() {
    wx.showModal({
      title: '确认收货',
      content: '确认已收到商品？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '确认中...' });
            const result = await confirmReceipt(this.data.order.id);

            if (result.code === 200) {
              wx.showToast({
                title: '确认收货成功',
                icon: 'success'
              });

              // 刷新订单详情
              setTimeout(() => {
                this.loadOrderDetail();
              }, 1500);
            } else {
              wx.showToast({
                title: result.message || '确认失败',
                icon: 'none'
              });
            }
          } catch (err) {
            console.error('确认收货失败:', err);
            wx.showToast({
              title: '确认失败',
              icon: 'none'
            });
          } finally {
            wx.hideLoading();
          }
        }
      }
    });
  },

  // 复制订单号
  copyOrderNo() {
    wx.setClipboardData({
      data: this.data.order.orderNo,
      success: () => {
        wx.showToast({
          title: '订单号已复制',
          icon: 'success'
        });
      }
    });
  },

  // 复制快递单号
  copyExpressNo() {
    if (!this.data.order.expressNo) {
      wx.showToast({
        title: '暂无快递单号',
        icon: 'none'
      });
      return;
    }

    wx.setClipboardData({
      data: this.data.order.expressNo,
      success: () => {
        wx.showToast({
          title: '快递单号已复制',
          icon: 'success'
        });
      }
    });
  },

  // 预览图片
  previewImage(e) {
    const url = e.currentTarget.dataset.url;
    const photos = [];

    if (this.data.order.packageBeforeImage) {
      photos.push(this.data.order.packageBeforeImage);
    }
    if (this.data.order.packageAfterImage) {
      photos.push(this.data.order.packageAfterImage);
    }

    wx.previewImage({
      current: url,
      urls: photos
    });
  },

  // 联系客服
  contactService() {
    wx.showToast({
      title: '客服功能开发中',
      icon: 'none'
    });
  }
});

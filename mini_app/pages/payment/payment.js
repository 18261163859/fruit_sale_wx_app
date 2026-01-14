// pages/payment/payment.js
const { payOrder } = require('../../api/order.js');

Page({
  data: {
    orderId: null,
    orderNo: '',
    amount: '0.00',
    createTime: '',
    loading: false
  },

  onLoad(options) {
    if (!options.orderId) {
      wx.showToast({
        title: '订单信息错误',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
      return;
    }

    this.setData({
      orderId: options.orderId,
      orderNo: options.orderNo || '',
      amount: options.amount || '0.00',
      createTime: this.formatTime(options.createTime)
    });
  },

  // 格式化时间
  formatTime(timeStr) {
    if (!timeStr) {
      const now = new Date();
      return this.formatDate(now);
    }

    const date = new Date(timeStr);
    return this.formatDate(date);
  },

  formatDate(date) {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  },

  // 取消支付
  cancelPayment() {
    wx.showModal({
      title: '确认取消',
      content: '取消后可稍后在订单列表中继续支付',
      confirmText: '确认取消',
      cancelText: '继续支付',
      success: (res) => {
        if (res.confirm) {
          // 取消支付，返回订单列表
          wx.showToast({
            title: '已取消支付',
            icon: 'none',
            duration: 1500
          });

          setTimeout(() => {
            wx.redirectTo({
              url: '/pages/order-list/order-list?status=0'
            });
          }, 1500);
        }
      }
    });
  },

  // 确认支付
  async confirmPayment() {
    const { orderId } = this.data;

    try {
      this.setData({ loading: true });
      wx.showLoading({ title: '支付中...' });

      const res = await payOrder(orderId);

      if (res.code === 200) {
        wx.showToast({
          title: '支付成功',
          icon: 'success',
          duration: 2000
        });

        // 支付成功后跳转到订单详情
        setTimeout(() => {
          wx.redirectTo({
            url: `/pages/order-detail/order-detail?id=${orderId}`
          });
        }, 2000);
      } else {
        wx.showToast({
          title: res.message || '支付失败',
          icon: 'none'
        });
      }
    } catch (err) {
      console.error('支付失败:', err);
      wx.showToast({
        title: '支付失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
      wx.hideLoading();
    }
  }
});

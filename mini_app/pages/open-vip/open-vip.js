// pages/open-vip/open-vip.js
const { createVipOrder, payVipOrder } = require('../../api/vip.js');
const { getUserInfo } = require('../../api/user.js');

Page({
  data: {
    loading: false,
    paying: false,
    showPaymentModal: false,
    paymentType: 'wechat',
    orderNo: ''
  },

  async onLoad(options) {
    // 检查用户是否已经是VIP会员
    try {
      const res = await getUserInfo();
      if (res.code === 200 && res.data) {
        // 判断是否是VIP（基于 userType）
        const isVip = res.data.userType === 2;

        if (isVip) {
          wx.showToast({
            title: '您已经是星享会员了',
            icon: 'none',
            duration: 2000
          });
          setTimeout(() => {
            wx.navigateBack();
          }, 2000);
        }
      }
    } catch (err) {
      console.error('检查VIP状态失败:', err);
    }
  },

  // 开通会员
  async openVip() {
    if (this.data.loading) return;

    this.setData({ loading: true });

    try {
      // 创建订单
      const res = await createVipOrder();

      if (res.code === 200 && res.data) {
        this.setData({
          orderNo: res.data.orderNo,
          showPaymentModal: true
        });
      } else {
        wx.showToast({
          title: res.message || '创建订单失败',
          icon: 'none',
          duration: 2000
        });
      }
    } catch (err) {
      console.error('创建订单失败:', err);
      wx.showToast({
        title: err.message || '创建订单失败',
        icon: 'none',
        duration: 2000
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 选择支付方式
  selectPayment(e) {
    const { type } = e.currentTarget.dataset;
    this.setData({
      paymentType: type
    });
  },

  // 关闭支付弹窗
  closePaymentModal() {
    this.setData({
      showPaymentModal: false,
      paymentType: 'wechat'
    });
  },

  // 确认支付
  async confirmPayment() {
    if (this.data.paying) return;

    this.setData({ paying: true });

    try {
      const res = await payVipOrder({
        orderNo: this.data.orderNo,
        payType: this.data.paymentType
      });

      if (res.code === 200) {
        // 支付成功
        wx.showToast({
          title: '开通成功',
          icon: 'success',
          duration: 2000
        });

        // 延迟返回上一页并刷新
        setTimeout(() => {
          wx.navigateBack({
            delta: 1,
            success: () => {
              // 获取上一页实例并刷新数据
              const pages = getCurrentPages();
              const prevPage = pages[pages.length - 1];
              if (prevPage && prevPage.loadUserInfo) {
                prevPage.loadUserInfo();
              }
            }
          });
        }, 2000);
      } else {
        wx.showToast({
          title: res.message || '支付失败',
          icon: 'none',
          duration: 2000
        });
      }
    } catch (err) {
      console.error('支付失败:', err);
      wx.showToast({
        title: err.message || '支付失败',
        icon: 'none',
        duration: 2000
      });
    } finally {
      this.setData({
        paying: false,
        showPaymentModal: false
      });
    }
  }
});

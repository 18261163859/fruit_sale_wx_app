// pages/open-vip/open-vip.js
const app = getApp();
const { createVipOrder } = require('../../api/vip.js');
const { get } = require('../../utils/request.js');

Page({
  data: {
    loading: false,
    paying: false,
    showPaymentModal: false,
    paymentType: 'wechat',
    orderNo: '',
    payParams: null,
    isMockMode: true,
    amount: '199.00',
    vipPrice: '199.00',
    vipDurationDays: 365
  },

  async onLoad(options) {
    // 加载VIP配置
    await this.loadVipConfig();

    // 检查用户是否已经是VIP会员
    try {
      const res = await wx.request({
        url: `${app.globalData.baseUrl}/user/info`,
        method: 'GET',
        header: {
          'Authorization': 'Bearer ' + wx.getStorageSync('token')
        }
      });

      if (res.statusCode === 200 && res.data.code === 200) {
        const userData = res.data.data;
        // 判断是否是VIP（基于 userType）
        const isVip = userData.userType === 2;

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

  // 加载VIP配置
  async loadVipConfig() {
    try {
      const res = await get('/system-config/vip');

      if (res.code === 200 && res.data) {
        const vipPrice = res.data.vipPrice || '199.00';
        const vipDurationDays = res.data.vipDurationDays || 365;

        this.setData({
          vipPrice: vipPrice,
          amount: vipPrice,
          vipDurationDays: vipDurationDays
        });
      }
    } catch (err) {
      console.error('加载VIP配置失败:', err);
      // 使用默认值
    }
  },

  // 开通会员
  async openVip() {
    if (this.data.loading) return;

    this.setData({ loading: true });

    try {
      // 创建订单
      const res = await wx.request({
        url: `${app.globalData.baseUrl}/vip/order/create`,
        method: 'POST',
        header: {
          'Authorization': 'Bearer ' + wx.getStorageSync('token')
        }
      });

      if (res.statusCode === 200 && res.data.code === 200 && res.data.data) {
        this.setData({
          orderNo: res.data.data.orderNo,
          showPaymentModal: true
        });
      } else {
        wx.showToast({
          title: res.data.message || '创建订单失败',
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
      paymentType: 'wechat',
      payParams: null
    });
  },

  // 确认支付
  async confirmPayment() {
    if (this.data.paying) return;

    this.setData({ paying: true });

    try {
      // 先获取支付参数
      const payParamsRes = await wx.request({
        url: `${app.globalData.baseUrl}/pay/params/vip_${this.data.orderNo}`,
        method: 'POST',
        header: {
          'Authorization': 'Bearer ' + wx.getStorageSync('token')
        },
        data: {
          orderNo: this.data.orderNo,
          amount: this.data.amount
        }
      });

      if (payParamsRes.statusCode === 200 && payParamsRes.data.code === 200) {
        const payParams = payParamsRes.data.data;
        this.setData({
          payParams: payParams,
          isMockMode: payParams.mockMode === true
        });

        // 执行支付
        if (payParams.mockMode === true) {
          await this.mockVipPayment();
        } else {
          await this.realVipPayment(payParams);
        }
      } else {
        wx.showToast({
          title: payParamsRes.data.message || '获取支付参数失败',
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
  },

  // 模拟支付
  mockVipPayment() {
    return new Promise((resolve) => {
      setTimeout(async () => {
        // 调用后端确认支付
        await this.confirmVipPaymentToBackend();
        
        wx.showToast({
          title: '开通成功',
          icon: 'success',
          duration: 2000
        });

        setTimeout(() => {
          wx.navigateBack({
            delta: 1,
            success: () => {
              const pages = getCurrentPages();
              const prevPage = pages[pages.length - 1];
              if (prevPage && prevPage.loadUserInfo) {
                prevPage.loadUserInfo();
              }
            }
          });
        }, 2000);

        resolve();
      }, 1500);
    });
  },

  // 真实微信支付
  realVipPayment(payParams) {
    return new Promise((resolve, reject) => {
      wx.requestPayment({
        timeStamp: payParams.timeStamp,
        nonceStr: payParams.nonceStr,
        package: payParams.package,
        signType: payParams.signType,
        paySign: payParams.paySign,
        success: async (res) => {
          console.log('微信支付成功:', res);
          // 调用后端确认支付
          await this.confirmVipPaymentToBackend();
          
          wx.showToast({
            title: '开通成功',
            icon: 'success',
            duration: 2000
          });

          setTimeout(() => {
            wx.navigateBack({
              delta: 1,
              success: () => {
                const pages = getCurrentPages();
                const prevPage = pages[pages.length - 1];
                if (prevPage && prevPage.loadUserInfo) {
                  prevPage.loadUserInfo();
                }
              }
            });
          }, 2000);

          resolve(res);
        },
        fail: (err) => {
          console.error('微信支付失败:', err);
          if (err.errMsg && err.errMsg.indexOf('cancel') > -1) {
            wx.showToast({
              title: '已取消支付',
              icon: 'none'
            });
          } else {
            wx.showToast({
              title: '支付失败',
              icon: 'none'
            });
          }
          reject(err);
        }
      });
    });
  },

  // 调用后端确认会员支付
  async confirmVipPaymentToBackend() {
    try {
      const res = await wx.request({
        url: `${app.globalData.baseUrl}/vip/order/pay`,
        method: 'POST',
        header: {
          'Authorization': 'Bearer ' + wx.getStorageSync('token'),
          'Content-Type': 'application/json'
        },
        data: {
          orderNo: this.data.orderNo,
          payType: 'wechat'
        }
      });

      if (res.statusCode === 200 && res.data.code === 200) {
        return Promise.resolve();
      } else {
        return Promise.reject(new Error(res.data.message || '确认支付失败'));
      }
    } catch (err) {
      console.error('确认支付失败:', err);
      return Promise.reject(err);
    }
  }
});
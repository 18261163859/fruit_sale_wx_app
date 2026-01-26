// pages/open-vip/open-vip.js
const app = getApp();
const { createVipOrder, payVipOrder, checkVipStatus } = require('../../api/vip.js');
const { get, post } = require('../../utils/request.js');

Page({
  data: {
    loading: false,
    paying: false,
    showPaymentModal: false,
    orderNo: '',
    amount: '199.00',
    vipPrice: '199.00',
    vipDurationDays: 365
  },

  async onLoad(options) {
    // 加载VIP配置
    await this.loadVipConfig();

    // 检查用户是否已经是VIP会员
    await this.checkVipStatus();
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

  // 检查VIP状态
  async checkVipStatus() {
    try {
      const res = await get('/vip/status');

      if (res.code === 200 && res.data && res.data.isVip) {
        wx.showToast({
          title: '您已经是星享会员了',
          icon: 'none',
          duration: 2000
        });
        setTimeout(() => {
          wx.navigateBack();
        }, 2000);
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

  // 关闭支付弹窗
  closePaymentModal() {
    this.setData({
      showPaymentModal: false
    });
  },

  // 确认支付
  async confirmPayment() {
    if (this.data.paying) return;

    this.setData({ paying: true });
    wx.showLoading({ title: '支付中...' });

    try {
      // TODO: 真实微信支付
      // 1. 后端配置微信支付参数 (mchId, appId, privateKeyPath等)
      // 2. 调用后端获取支付参数: POST /pay/params/vip/${this.data.orderNo}
      // 3. 使用 wx.requestPayment 发起支付
      // 4. 支付成功后调用后端确认接口: POST /vip/order/pay

      // 当前默认使用模拟支付
      // await this.mockVipPayment();

      // 启用真实支付时，替换为以下代码:
      const payParamsRes = await post(`/pay/params/vip/${this.data.orderNo}`, {});
      if (payParamsRes.code === 200 && payParamsRes.data) {
        await this.realVipPayment(payParamsRes.data);
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
      wx.hideLoading();
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
      const res = await payVipOrder({
        orderNo: this.data.orderNo,
        payType: 'wechat'
      });

      if (res.code === 200) {
        return Promise.resolve();
      } else {
        return Promise.reject(new Error(res.message || '确认支付失败'));
      }
    } catch (err) {
      console.error('确认支付失败:', err);
      return Promise.reject(err);
    }
  }
});
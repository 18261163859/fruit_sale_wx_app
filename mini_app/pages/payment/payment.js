// pages/payment/payment.js
const app = getApp();
const { post } = require('../../utils/request.js');

Page({
  data: {
    orderId: null,
    orderNo: '',
    amount: '0.00',
    createTime: '',
    loading: false,
    payParams: null,
    isMockMode: true
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

    // 获取支付参数
    // this.getPayParams();
  },

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

  // 获取支付参数
  async getPayParams() {
    try {
      console.log(this.data.orderNo)
      const res = await post(`/pay/params/${this.data.orderId}`, {
        orderId: this.data.orderId,
        orderNo: this.data.orderNo,
        amount: this.data.amount
      }, { hideLoading: false });

      console.log('支付参数响应:', res);

      if (res.code === 200 && res.data) {
        this.setData({
          payParams: res.data,
          isMockMode: res.data.mockMode === true
        });
      } else {
        wx.showToast({
          title: res.message || '获取支付参数失败',
          icon: 'none'
        });
      }
    } catch (err) {
      console.error('获取支付参数失败:', err);
      wx.showToast({
        title: err.message || '网络错误',
        icon: 'none'
      });
    }
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
    const { payParams, orderId, isMockMode } = this.data;

    // if (!payParams) {
    //   wx.showToast({
    //     title: '支付参数获取中...',
    //     icon: 'none'
    //   });
    //   return;
    // }

    try {
      this.setData({ loading: true });
      wx.showLoading({ title: '支付中...' });
      await this.mockPayment();
      // if (isMockMode) {
      //   // 模拟支付模式
      //   await this.mockPayment();
      // } else {
      //   // 真实微信支付
      //   await this.realPayment(payParams);
      // }
    } catch (err) {
      console.error('支付失败:', err);
      wx.showToast({
        title: err.errMsg || '支付失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
      wx.hideLoading();
    }
  },

  // 模拟支付
  mockPayment() {
    return new Promise((resolve) => {
      setTimeout(() => {
        // 调用后端确认支付
        this.confirmPaymentToBackend().then(() => {
          wx.showToast({
            title: '支付成功',
            icon: 'success',
            duration: 2000
          });

          setTimeout(() => {
            wx.redirectTo({
              url: `/pages/order-detail/order-detail?orderNo=${this.data.orderNo}`
            });
          }, 2000);

          resolve();
        });
      }, 1500);
    });
  },

  // 真实微信支付
  realPayment(payParams) {
    return new Promise((resolve, reject) => {
      wx.requestPayment({
        timeStamp: payParams.timeStamp,
        nonceStr: payParams.nonceStr,
        package: payParams.package,
        signType: payParams.signType,
        paySign: payParams.paySign,
        success: (res) => {
          console.log('微信支付成功:', res);
          // 调用后端确认支付
          this.confirmPaymentToBackend().then(() => {
            wx.showToast({
              title: '支付成功',
              icon: 'success',
              duration: 2000
            });

            setTimeout(() => {
              wx.redirectTo({
                url: `/pages/order-detail/order-detail?orderNo=${this.data.orderNo}`
              });
            }, 2000);
          });
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

  // 调用后端确认支付
  async confirmPaymentToBackend() {
    try {
      // 使用封装好的 post 方法，而不是原生的 wx.request
      // 1. 不需要手动拼接 app.globalData.baseUrl (封装里通常有)
      // 2. 不需要手动加 header Token (封装里通常有)
      // 3. post 方法通常直接返回 res.data 部分（根据你 getPayParams 的写法推断）
      
      const res = await post(`/pay/confirm/${this.data.orderId}`, {}, { hideLoading: true });
      
      console.log('确认支付响应:', res);

      // 根据你 getPayParams 的逻辑，封装后的 res 直接就是后端返回的 JSON 对象
      if (res.code === 200) {
        return Promise.resolve(res);
      } else {
        return Promise.reject(new Error(res.message || '确认支付失败'));
      }
    } catch (err) {
      console.error('确认支付失败:', err);
      return Promise.reject(err);
    }
  }
});
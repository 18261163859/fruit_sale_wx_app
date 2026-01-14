const { get, post } = require('../../utils/request');

Page({
  data: {
    userInfo: {},
    cardCode: '',
    canRecharge: false,
    loading: false
  },

  onLoad() {
    this.getUserInfo();
  },

  onShow() {
    // 每次显示页面时刷新用户信息
    this.getUserInfo();
  },

  onPullDownRefresh() {
    this.getUserInfo().then(() => {
      wx.stopPullDownRefresh();
    });
  },

  // 获取用户信息
  getUserInfo() {
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo) {
      this.setData({
        userInfo: userInfo
      });
    }

    // 从服务器获取最新用户信息
    return get('/user/info')
      .then(res => {
        if (res.code === 200) {
          this.setData({
            userInfo: res.data
          });
          // 更新本地存储
          wx.setStorageSync('userInfo', res.data);
        }
      })
      .catch(err => {
        console.error('获取用户信息失败:', err);
      });
  },

  // 兑换码输入
  onCardCodeInput(e) {
    const cardCode = e.detail.value.trim();
    this.setData({
      cardCode: cardCode,
      canRecharge: cardCode.length === 16
    });
  },

  // 充值积分
  onRecharge() {
    const { cardCode, canRecharge, loading } = this.data;

    if (!canRecharge || loading) {
      return;
    }

    if (!cardCode || cardCode.length !== 16) {
      wx.showToast({
        title: '请输入16位兑换码',
        icon: 'none'
      });
      return;
    }

    this.setData({ loading: true });

    post('/user/integral/recharge', {}, {
      data: `cardNo=${cardCode}`,
      header: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
      .then(res => {
        if (res.code === 200) {
          wx.showToast({
            title: '充值成功',
            icon: 'success'
          });

          // 清空输入
          this.setData({
            cardCode: '',
            canRecharge: false,
            loading: false
          });

          // 刷新用户信息
          this.getUserInfo();

          // 1.5秒后跳转到积分记录页面
          setTimeout(() => {
            wx.navigateTo({
              url: '/pages/integral-records/integral-records'
            });
          }, 1500);
        }
      })
      .catch(err => {
        this.setData({ loading: false });
        console.error('充值失败:', err);

        // 显示具体错误信息
        const errorMsg = err.message || '充值失败，请重试';
        wx.showToast({
          title: errorMsg,
          icon: 'none',
          duration: 2000
        });
      });
  },

  // 跳转到积分记录页面
  goToRecords() {
    wx.navigateTo({
      url: '/pages/integral-records/integral-records'
    });
  }
});
const { get } = require('../../utils/request');

Page({
  data: {
    userInfo: {},
    records: [],
    filteredRecords: [],
    activeTab: 'all',
    loading: false
  },

  onLoad() {
    this.getUserInfo();
    this.getIntegralRecords();
  },

  onShow() {
    // 每次显示页面时刷新用户信息和记录
    this.getUserInfo();
    this.getIntegralRecords();
  },

  onPullDownRefresh() {
    this.getIntegralRecords().then(() => {
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
    get('/user/info')
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

  // 获取积分记录
  getIntegralRecords() {
    this.setData({ loading: true });

    return get('/user/integral/records')
      .then(res => {
        if (res.code === 200) {
          this.setData({
            records: res.data || [],
            loading: false
          });
          this.filterRecords();
        }
      })
      .catch(err => {
        console.error('获取积分记录失败:', err);
        this.setData({ loading: false });
        wx.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        });
      });
  },

  // 切换筛选标签
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({
      activeTab: tab
    });
    this.filterRecords();
  },

  // 筛选记录
  filterRecords() {
    const { records, activeTab } = this.data;
    let filteredRecords = [];

    switch (activeTab) {
      case 'all':
        filteredRecords = records;
        break;
      case 'income':
        // 收入：正数变动
        filteredRecords = records.filter(item => item.changeAmount > 0);
        break;
      case 'expense':
        // 支出：负数变动
        filteredRecords = records.filter(item => item.changeAmount < 0);
        break;
      default:
        filteredRecords = records;
    }

    this.setData({
      filteredRecords: filteredRecords
    });
  }
});
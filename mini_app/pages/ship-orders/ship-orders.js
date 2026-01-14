// pages/ship-orders/ship-orders.js
const { get } = require('../../utils/request.js');

Page({
  data: {
    orders: [],
    current: 1,
    size: 10,
    total: 0,
    loading: false,
    hasMore: true
  },

  onLoad() {
    this.loadOrders();
  },

  onShow() {
    // 从发货页面返回时刷新列表
    this.refreshOrders();
  },

  // 加载待发货订单列表
  async loadOrders() {
    if (this.data.loading || !this.data.hasMore) return;

    try {
      this.setData({ loading: true });

      const { current, size } = this.data;

      const res = await get('/order/pending-ship/list', {
        current,
        size
      });

      if (res.code === 200) {
        const newOrders = res.data.records || [];

        this.setData({
          orders: this.data.orders.concat(newOrders),
          total: res.data.total,
          hasMore: this.data.orders.length + newOrders.length < res.data.total
        });
      } else {
        wx.showToast({
          title: res.message || '加载失败',
          icon: 'none'
        });
      }
    } catch (err) {
      console.error('加载待发货订单失败:', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
      wx.stopPullDownRefresh();
    }
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.refreshOrders();
  },

  // 上拉加载更多
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ current: this.data.current + 1 });
      this.loadOrders();
    }
  },

  // 刷新订单列表
  refreshOrders() {
    this.setData({
      orders: [],
      current: 1,
      hasMore: true
    });
    this.loadOrders();
  },

  // 跳转到发货详情
  goToShipDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/ship-detail/ship-detail?orderId=${id}`
    });
  }
});

// pages/shipping-list/shipping-list.js
const { getPendingShipOrders, getShippedOrders } = require('../../api/order.js');
const { convertImageUrl } = require('../../utils/image.js');

Page({
  data: {
    activeTab: 'pending', // pending | shipped
    pendingList: [],
    shippedList: [],
    pendingPage: 1,
    shippedPage: 1,
    pendingHasMore: true,
    shippedHasMore: true,
    loading: false
  },

  onLoad() {
    this.loadPendingOrders();
  },

  onShow() {
    // 刷新当前tab数据
    if (this.data.activeTab === 'pending') {
      this.refreshPendingOrders();
    } else {
      this.refreshShippedOrders();
    }
  },

  // 切换Tab
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });

    if (tab === 'pending' && this.data.pendingList.length === 0) {
      this.loadPendingOrders();
    } else if (tab === 'shipped' && this.data.shippedList.length === 0) {
      this.loadShippedOrders();
    }
  },

  // 加载待发货订单
  async loadPendingOrders() {
    if (this.data.loading || !this.data.pendingHasMore) return;

    this.setData({ loading: true });

    try {
      const res = await getPendingShipOrders({
        current: this.data.pendingPage,
        size: 10
      });

      if (res.code === 200 && res.data) {
        const orders = res.data.records.map(order => {
          return Object.assign({}, order, {
            items: (order.items || []).map(item => Object.assign({}, item, {
              productImage: convertImageUrl(item.productImage, 'product')
            }))
          });
        });

        this.setData({
          pendingList: [...this.data.pendingList, ...orders],
          pendingPage: this.data.pendingPage + 1,
          pendingHasMore: orders.length >= 10
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
    }
  },

  // 加载已发货订单
  async loadShippedOrders() {
    if (this.data.loading || !this.data.shippedHasMore) return;

    this.setData({ loading: true });

    try {
      const res = await getShippedOrders({
        current: this.data.shippedPage,
        size: 10
      });

      if (res.code === 200 && res.data) {
        const orders = res.data.records.map(order => {
          return Object.assign({}, order, {
            items: (order.items || []).map(item => Object.assign({}, item, {
              productImage: convertImageUrl(item.productImage, 'product')
            }))
          });
        });

        this.setData({
          shippedList: [...this.data.shippedList, ...orders],
          shippedPage: this.data.shippedPage + 1,
          shippedHasMore: orders.length >= 10
        });
      }
    } catch (err) {
      console.error('加载已发货订单失败:', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 刷新待发货订单
  refreshPendingOrders() {
    this.setData({
      pendingList: [],
      pendingPage: 1,
      pendingHasMore: true
    });
    this.loadPendingOrders();
  },

  // 刷新已发货订单
  refreshShippedOrders() {
    this.setData({
      shippedList: [],
      shippedPage: 1,
      shippedHasMore: true
    });
    this.loadShippedOrders();
  },

  // 下拉刷新
  onPullDownRefresh() {
    if (this.data.activeTab === 'pending') {
      this.refreshPendingOrders();
    } else {
      this.refreshShippedOrders();
    }
    wx.stopPullDownRefresh();
  },

  // 触底加载更多
  onReachBottom() {
    if (this.data.activeTab === 'pending') {
      this.loadPendingOrders();
    } else {
      this.loadShippedOrders();
    }
  },

  // 去发货
  goToShip(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/shipping-detail/shipping-detail?orderId=${id}`
    });
  },

  // 查看详情
  goToDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/order-detail/order-detail?id=${id}`
    });
  }
});

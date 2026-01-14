// pages/order-list/order-list.js
const { getOrders, cancelOrder, payOrder, confirmReceipt } = require('../../api/order.js');

Page({
  data: {
    activeTab: 0,
    tabs: [
      { name: '全部', status: null, count: 0 },
      { name: '待付款', status: 0, count: 0 },
      { name: '待发货', status: 1, count: 0 },
      { name: '待收货', status: 2, count: 0 },
      { name: '已完成', status: 3, count: 0 }
    ],
    orders: [],
    current: 1,
    size: 10,
    total: 0,
    loading: false,
    hasMore: true
  },

  onLoad(options) {
    // 如果传入了状态参数，切换到对应tab
    if (options.status !== undefined) {
      const tabIndex = this.data.tabs.findIndex(tab => tab.status === parseInt(options.status));
      if (tabIndex !== -1) {
        this.setData({ activeTab: tabIndex });
      }
    }
    this.loadOrders();
  },

  onShow() {
    // 从订单详情返回时刷新列表
    this.refreshOrders();
  },

  // 切换Tab
  switchTab(e) {
    const index = e.currentTarget.dataset.index;
    if (index === this.data.activeTab) return;

    this.setData({
      activeTab: index,
      orders: [],
      current: 1,
      hasMore: true
    });
    this.loadOrders();
  },

  // 加载订单列表
  async loadOrders() {
    if (this.data.loading || !this.data.hasMore) return;

    try {
      this.setData({ loading: true });

      const { activeTab, tabs, current, size } = this.data;
      const status = tabs[activeTab].status;

      const params = {
        current,
        size
      };
      if (status !== null) {
        params.status = status;
      }

      const res = await getOrders(params);
      
      if (res.code === 200) {
        const newOrders = res.data.records || [];
        
        // 处理订单数据
        const processedOrders = newOrders.map(order => this.processOrder(order));

        this.setData({
          orders: this.data.orders.concat(processedOrders),
          total: res.data.total,
          hasMore: this.data.orders.length + processedOrders.length < res.data.total
        });
      } else {
        wx.showToast({
          title: res.message || '加载失败',
          icon: 'none'
        });
      }
    } catch (err) {
      console.error('加载订单列表失败:', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
      wx.stopPullDownRefresh();
    }
  },

  // 处理订单数据
  processOrder(order) {
    // 状态文本和样式（后端返回的是中文字符串）
    const statusMap = {
      '待付款': { text: '待付款', class: 'pending-payment' },
      'PENDING_PAYMENT': { text: '待付款', class: 'pending-payment' },
      '待发货': { text: '待发货', class: 'pending-ship' },
      'PENDING_SHIPMENT': { text: '待发货', class: 'pending-ship' },
      '已发货': { text: '待收货', class: 'shipped' },
      'SHIPPED': { text: '待收货', class: 'shipped' },
      '已完成': { text: '已完成', class: 'completed' },
      'COMPLETED': { text: '已完成', class: 'completed' },
      '已取消': { text: '已取消', class: 'cancelled' },
      'CANCELLED': { text: '已取消', class: 'cancelled' },
      // 兼容数字状态码
      0: { text: '待付款', class: 'pending-payment' },
      1: { text: '待发货', class: 'pending-ship' },
      2: { text: '待收货', class: 'shipped' },
      3: { text: '已完成', class: 'completed' },
      4: { text: '已取消', class: 'cancelled' }
    };

    const statusInfo = statusMap[order.orderStatus] || { text: '未知', class: '' };

    // 计算订单总商品数量
    const totalQuantity = order.items ? order.items.reduce((sum, item) => sum + item.quantity, 0) : 0;

    // 格式化时间
    const createTime = this.formatTime(order.createTime);

    return Object.assign({}, order, {
      statusText: statusInfo.text,
      statusClass: statusInfo.class,
      totalQuantity,
      createTime,
      items: order.items
    });
  },

  // 格式化时间
  formatTime(timeStr) {
    if (!timeStr) return '';

    const date = new Date(timeStr);
    const now = new Date();
    const diff = now - date;

    // 一分钟内
    if (diff < 60000) {
      return '刚刚';
    }

    // 一小时内
    if (diff < 3600000) {
      return Math.floor(diff / 60000) + '分钟前';
    }

    // 今天
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    if (date >= today) {
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `今天 ${hours}:${minutes}`;
    }

    // 昨天
    const yesterday = new Date(today - 86400000);
    if (date >= yesterday) {
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `昨天 ${hours}:${minutes}`;
    }

    // 其他
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');

    // 今年
    if (date.getFullYear() === now.getFullYear()) {
      return `${month}-${day} ${hours}:${minutes}`;
    }

    // 往年
    return `${date.getFullYear()}-${month}-${day}`;
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

  // 跳转到订单详情
  goToDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/order-detail/order-detail?id=${id}`
    });
  },

  // 取消订单
  cancelOrder(e) {
    const { id } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '提示',
      content: '确定要取消这个订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '取消中...' });
            const result = await cancelOrder(id);
            
            if (result.code === 200) {
              wx.showToast({
                title: '订单已取消',
                icon: 'success'
              });
              this.refreshOrders();
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

  // 去支付
  payOrder(e) {
    const { id } = e.currentTarget.dataset;
    const order = this.data.orders.find(o => o.id === id);

    if (!order) {
      wx.showToast({
        title: '订单不存在',
        icon: 'none'
      });
      return;
    }

    const amount = order.payAmount || order.actualAmount || order.totalAmount;

    // 跳转到支付页面
    wx.navigateTo({
      url: `/pages/payment/payment?orderId=${order.id}&orderNo=${order.orderNo}&amount=${amount}&createTime=${order.createTime}`
    });
  },

  // 确认收货
  confirmReceipt(e) {
    const { id } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '确认收货',
      content: '确认已收到商品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '确认中...' });
            const result = await confirmReceipt(id);
            
            if (result.code === 200) {
              wx.showToast({
                title: '确认收货成功',
                icon: 'success'
              });
              this.refreshOrders();
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
  }
});

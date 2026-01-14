// pages/cart/cart.js
const { getCartList, updateCartItem, removeCartItem } = require('../../api/cart.js');
const { convertImageUrl } = require('../../utils/image.js');
const { getUserInfo: getStorageUserInfo } = require('../../utils/storage.js');

Page({
  data: {
    cartList: [],
    totalPrice: 0,
    selectedCount: 0,
    isAllSelected: false,
    isVip: false  // 是否是VIP会员
  },

  onLoad() {
    this.checkUserVipStatus();
    this.loadCartList();
  },

  onShow() {
    this.checkUserVipStatus();
    this.loadCartList();
  },

  // 检查用户VIP状态
  checkUserVipStatus() {
    const userInfo = getStorageUserInfo();
    const isVip = userInfo && userInfo.userType === 2;
    this.setData({ isVip });
  },

  async loadCartList() {
    try {
      const res = await getCartList();
      if (res.code === 200 && res.data) {
        // 转换图片URL并默认全选
        const cartList = res.data.map(item => {
          return Object.assign({}, item, {
            productImage: convertImageUrl(item.productImage || item.coverImage || item.mainImage, 'product'),
            selected: true // 默认选中
          });
        });
        this.setData({
          cartList,
          isAllSelected: true
        });
        this.calculateTotal();
      }
    } catch (err) {
      console.error('加载购物车失败:', err);
    }
  },

  // 全选/取消全选
  onSelectAll(e) {
    const isAllSelected = e.detail.value.length > 0;
    const cartList = this.data.cartList.map(item => {
      return Object.assign({}, item, {
        selected: isAllSelected
      });
    });

    this.setData({
      cartList,
      isAllSelected
    });
    this.calculateTotal();
  },

  // 选择单个商品
  onSelectItem(e) {
    const id = parseInt(e.currentTarget.dataset.id);
    const isSelected = e.detail.value.length > 0;

    const cartList = this.data.cartList.map(item => {
      if (item.id === id) {
        return Object.assign({}, item, { selected: isSelected });
      }
      return item;
    });

    // 检查是否全选
    const isAllSelected = cartList.every(item => item.selected);

    this.setData({
      cartList,
      isAllSelected
    });
    this.calculateTotal();
  },

  calculateTotal() {
    const selectedItems = this.data.cartList.filter(item => item.selected);
    const total = selectedItems.reduce((sum, item) => {
      // 根据会员状态选择正确的价格
      const currentPrice = this.data.isVip && item.vipPrice ? item.vipPrice : item.price;
      return sum + currentPrice * item.quantity;
    }, 0);

    this.setData({
      totalPrice: total.toFixed(2),
      selectedCount: selectedItems.length
    });
  },

  async increaseQuantity(e) {
    const id = e.currentTarget.dataset.id;
    const item = this.data.cartList.find(item => item.id === id);
    if (item) {
      await this.updateQuantity(id, item.quantity + 1);
    }
  },

  async decreaseQuantity(e) {
    const id = e.currentTarget.dataset.id;
    const item = this.data.cartList.find(item => item.id === id);
    if (item) {
      if (item.quantity > 1) {
        await this.updateQuantity(id, item.quantity - 1);
      } else {
        await this.removeItem(id);
      }
    }
  },

  async updateQuantity(id, quantity) {
    try {
      await updateCartItem(id, quantity);
      this.loadCartList();
    } catch (err) {
      console.error('更新数量失败:', err);
      wx.showToast({
        title: '更新失败',
        icon: 'none'
      });
    }
  },

  async removeItem(id) {
    try {
      await removeCartItem(id);
      this.loadCartList();
    } catch (err) {
      console.error('删除失败:', err);
    }
  },

  goToIndex() {
    wx.switchTab({
      url: '/pages/index/index'
    });
  },

  goToProductDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/product-detail/product-detail?id=${id}`
    });
  },

  goToCheckout() {
    const selectedItems = this.data.cartList.filter(item => item.selected);

    // 验证是否有选中商品
    if (selectedItems.length === 0) {
      wx.showToast({
        title: '请选择商品',
        icon: 'none'
      });
      return;
    }

    // 准备商品数据
    const products = selectedItems.map(item => {
      // 根据会员状态选择正确的价格
      const currentPrice = this.data.isVip && item.vipPrice ? item.vipPrice : item.price;
      const originalPrice = item.originalPrice || item.price;

      return {
        cartId: item.id, // 添加购物车ID，用于后续删除
        productId: item.productId,
        productName: item.productName,
        productImage: item.productImage,
        specId: item.specId || null,
        specName: item.specName || '',
        price: currentPrice,        // 实际支付价格（会员价或普通价）
        originalPrice: originalPrice, // 原价
        quantity: item.quantity,
        stock: item.stock || 999
      };
    });

    // 编码并传递数据
    const productsStr = encodeURIComponent(JSON.stringify(products));

    wx.navigateTo({
      url: `/pages/checkout/checkout?products=${productsStr}&from=cart`
    });
  }
});

// pages/checkout/checkout.js
const { createOrder } = require('../../api/order.js');
const { getAddressList } = require('../../api/address.js');
const { removeCartItem } = require('../../api/cart.js');
const { getIntegralConfig, getShippingConfig } = require('../../api/config.js');

Page({
  data: {
    // 商品信息
    products: [],
    totalAmount: 0,
    originalAmount: 0, // 原价总额
    savedAmount: 0, // 节省金额

    // 收货地址
    selectedAddress: null,

    // 积分抵扣
    userIntegral: 0,
    useIntegral: 0,
    maxIntegralDeduct: 0,
    integralDeductAmount: 0,
    maxDeductAmountText: '0.00', // 预计算的最大抵扣金额文本

    // 积分配置
    integralExchangeRate: 100, // 积分兑换比例（100积分=1元）
    maxPointsDeductionRate: 30, // 最大积分抵扣比例（30%）

    // 运费配置
    defaultFreight: 15, // 基础运费（元）
    freeFreightThreshold: 199, // 包邮门槛（元）
    freightAmount: 0, // 运费金额
    remainingForFreeShipping: '0.00', // 距离包邮还需金额

    // 订单备注
    remark: '',

    // 最终金额
    actualAmount: 0,

    // 加载状态
    loading: false,

    // 来源标识
    from: '',

    // 购物车ID列表（用于订单创建后删除）
    cartIds: []
  },

  onLoad(options) {
    this.setData({ from: options.from || '' });

    // 获取积分配置和运费配置
    this.loadIntegralConfig();
    this.loadShippingConfig();

    // 从页面参数获取商品信息
    if (options.products) {
      try {
        const products = JSON.parse(decodeURIComponent(options.products));
        const normalizedProducts = this.normalizeProducts(products);

        // 提取购物车ID
        const cartIds = products
          .filter(item => item.cartId)
          .map(item => item.cartId);

        this.setData({
          products: normalizedProducts,
          cartIds
        });
        this.calculateTotal();
      } catch (err) {
        console.error('解析商品信息失败:', err);
        wx.showToast({
          title: '商品信息错误',
          icon: 'none'
        });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
        return;
      }
    } else {
      // 没有商品数据，返回上一页
      wx.showToast({
        title: '请先选择商品',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
      return;
    }

    // 获取用户积分（从本地存储或API获取）
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo && userInfo.integralBalance) {
      this.setData({ userIntegral: userInfo.integralBalance });
    }

    this.loadDefaultAddress();
  },

  onShow() {
    // 从地址页面返回时重新加载地址
    const selectedAddressId = wx.getStorageSync('selectedAddressId');
    if (selectedAddressId) {
      this.loadAddressById(selectedAddressId);
      wx.removeStorageSync('selectedAddressId');
    } else {
      this.loadDefaultAddress();
    }
  },

  // 标准化商品数据格式
  normalizeProducts(products) {
    return products.map(item => ({
      productId: item.productId || item.id,
      productName: item.productName || item.name,
      productImage: item.productImage || item.image,
      specId: item.specId,
      specName: item.specName,
      price: parseFloat(item.price || 0),
      originalPrice: parseFloat(item.originalPrice || item.price || 0),
      quantity: parseInt(item.quantity || 1),
      stock: item.stock || 999
    }));
  },

  // 加载默认收货地址
  async loadDefaultAddress() {
    try {
      const res = await getAddressList();

      if (res.code === 200 && res.data && res.data.length > 0) {
        // 查找默认地址
        const defaultAddress = res.data.find(addr => addr.isDefault === 1);
        this.setData({
          selectedAddress: defaultAddress || res.data[0]
        });
      }
    } catch (err) {
      console.error('加载地址失败:', err);
    }
  },

  // 加载积分配置
  async loadIntegralConfig() {
    try {
      const res = await getIntegralConfig();
      if (res.code === 200 && res.data) {
        this.setData({
          integralExchangeRate: res.data.integralExchangeRate || 100,
          maxPointsDeductionRate: res.data.maxPointsDeductionRate || 30
        });
        // 重新计算最大抵扣积分
        if (this.data.totalAmount > 0) {
          this.calculateTotal();
        }
      }
    } catch (err) {
      console.error('加载积分配置失败:', err);
      // 使用默认值
    }
  },

  // 加载运费配置
  async loadShippingConfig() {
    try {
      const res = await getShippingConfig();
      if (res.code === 200 && res.data) {
        this.setData({
          defaultFreight: parseFloat(res.data.defaultFreight) || 15,
          freeFreightThreshold: parseFloat(res.data.freeFreightThreshold) || 199
        });
        // 重新计算运费
        if (this.data.totalAmount > 0) {
          this.calculateTotal();
        }
      }
    } catch (err) {
      console.error('加载运费配置失败:', err);
      // 使用默认值
    }
  },

  // 根据ID加载地址
  async loadAddressById(addressId) {
    try {
      const res = await getAddressList();

      if (res.code === 200 && res.data && res.data.length > 0) {
        const address = res.data.find(addr => addr.id === addressId);
        if (address) {
          this.setData({ selectedAddress: address });
        } else {
          this.loadDefaultAddress();
        }
      }
    } catch (err) {
      console.error('加载地址失败:', err);
      this.loadDefaultAddress();
    }
  },

  // 计算商品总额
  calculateTotal() {
    const { products } = this.data;

    // 计算原价总额和实际总额
    let originalAmount = 0;
    let totalAmount = 0;

    products.forEach(item => {
      originalAmount += item.originalPrice * item.quantity;
      totalAmount += item.price * item.quantity;
    });

    const savedAmount = originalAmount - totalAmount;

    // 计算运费
    let freightAmount = 0;
    let remainingForFreeShipping = '0.00';
    if (totalAmount < this.data.freeFreightThreshold) {
      freightAmount = this.data.defaultFreight;
      remainingForFreeShipping = (this.data.freeFreightThreshold - totalAmount).toFixed(2);
    }

    // 积分最多可抵扣订单金额的配置比例（默认30%）
    // 先计算最大可抵扣金额（元），再转换为积分数
    const maxDeductAmount = totalAmount * this.data.maxPointsDeductionRate / 100;
    const maxIntegralDeduct = Math.floor(maxDeductAmount * this.data.integralExchangeRate);
    const maxDeductAmountText = maxDeductAmount.toFixed(2);

    this.setData({
      originalAmount: originalAmount.toFixed(2),
      totalAmount: totalAmount.toFixed(2),
      savedAmount: savedAmount.toFixed(2),
      freightAmount: freightAmount.toFixed(2),
      remainingForFreeShipping: remainingForFreeShipping,
      maxIntegralDeduct,
      maxDeductAmountText
    });

    this.calculateActualAmount();
  },

  // 计算实际支付金额
  calculateActualAmount() {
    const { totalAmount, integralDeductAmount, freightAmount } = this.data;
    const actualAmount = Math.max(0, parseFloat(totalAmount) - parseFloat(integralDeductAmount) + parseFloat(freightAmount));

    this.setData({
      actualAmount: actualAmount.toFixed(2)
    });
  },

  // 选择地址
  selectAddress() {
    wx.navigateTo({
      url: '/pages/address-list/address-list?from=checkout'
    });
  },

  // 新增地址
  addAddress() {
    wx.navigateTo({
      url: '/pages/address-edit/address-edit?from=checkout'
    });
  },

  // 积分输入
  onIntegralInput(e) {
    let useIntegral = parseInt(e.detail.value) || 0;
    this.updateIntegralDeduct(useIntegral);
  },

  // 更新积分抵扣
  updateIntegralDeduct(useIntegral) {
    const { userIntegral, maxIntegralDeduct } = this.data;

    // 限制积分使用数量
    if (useIntegral > userIntegral) {
      useIntegral = userIntegral;
      wx.showToast({
        title: '积分不足',
        icon: 'none',
        duration: 1500
      });
    }

    if (useIntegral > maxIntegralDeduct) {
      useIntegral = maxIntegralDeduct;
      wx.showToast({
        title: `最多可抵扣${maxIntegralDeduct}积分`,
        icon: 'none',
        duration: 1500
      });
    }

    // 积分抵扣金额（根据配置的兑换比例，默认100积分=1元）
    const integralDeductAmount = (useIntegral / this.data.integralExchangeRate).toFixed(2);

    this.setData({
      useIntegral,
      integralDeductAmount: parseFloat(integralDeductAmount)
    });

    this.calculateActualAmount();
  },

  // 全部抵扣
  useAllIntegral() {
    const { userIntegral, maxIntegralDeduct } = this.data;
    const useIntegral = Math.min(userIntegral, maxIntegralDeduct);
    this.updateIntegralDeduct(useIntegral);
  },

  // 备注输入
  onRemarkInput(e) {
    this.setData({
      remark: e.detail.value
    });
  },

  // 提交订单
  async submitOrder() {
    const { selectedAddress, products, useIntegral, remark } = this.data;

    // 验证收货地址
    if (!selectedAddress) {
      wx.showToast({
        title: '请选择收货地址',
        icon: 'none'
      });
      return;
    }

    // 验证商品
    if (!products || products.length === 0) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      });
      return;
    }

    // 验证商品库存
    const outOfStock = products.find(item => item.quantity > item.stock);
    if (outOfStock) {
      wx.showToast({
        title: `${outOfStock.productName} 库存不足`,
        icon: 'none'
      });
      return;
    }

    // 二次确认
    const confirmed = await new Promise(resolve => {
      wx.showModal({
        title: '确认下单',
        content: `确认支付 ¥${this.data.actualAmount} 吗？`,
        success: res => resolve(res.confirm),
        fail: () => resolve(false)
      });
    });

    if (!confirmed) return;

    try {
      this.setData({ loading: true });
      wx.showLoading({ title: '提交订单中...' });

      // 构建订单数据
      const orderData = {
        addressId: selectedAddress.id,
        items: products.map(item => ({
          productId: item.productId,
          specId: item.specId,
          quantity: item.quantity
        })),
        useIntegral: useIntegral || 0,
        remark: remark || ''
      };

      const res = await createOrder(orderData);

      if (res.code === 200) {
        const orderId = res.data;

        // 如果是从购物车来的，删除已结算的商品
        if (this.data.from === 'cart' && this.data.cartIds.length > 0) {
          this.clearCartItems(this.data.cartIds);
        }

        wx.showToast({
          title: '订单创建成功',
          icon: 'success',
          duration: 1500
        });

        // 延迟跳转到支付页面
        setTimeout(() => {
          wx.redirectTo({
            url: `/pages/payment/payment?orderId=${orderId}&amount=${this.data.actualAmount}&createTime=${new Date().toISOString()}`
          });
        }, 1500);
      } else {
        wx.showToast({
          title: res.message || '订单创建失败',
          icon: 'none'
        });
      }
    } catch (err) {
      console.error('提交订单失败:', err);
      wx.showToast({
        title: '订单创建失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
      wx.hideLoading();
    }
  },

  // 清空购物车中已结算的商品
  async clearCartItems(cartIds) {
    try {
      // 批量删除购物车商品
      const deletePromises = cartIds.map(id => removeCartItem(id));
      await Promise.all(deletePromises);
      console.log('购物车已结算商品已清除');
    } catch (err) {
      console.error('清除购物车失败:', err);
      // 不影响主流程，只记录错误
    }
  }
});

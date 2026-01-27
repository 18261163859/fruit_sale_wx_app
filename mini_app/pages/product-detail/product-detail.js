// pages/product-detail/product-detail.js
const { getProductDetail, getProductSpecs } = require('../../api/product.js');
const { addToCart } = require('../../api/cart.js');
const { convertImageUrl, convertImageUrls } = require('../../utils/image.js');
const { getUserInfo: getStorageUserInfo } = require('../../utils/storage.js');

Page({
  data: {
    productId: null,
    product: {},
    specs: [],
    selectedSpec: null,
    showSpecPopup: false,
    vipSaveAmount: '0.00',
    isVip: false  // 是否是VIP会员
  },

  onLoad(options) {
    this.checkUserVipStatus();
    if (options.id) {
      this.setData({ productId: options.id });
      this.loadProductDetail();
      this.loadProductSpecs();
    }
  },

  // 检查用户VIP状态
  checkUserVipStatus() {
    const userInfo = getStorageUserInfo();
    const isVip = userInfo && userInfo.userType === 2;
    this.setData({ isVip });
  },

  // 计算VIP节省金额
  calculateVipSave() {
    const { selectedSpec, product } = this.data;
    let saveAmount = '0.00';

    if (selectedSpec) {
      const specPrice = parseFloat(selectedSpec.price) || 0;
      const specVipPrice = parseFloat(selectedSpec.vipPrice) || 0;
      if (specVipPrice < specPrice) {
        saveAmount = (specPrice - specVipPrice).toFixed(2);
      }
    } else {
      const productPrice = parseFloat(product.price) || 0;
      const productVipPrice = parseFloat(product.vipPrice) || 0;
      if (productVipPrice < productPrice) {
        saveAmount = (productPrice - productVipPrice).toFixed(2);
      }
    }

    console.log('计算VIP节省:', { selectedSpec, product, saveAmount });
    this.setData({ vipSaveAmount: saveAmount });
  },

  async loadProductDetail() {
    try {
      const res = await getProductDetail(this.data.productId);
      if (res.code === 200 && res.data) {
        const product = Object.assign({}, res.data, {
          mainImage: convertImageUrl(res.data.mainImage || res.data.coverImage, 'product'),
          images: convertImageUrls(res.data.images || [res.data.mainImage], 'product'),
          productName: res.data.productName || res.data.name
        });
        this.setData({ product });
        this.calculateVipSave();
      }
    } catch (err) {
      console.error('加载商品详情失败:', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  },

  async loadProductSpecs() {
    try {
      const res = await getProductSpecs(this.data.productId);
      if (res.code === 200 && res.data) {
        const specs = res.data.filter(spec => spec.status === 1); // 只显示启用的规格

        // 处理价格字段，确保是数字类型
        const normalizedSpecs = specs.map(spec => ({
          ...spec,
          price: parseFloat(spec.price) || 0,
          vipPrice: parseFloat(spec.vipPrice) || 0,
          stock: parseInt(spec.stock) || 0
        }));

        this.setData({ specs: normalizedSpecs });

        // 如果有规格，默认选中第一个
        if (normalizedSpecs.length > 0) {
          this.setData({ selectedSpec: normalizedSpecs[0] });
          this.calculateVipSave();
        }
      }
    } catch (err) {
      console.error('加载商品规格失败:', err);
    }
  },

  // 显示规格选择器
  showSpecSelector() {
    if (this.data.specs.length === 0) {
      return;
    }
    this.setData({ showSpecPopup: true });
  },

  // 隐藏规格选择器
  hideSpecSelector() {
    this.setData({ showSpecPopup: false });
  },

  // 阻止冒泡
  stopPropagation() {
    // 阻止点击弹窗内容时关闭
  },

  // 选择规格
  selectSpec(e) {
    const spec = e.currentTarget.dataset.spec;
    if (spec.status === 0) {
      wx.showToast({
        title: '该规格已下架',
        icon: 'none'
      });
      return;
    }
    console.log('选择规格:', spec);
    this.setData({ selectedSpec: spec });
    this.calculateVipSave();
  },

  // 确认规格
  confirmSpec() {
    if (!this.data.selectedSpec) {
      wx.showToast({
        title: '请选择规格',
        icon: 'none'
      });
      return;
    }
    this.hideSpecSelector();
  },

  async addToCart() {
    // 如果有规格但未选择，先显示规格选择器
    if (this.data.specs.length > 0 && !this.data.selectedSpec) {
      this.showSpecSelector();
      return;
    }

    try {
      const params = {
        productId: this.data.productId,
        quantity: 1
      };

      if (this.data.selectedSpec) {
        params.specId = this.data.selectedSpec.id;
      }

      const res = await addToCart(params);
      if (res.code === 200) {
        wx.showToast({
          title: '已加入购物车',
          icon: 'success'
        });
      }
    } catch (err) {
      console.error('加入购物车失败:', err);
      wx.showToast({
        title: '添加失败',
        icon: 'none'
      });
    }
  },

  buyNow() {
    const { product, selectedSpec, specs, isVip } = this.data;

    // 如果有规格但未选择，先显示规格选择器
    if (specs.length > 0 && !selectedSpec) {
      this.showSpecSelector();
      wx.showToast({
        title: '请先选择规格',
        icon: 'none'
      });
      return;
    }

    // 根据VIP状态选择正确的价格
    let price, originalPrice;
    if (selectedSpec) {
      originalPrice = selectedSpec.price;
      // VIP用户使用VIP价，非VIP用户使用原价
      price = (isVip && selectedSpec.vipPrice) ? selectedSpec.vipPrice : selectedSpec.price;
    } else {
      originalPrice = product.price;
      price = (isVip && product.vipPrice) ? product.vipPrice : product.price;
    }

    // 准备商品数据
    const productData = {
      productId: product.id,
      productName: product.productName,
      productImage: product.mainImage,
      specId: selectedSpec ? selectedSpec.id : null,
      specName: selectedSpec ? selectedSpec.specName : '',
      price: price,
      originalPrice: originalPrice,
      quantity: 1,
      stock: selectedSpec ? selectedSpec.stock : product.stock
    };

    // 编码并传递数据
    const productsStr = encodeURIComponent(JSON.stringify([productData]));

    wx.navigateTo({
      url: `/pages/checkout/checkout?products=${productsStr}&from=product`
    });
  },

  onShareAppMessage() {
    const { product } = this.data;
    const userInfo = getStorageUserInfo();
    let path = `/pages/product-detail/product-detail?id=${product.id}`;
    if (userInfo && userInfo.inviteCode) {
      path += `&inviteCode=${userInfo.inviteCode}`;
    }

    return {
      title: product.productName || '植野集商品',
      path: path,
      imageUrl: product.mainImage
    };
  },

  onShareTimeline() {
    const { product } = this.data;
    const userInfo = getStorageUserInfo();
    let path = `/pages/product-detail/product-detail?id=${product.id}`;
    if (userInfo && userInfo.inviteCode) {
      path += `&inviteCode=${userInfo.inviteCode}`;
    }
    return {
      title: product.productName || '植野集商品',
      query: `id=${product.id}&inviteCode=${userInfo?.inviteCode || ''}`,
      imageUrl: product.mainImage
    };
  }
});

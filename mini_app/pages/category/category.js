// pages/category/category.js
const { getCategories, getProducts } = require('../../api/product.js');
const { addToCart } = require('../../api/cart.js');
const { convertImageUrl } = require('../../utils/image.js');
const { applyTheme } = require('../../utils/theme.js');
const { getUserInfo } = require('../../utils/storage.js');

Page({
  data: {
    categories: [],
    currentCategoryId: null,
    currentCategoryName: '',
    products: [],
    userInfo: null,
    themeConfig: {},
    currentTheme: 'normal',
    sortType: 0, // 0-综合 1-销量 2-价格从低到高 3-价格从高到低
    sortText: '综合排序'
  },

  onLoad() {
    this.initTheme();
    this.loadCategories();
  },

  onShow() {
    this.initTheme();
  },

  // 初始化主题
  async initTheme() {
    const userInfo = getUserInfo();
    await applyTheme(this, userInfo);
    this.setData({
      userInfo: userInfo
    });
  },

  // 加载分类列表
  async loadCategories() {
    try {
      const res = await getCategories();
      if (res.code === 200 && res.data && res.data.length > 0) {
        this.setData({
          categories: res.data,
          currentCategoryId: res.data[0].id,
          currentCategoryName: res.data[0].categoryName
        });
        // 加载第一个分类的商品
        this.loadProducts(res.data[0].id);
      }
    } catch (err) {
      console.error('加载分类失败:', err);
      wx.showToast({
        title: '加载分类失败',
        icon: 'none'
      });
    }
  },

  // 加载商品列表
  async loadProducts(categoryId) {
    try {
      const res = await getProducts({
        categoryId: categoryId,
        current: 1,
        size: 100
      });
      if (res.code === 200 && res.data) {
        const products = (res.data.records || []).map(product => {
          return {
            id: product.id,
            productNo: product.productNo,
            productName: product.productName || product.name,
            categoryId: product.categoryId,
            mainImage: convertImageUrl(product.mainImage || product.coverImage, 'product'),
            coverImage: product.coverImage,
            price: product.price,
            vipPrice: product.vipPrice,
            stock: product.stock,
            salesCount: product.salesCount,
            status: product.status,
            description: product.description
          };
        });
        this.setData({
          products
        });
      }
    } catch (err) {
      console.error('加载商品失败:', err);
      wx.showToast({
        title: '加载商品失败',
        icon: 'none'
      });
    }
  },

  // 点击分类
  onCategoryTap(e) {
    const categoryId = e.currentTarget.dataset.id;
    const category = this.data.categories.find(c => c.id === categoryId);

    this.setData({
      currentCategoryId: categoryId,
      currentCategoryName: category ? category.categoryName : '',
      products: []
    });
    this.loadProducts(categoryId);
  },

  // 加入购物车
  async addToCart(e) {
    const { id, name } = e.currentTarget.dataset;

    try {
      const res = await addToCart({
        productId: id,
        quantity: 1
      });

      if (res.code === 200) {
        wx.showToast({
          title: '已加入购物车',
          icon: 'success'
        });
      } else {
        wx.showToast({
          title: res.message || '添加失败',
          icon: 'none'
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

  // 显示排序选项
  showSortOptions() {
    const that = this;
    wx.showActionSheet({
      itemList: ['综合排序', '销量优先', '价格从低到高', '价格从高到低'],
      success: (res) => {
        const sortType = res.tapIndex;
        const sortTexts = ['综合排序', '销量优先', '价格从低到高', '价格从高到低'];

        that.setData({
          sortType,
          sortText: sortTexts[sortType]
        });

        that.sortProducts();
      }
    });
  },

  // 排序商品
  sortProducts() {
    let products = this.data.products.slice();
    const { sortType } = this.data;

    switch (sortType) {
      case 1: // 销量优先
        products.sort((a, b) => (b.salesCount || 0) - (a.salesCount || 0));
        break;
      case 2: // 价格从低到高
        products.sort((a, b) => {
          const priceA = a.vipPrice || a.price;
          const priceB = b.vipPrice || b.price;
          return priceA - priceB;
        });
        break;
      case 3: // 价格从高到低
        products.sort((a, b) => {
          const priceA = a.vipPrice || a.price;
          const priceB = b.vipPrice || b.price;
          return priceB - priceA;
        });
        break;
      default: // 综合排序（按ID倒序）
        products.sort((a, b) => b.id - a.id);
        break;
    }

    this.setData({ products });
  },

  // 跳转到商品详情
  goToProductDetail(e) {
    const productId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/product-detail/product-detail?id=${productId}`
    });
  }
});

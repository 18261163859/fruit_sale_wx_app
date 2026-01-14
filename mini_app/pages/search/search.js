// pages/search/search.js
const { searchProducts } = require('../../api/product.js');
const { addToCart } = require('../../api/cart.js');
const { getUserInfo } = require('../../utils/storage.js');
const { showToast, navigateTo, switchTab } = require('../../utils/common.js');
const { convertImageUrl } = require('../../utils/image.js');

Page({
  data: {
    keyword: '',
    searchHistory: [],
    hotKeywords: ['榴莲', '车厘子', '芒果', '蓝莓', '牛油果'],
    products: [],
    loading: false,
    showResult: false,
    current: 1,
    size: 10,
    total: 0,
    hasMore: true,
    userInfo: null
  },

  onLoad(options) {
    // 从首页传来的关键词
    if (options.keyword) {
      this.setData({
        keyword: options.keyword
      });
      this.onSearch();
    }

    // 加载搜索历史
    this.loadSearchHistory();

    // 加载用户信息
    const userInfo = getUserInfo();
    this.setData({ userInfo });
  },

  /**
   * 加载搜索历史
   */
  loadSearchHistory() {
    const history = wx.getStorageSync('searchHistory') || [];
    this.setData({
      searchHistory: history.slice(0, 10) // 最多显示10条
    });
  },

  /**
   * 保存搜索历史
   */
  saveSearchHistory(keyword) {
    if (!keyword.trim()) return;

    let history = wx.getStorageSync('searchHistory') || [];

    // 移除重复项
    history = history.filter(item => item !== keyword);

    // 添加到开头
    history.unshift(keyword);

    // 最多保存20条
    history = history.slice(0, 20);

    wx.setStorageSync('searchHistory', history);
    this.setData({
      searchHistory: history.slice(0, 10)
    });
  },

  /**
   * 清空搜索历史
   */
  clearHistory() {
    wx.showModal({
      title: '提示',
      content: '确定清空搜索历史吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('searchHistory');
          this.setData({
            searchHistory: []
          });
          showToast('已清空', 'success');
        }
      }
    });
  },

  /**
   * 输入搜索关键词
   */
  onInput(e) {
    this.setData({
      keyword: e.detail.value
    });
  },

  /**
   * 点击搜索按钮
   */
  onSearchBtn() {
    this.onSearch();
  },

  /**
   * 搜索
   */
  onSearch() {
    const keyword = this.data.keyword.trim();

    if (!keyword) {
      showToast('请输入搜索关键词');
      return;
    }

    // 保存搜索历史
    this.saveSearchHistory(keyword);

    // 重置分页
    this.setData({
      products: [],
      current: 1,
      hasMore: true,
      showResult: true
    });

    // 执行搜索
    this.loadProducts();
  },

  /**
   * 点击历史记录或热门搜索
   */
  onKeywordTap(e) {
    const keyword = e.currentTarget.dataset.keyword;
    this.setData({
      keyword
    });
    this.onSearch();
  },

  /**
   * 加载商品列表
   */
  async loadProducts() {
    if (this.data.loading || !this.data.hasMore) return;

    this.setData({ loading: true });

    try {
      const res = await searchProducts(this.data.keyword, {
        current: this.data.current,
        size: this.data.size,
        status: 1 // 只搜索上架商品
      });

      if (res.code === 200) {
        const newProducts = (res.data.records || []).map(product => {
          return Object.assign({}, product, {
            coverImage: convertImageUrl(product.coverImage || product.mainImage, 'product'),
            mainImage: convertImageUrl(product.mainImage, 'product'),
            productName: product.productName || product.name,
            salesCount: product.salesCount || 0
          });
        });

        this.setData({
          products: this.data.products.concat(newProducts),
          total: res.data.total,
          hasMore: this.data.products.length + newProducts.length < res.data.total
        });
      } else {
        showToast(res.message || '搜索失败');
      }
    } catch (err) {
      console.error('搜索失败:', err);
      showToast('搜索失败');
    } finally {
      this.setData({ loading: false });
    }
  },

  /**
   * 上拉加载更多
   */
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading && this.data.showResult) {
      this.setData({ current: this.data.current + 1 });
      this.loadProducts();
    }
  },

  /**
   * 查看商品详情
   */
  goToProductDetail(e) {
    const { id } = e.currentTarget.dataset;
    navigateTo(`/pages/product-detail/product-detail?id=${id}`);
  },

  /**
   * 加入购物车
   */
  async addToCart(e) {
    const { id, name } = e.currentTarget.dataset;

    // 检查登录状态
    if (!this.data.userInfo) {
      showToast('请先登录');
      setTimeout(() => {
        switchTab('/pages/profile/profile');
      }, 1500);
      return;
    }

    try {
      const res = await addToCart({
        productId: id,
        quantity: 1
      });

      if (res.code === 200) {
        showToast('已加入购物车', 'success');
      }
    } catch (err) {
      console.error('加入购物车失败:', err);
      showToast(err.message || '加入购物车失败');
    }
  },

  /**
   * 清空输入框
   */
  onClearInput() {
    this.setData({
      keyword: '',
      showResult: false,
      products: []
    });
  }
});

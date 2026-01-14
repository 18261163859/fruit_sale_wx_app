// pages/index/index.js
const { getBanners, getRecommendProducts } = require('../../api/product.js');
const { addToCart } = require('../../api/cart.js');
const { getUserInfo: getStorageUserInfo, setUserInfo: setStorageUserInfo } = require('../../utils/storage.js');
const { getUserInfo: getUserInfoAPI } = require('../../api/user.js');
const { applyTheme } = require('../../utils/theme.js');
const { showToast, navigateTo, switchTab } = require('../../utils/common.js');
const { convertImageUrl, getPlaceholderImage } = require('../../utils/image.js');

Page({
  data: {
    banners: [],
    products: [],
    searchKeyword: '',
    currentBannerIndex: 0,
    theme: 'normal',
    themeStyle: {},
    userInfo: null,
    isVip: false,
    loading: false,
    themeConfig: {},
    currentTheme: 'normal'
  },

  bannerTimer: null, // 轮播图定时器

  onLoad(options) {
    // 检查是否有邀请码参数
    if (options.inviteCode && !getStorageUserInfo()) {
      // 如果有邀请码且用户未登录，跳转到登录页
      wx.reLaunch({
        url: `/pages/login/login?inviteCode=${options.inviteCode}`
      });
      return;
    }

    this.initTheme();
    this.loadBanners();
    this.loadProducts();
  },

  onShow() {
    this.initTheme();
    // 重新启动轮播图自动播放
    if (this.data.banners.length > 0) {
      this.startBannerAutoPlay();
    }
  },

  onHide() {
    // 页面隐藏时清除定时器
    this.clearBannerTimer();
  },

  onUnload() {
    // 页面卸载时清除定时器
    this.clearBannerTimer();
  },

  // 初始化主题
  async initTheme() {
    // 先从本地存储获取用户信息用于快速渲染
    const cachedUserInfo = getStorageUserInfo();
    if (cachedUserInfo) {
      // 先使用缓存的用户信息快速渲染
      const cachedIsVip = this.checkIsVip(cachedUserInfo);
      this.setData({
        userInfo: cachedUserInfo,
        isVip: cachedIsVip
      });
      await applyTheme(this, cachedUserInfo);
    }

    // 然后从API获取最新的用户信息
    try {
      const res = await getUserInfoAPI();
      if (res.code === 200 && res.data) {
        const userInfo = res.data;
        const isVip = this.checkIsVip(userInfo);

        console.log('首页 - 用户信息:', userInfo);
        console.log('首页 - isVip:', isVip);

        // 更新本地存储
        setStorageUserInfo(userInfo);

        // 更新页面数据
        this.setData({
          userInfo: userInfo,
          isVip: isVip
        });

        // 重新应用主题
        await applyTheme(this, userInfo);
      }
    } catch (err) {
      console.error('获取用户信息失败:', err);
      // 如果获取失败，继续使用缓存的数据
    }
  },

  // 检查用户是否是VIP会员
  checkIsVip(userInfo) {
    if (!userInfo) {
      return false;
    }
    return userInfo.userType === 2;
  },

  // 加载轮播图
  async loadBanners() {
    try {
      const res = await getBanners();
      if (res.code === 200) {
        const banners = (res.data || []).map(banner => {
          return Object.assign({}, banner, {
            imageUrl: convertImageUrl(banner.imageUrl || banner.bannerImage, 'banner'),
            videoUrl: banner.videoUrl ? convertImageUrl(banner.videoUrl, 'banner') : '',
            bannerType: banner.bannerType || 1
          });
        });
        this.setData({
          banners
        }, () => {
          // 轮播图加载完成后，启动自动播放
          if (banners.length > 0) {
            this.startBannerAutoPlay();
          }
        });
      }
    } catch (err) {
      console.error('加载轮播图失败:', err);
    }
  },

  // 加载推荐商品
  async loadProducts() {
    this.setData({ loading: true });
    try {
      const res = await getRecommendProducts();
      if (res.code === 200) {
        // 推荐商品接口返回的是数组，不是分页对象
        const products = (res.data || []).map(product => {
          return Object.assign({}, product, {
            coverImage: convertImageUrl(product.coverImage || product.mainImage, 'product'),
            mainImage: convertImageUrl(product.mainImage, 'product'),
            name: product.name || product.productName,
            salesCount: product.salesCount || 0
          });
        });
        this.setData({
          products
        });
      }
    } catch (err) {
      console.error('加载商品失败:', err);
    } finally {
      this.setData({ loading: false });
    }
  },

  // 轮播图切换
  onBannerChange(e) {
    const newIndex = e.detail.current;
    this.setData({
      currentBannerIndex: newIndex
    });
    // 轮播图切换后，重新启动自动播放逻辑
    this.startBannerAutoPlay();
  },

  // 视频播放完成
  onVideoEnded(e) {
    // 视频播放完成，切换到下一个轮播图
    this.goToNextBanner();
  },

  // 切换到下一个轮播图
  goToNextBanner() {
    const { banners, currentBannerIndex } = this.data;
    if (banners.length === 0) return;

    const nextIndex = (currentBannerIndex + 1) % banners.length;
    this.setData({
      currentBannerIndex: nextIndex
    });
  },

  // 启动轮播图自动播放
  startBannerAutoPlay() {
    // 清除之前的定时器
    this.clearBannerTimer();

    const { banners, currentBannerIndex } = this.data;
    if (banners.length === 0) return;

    const currentBanner = banners[currentBannerIndex];

    // 如果是图片类型，设置3秒后自动切换
    if (currentBanner.bannerType === 1 || !currentBanner.bannerType) {
      this.bannerTimer = setTimeout(() => {
        this.goToNextBanner();
      }, 3000);
    }
    // 如果是视频类型，不设置定时器，等待视频播放完成触发 onVideoEnded
  },

  // 清除轮播图定时器
  clearBannerTimer() {
    if (this.bannerTimer) {
      clearTimeout(this.bannerTimer);
      this.bannerTimer = null;
    }
  },

  // 点击轮播图
  onBannerTap(e) {
    const { index } = e.currentTarget.dataset;
    const banner = this.data.banners[index];

    if (banner.linkType === 'product') {
      navigateTo(`/pages/product-detail/product-detail?id=${banner.linkId}`);
    } else if (banner.linkType === 'url') {
      // 处理外部链接
    }
  },

  // 搜索输入
  onSearchInput(e) {
    this.setData({
      searchKeyword: e.detail.value
    });
  },

  // 搜索
  onSearch() {
    if (!this.data.searchKeyword.trim()) {
      showToast('请输入搜索关键词');
      return;
    }
    navigateTo(`/pages/search/search?keyword=${this.data.searchKeyword}`);
  },

  // 跳转到搜索页
  goToSearch() {
    navigateTo('/pages/search/search');
  },

  // 查看商品详情
  goToProductDetail(e) {
    const { id } = e.currentTarget.dataset;
    navigateTo(`/pages/product-detail/product-detail?id=${id}`);
  },

  // 快速加入购物车
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
        showToast(`已加入购物车`, 'success');
        // 更新购物车角标
        this.updateCartBadge();
      }
    } catch (err) {
      console.error('加入购物车失败:', err);
      showToast(err.message || '加入购物车失败');
    }
  },

  // 更新购物车角标
  updateCartBadge() {
    // 获取购物车数量并更新tabBar角标
    const app = getApp();
    if (app.globalData.cartCount) {
      wx.setTabBarBadge({
        index: 2,
        text: String(app.globalData.cartCount)
      });
    }
  },

  // 前往会员中心
  goToVipCenter() {
    if (!this.data.userInfo) {
      showToast('请先登录');
      setTimeout(() => {
        switchTab('/pages/profile/profile');
      }, 1500);
      return;
    }
    navigateTo('/pages/open-vip/open-vip');
  },

  // 前往分类页
  goToCategory() {
    switchTab('/pages/category/category');
  },

  // 下拉刷新
  async onPullDownRefresh() {
    await Promise.all([
      this.loadBanners(),
      this.loadProducts()
    ]);
    wx.stopPullDownRefresh();
  }
});
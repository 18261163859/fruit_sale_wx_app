// app.js
const { getUserInfo: getStoredUserInfo, isLogin } = require('./utils/storage.js');
const { getUserInfo } = require('./api/user.js');
const { getCartList } = require('./api/cart.js');
const { fetchThemeConfig, getThemeByUserType, getThemeConfig, applyThemeToTabBar } = require('./utils/theme.js');

App({
  globalData: {
    userInfo: null,
    cartCount: 0,
    currentTheme: 'normal',  // 当前主题类型
    themeConfig: null,       // 当前主题配置
    baseUrl: 'https://www.zhiyeji.com/api'  // 后端API地址
  },

  onLaunch() {
    // 初始化主题
    this.initTheme();

    // 检查登录状态
    this.checkLogin();

    // 获取购物车数量
    this.updateCartCount();
  },

  // 初始化主题
  async initTheme(userType = 1) {
    try {
      // 获取后端主题配置
      const backendThemeConfig = await fetchThemeConfig();

      const themeType = getThemeByUserType(userType, backendThemeConfig);
      this.globalData.currentTheme = themeType;
      this.globalData.themeConfig = getThemeConfig(themeType);

      // 应用主题到TabBar
      applyThemeToTabBar(themeType);
    } catch (error) {
      console.error('初始化主题失败:', error);
      // 使用默认主题
      const themeType = 'normal';
      this.globalData.currentTheme = themeType;
      this.globalData.themeConfig = getThemeConfig(themeType);
      applyThemeToTabBar(themeType);
    }
  },

  // 切换主题（当用户类型变化时调用）
  changeTheme(userType) {
    this.initTheme(userType);
  },

  // 检查登录状态
  async checkLogin() {
    if (isLogin()) {
      try {
        // 获取用户信息
        const res = await getUserInfo();
        if (res.code === 200 && res.data) {
          this.globalData.userInfo = res.data;

          // 保存用户信息到本地存储
          const { setUserInfo: saveUserInfo } = require('./utils/storage.js');
          saveUserInfo(res.data);

          // 根据用户类型更新主题
          if (res.data.userType) {
            this.changeTheme(res.data.userType);
          }
        }
      } catch (err) {
        console.error('获取用户信息失败:', err);
        // Token可能已过期,跳转到登录页
        wx.reLaunch({
          url: '/pages/login/login'
        });
      }
    } else {
      // 未登录,跳转到登录页
      wx.reLaunch({
        url: '/pages/login/login'
      });
    }
  },

  // 更新购物车数量
  async updateCartCount() {
    if (!isLogin()) return;

    try {
      const res = await getCartList();
      if (res.code === 200 && res.data) {
        const cartCount = res.data.reduce((sum, item) => sum + item.quantity, 0);
        this.globalData.cartCount = cartCount;

        // 更新tabBar角标
        if (cartCount > 0) {
          wx.setTabBarBadge({
            index: 2,
            text: String(cartCount)
          });
        } else {
          wx.removeTabBarBadge({
            index: 2
          });
        }
      }
    } catch (err) {
      console.error('获取购物车数量失败:', err);
    }
  }
});
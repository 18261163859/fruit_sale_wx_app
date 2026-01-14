/**
 * 主题管理工具
 * 根据用户类型和后端配置自动切换主题
 */

const { getThemeConfig: getThemeConfigAPI } = require('../api/config.js');

// 主题类型
const THEME_TYPES = {
  NORMAL: 'normal',      // 普通会员 - 蓝白配色
  VIP: 'vip',           // 星享会员 - 黑金配色
  NEWYEAR: 'newyear'    // 新年特供 - 红金配色
};

// 三种主题配置
const THEMES = {
  // 普通会员主题 - 蓝白配色
  normal: {
    name: '普通会员',
    primary: '#2563eb',      // 主色调
    secondary: '#dbeafe',    // 辅助色
    accent: '#1d4ed8',       // 强调色
    background: '#f5f5f5',   // 背景色
    cardBg: '#ffffff',       // 卡片背景
    textPrimary: '#333333',  // 主文本色
    textSecondary: '#666666', // 次要文本色
    tabBarSelected: '#1989fa' // 底部导航选中色
  },

  // 星享会员主题 - 黑金配色
  vip: {
    name: '星享会员',
    primary: '#000000',      // 主色调-黑色
    secondary: '#fbbf24',    // 辅助色-金色
    accent: '#1f2937',       // 强调色-深灰
    gold: '#d97706',         // 金色强调
    background: '#f5f5f5',   // 背景色
    cardBg: '#ffffff',       // 卡片背景
    textPrimary: '#333333',  // 主文本色
    textSecondary: '#666666', // 次要文本色
    tabBarSelected: '#fbbf24' // 底部导航选中色-金色
  },

  // 新年特供主题 - 喜庆红金配色
  newyear: {
    name: '新年特供',
    primary: '#E63946',      // 主色调-喜庆红
    secondary: '#FFD700',    // 辅助色-亮金色
    accent: '#C1121F',       // 强调色-中国红
    gold: '#D4AF37',         // 暗金色强调
    lightGold: '#FFF8DC',    // 浅金色
    redGradient: 'linear-gradient(135deg, #E63946 0%, #C1121F 100%)',  // 红色渐变
    goldGradient: 'linear-gradient(135deg, #FFD700 0%, #FFA500 100%)', // 金色渐变
    background: '#FFF8F0',   // 背景色-温暖米黄
    cardBg: '#FFFEF7',       // 卡片背景-纸白色
    textPrimary: '#2B2D42',  // 主文本色-深蓝灰
    textSecondary: '#8D99AE', // 次要文本色-中灰蓝
    tabBarSelected: '#E63946' // 底部导航选中色-喜庆红
  }
};

// 缓存主题配置，避免频繁请求
let cachedThemeConfig = null;
let cacheTime = 0;
const CACHE_DURATION = 5 * 60 * 1000; // 5分钟缓存

/**
 * 从后端获取主题配置
 */
async function fetchThemeConfig() {
  try {
    // 检查缓存
    if (cachedThemeConfig && (Date.now() - cacheTime < CACHE_DURATION)) {
      return cachedThemeConfig;
    }

    const res = await getThemeConfigAPI();
    if (res.code === 200 && res.data) {
      cachedThemeConfig = res.data;
      cacheTime = Date.now();
      return res.data;
    }
  } catch (err) {
    console.error('获取主题配置失败:', err);
  }

  // 默认返回空配置
  return {
    normalUserNewYearEnabled: false,
    vipUserNewYearEnabled: false
  };
}

/**
 * 根据用户类型和后端配置获取主题
 * @param {Number} userType 用户类型：1-普通会员，2-星享会员，3-一级代理，4-二级代理
 * @param {Object} themeConfig 后端主题配置
 */
function getThemeByUserType(userType, themeConfig = {}) {
  // 转换为数字类型，防止字符串比较问题
  const type = Number(userType) || 1;

  console.log('getThemeByUserType - userType:', type, 'themeConfig:', themeConfig);

  // 检查是否启用新年主题
  const isVipUser = type === 2 || type === 3 || type === 4;

  if (isVipUser && themeConfig.vipUserNewYearEnabled) {
    console.log('返回新年主题 (VIP用户)');
    return THEME_TYPES.NEWYEAR;
  }

  if (!isVipUser && themeConfig.normalUserNewYearEnabled) {
    console.log('返回新年主题 (普通用户)');
    return THEME_TYPES.NEWYEAR;
  }

  // 星享会员、一级代理、二级代理使用VIP主题
  if (isVipUser) {
    console.log('返回VIP主题');
    return THEME_TYPES.VIP;
  }

  // 普通会员使用普通主题
  console.log('返回普通主题');
  return THEME_TYPES.NORMAL;
}

/**
 * 获取主题配置
 * @param {String} themeType 主题类型
 */
function getThemeConfig(themeType) {
  return THEMES[themeType] || THEMES.normal;
}

/**
 * 应用主题到TabBar
 * @param {String} themeType 主题类型
 */
function applyThemeToTabBar(themeType) {
  const theme = getThemeConfig(themeType);

  wx.setTabBarStyle({
    color: '#999999',
    selectedColor: theme.tabBarSelected,
    backgroundColor: '#ffffff',
    borderStyle: 'black'
  });
}

/**
 * 应用主题到导航栏
 * @param {String} themeType 主题类型
 * @param {String} title 导航栏标题
 */
function applyThemeToNavigationBar(themeType, title) {
  const theme = getThemeConfig(themeType);

  wx.setNavigationBarColor({
    frontColor: themeType === 'vip' ? '#ffffff' : '#000000',
    backgroundColor: theme.primary,
    animation: {
      duration: 400,
      timingFunc: 'easeIn'
    }
  });

  if (title) {
    wx.setNavigationBarTitle({
      title: title
    });
  }
}

/**
 * 页面应用主题（辅助函数）
 * @param {Object} page 页面实例（this）
 * @param {Object} userInfo 用户信息（可选）
 */
async function applyTheme(page, userInfo) {
  const app = getApp();

  console.log('applyTheme - userInfo:', userInfo);

  // 获取后端主题配置
  const backendThemeConfig = await fetchThemeConfig();

  // 如果传入了userInfo，根据userType和后端配置获取主题
  let themeType = app.globalData.currentTheme || THEME_TYPES.NORMAL;
  if (userInfo && userInfo.userType) {
    themeType = getThemeByUserType(userInfo.userType, backendThemeConfig);
    app.globalData.currentTheme = themeType;
  } else {
    console.log('applyTheme - 使用默认主题，userInfo为空或无userType');
  }

  const themeConfig = getThemeConfig(themeType);

  console.log('applyTheme - 最终主题类型:', themeType);

  // 设置页面数据
  page.setData({
    themeConfig: themeConfig,
    currentTheme: themeType
  });

  return { themeConfig, themeType };
}

module.exports = {
  THEME_TYPES,
  THEMES,
  fetchThemeConfig,
  getThemeByUserType,
  getThemeConfig,
  applyThemeToTabBar,
  applyThemeToNavigationBar,
  applyTheme
};

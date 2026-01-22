const { get } = require('../utils/request.js');

/**
 * 系统配置相关API
 */

// 获取主题配置
function getThemeConfig() {
  return get('/system-config/theme');
}

// 获取积分配置
function getIntegralConfig() {
  return get('/system-config/integral');
}

// 获取运费配置
function getShippingConfig() {
  return get('/system-config/shipping');
}

// 获取客服配置
function getCustomerServiceConfig() {
  return get('/system-config/customer-service');
}

// 获取VIP配置
function getVipConfig() {
  return get('/system-config/vip');
}

module.exports = {
  getThemeConfig,
  getIntegralConfig,
  getShippingConfig,
  getCustomerServiceConfig,
  getVipConfig
};

const { get, post } = require('../utils/request.js');

/**
 * VIP相关API
 */

// 创建会员开通订单
function createVipOrder() {
  return post('/vip/order/create');
}

// 支付会员订单
function payVipOrder(data) {
  return post('/vip/order/pay', data);
}

// 查询会员订单详情
function getVipOrder(orderNo) {
  return get(`/vip/order/${orderNo}`);
}

// 检查会员状态
function checkVipStatus() {
  return get('/vip/status');
}

module.exports = {
  createVipOrder,
  payVipOrder,
  getVipOrder,
  checkVipStatus
};
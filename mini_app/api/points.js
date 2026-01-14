const { get, post } = require('../utils/request.js');

/**
 * 积分相关API
 */

// 获取积分余额
function getPointsBalance() {
  return get('/user/info');  // 从用户信息中获取积分余额
}

// 获取积分记录
function getPointsRecords(params) {
  return get('/user/points/records', params);
}

// 兑换积分
function redeemPoints(code) {
  return post('/user/integral/recharge', { cardNo: code });
}

// 获取积分规则
function getPointsRules() {
  return get('/user/points/rules');
}

module.exports = {
  getPointsBalance,
  getPointsRecords,
  redeemPoints,
  getPointsRules
};
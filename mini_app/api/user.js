const { get, post, put, del } = require('../utils/request.js');

/**
 * 用户相关API
 */

// 微信登录
function wxLogin(data) {
  return post('/auth/user/login', data);
}

// 获取手机号
function getPhoneNumber(data) {
  return post('/auth/phone', data);
}

// 获取用户信息
function getUserInfo() {
  return get('/user/info');
}

// 更新用户信息
function updateUserInfo(data) {
  return put('/user/info', data);
}

// 获取用户积分
function getUserPoints() {
  return get('/user/points');
}

// 兑换积分
function rechargeIntegral(cardNo) {
  return post('/user/integral/recharge', { cardNo }, {
    header: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  });
}

// 获取积分记录
function getIntegralRecords() {
  return get('/user/integral/records');
}

// 获取订单统计
function getOrderStatistics() {
  return get('/user/order/statistics');
}

// 开通星享会员
function openStarMember() {
  return post('/user/star-member/open');
}

// 绑定邀请码
function bindInviteCode(inviteCode) {
  return post('/user/bind-invite-code', { inviteCode }, {
    header: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  });
}

module.exports = {
  wxLogin,
  getPhoneNumber,
  getUserInfo,
  updateUserInfo,
  getUserPoints,
  rechargeIntegral,
  getIntegralRecords,
  getOrderStatistics,
  openStarMember,
  bindInviteCode
};
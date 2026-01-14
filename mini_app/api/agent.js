const { get, post } = require('../utils/request.js');

/**
 * 代理相关API
 */

// 获取我的代理信息
function getMyAgentInfo() {
  return get('/user/info');  // 从用户信息中获取代理信息
}

// 获取下级列表
function getSubordinates(params) {
  return get('/agent/my-team', params);
}

// 获取返现记录
function getCommissionList(params) {
  return get('/agent/commission/list', params);
}

// 获取返现记录（分页）
function getCommissionRecords(params) {
  return get('/agent/commission/records', params);
}

// 获取返现统计
function getCommissionStats() {
  return get('/agent/statistics');
}

// 获取返现统计详情
function getCommissionStatistics() {
  return get('/agent/commission/statistics');
}

// 获取下级返现记录
function getSubordinateCommissions(params) {
  return get('/agent/commission/subordinate', params);
}

// 申请成为代理
function applyAgent(data) {
  return post('/agent/apply', data);
}

// 生成邀请二维码
function generateInviteQrcode() {
  return get('/share/qrcode');  // 使用ShareController的二维码生成
}

// 一级代理邀请二级代理
function inviteSubAgent(data) {
  return post('/agent/invite-sub-agent', data);
}

// 获取我发起的邀请申请列表
function getMyInviteApplications() {
  return get('/agent/invite-applications');
}

// 提交返现申请
function submitCommissionApplication(data) {
  return post('/agent/commission-application', data);
}

// 获取我的返现申请列表
function getMyCommissionApplications() {
  return get('/agent/commission-applications');
}

module.exports = {
  getMyAgentInfo,
  getSubordinates,
  getCommissionList,
  getCommissionRecords,
  getCommissionStats,
  getCommissionStatistics,
  getSubordinateCommissions,
  applyAgent,
  generateInviteQrcode,
  inviteSubAgent,
  getMyInviteApplications,
  submitCommissionApplication,
  getMyCommissionApplications
};
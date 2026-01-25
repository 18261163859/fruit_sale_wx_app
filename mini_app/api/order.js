const { get, post, put } = require('../utils/request.js');

/**
 * 订单相关API
 */

// 创建订单
function createOrder(data) {
  return post('/order/create', data);
}

// 获取订单列表
function getOrders(params) {
  return get('/order/my/list', params);
}

// 获取订单详情（根据ID）
function getOrderDetail(id) {
  return get(`/order/${id}`);
}

// 获取订单详情（根据订单号）
function getOrderDetailByOrderNo(orderNo) {
  return get(`/order/no/${orderNo}`);
}

// 取消订单
function cancelOrder(id) {
  return post(`/order/cancel/${id}`);
}

// 确认收货
function confirmReceipt(id) {
  return post(`/order/confirm/${id}`);
}

// 支付订单
function payOrder(id) {
  return post(`/order/pay/${id}`);
}

// 获取订单统计
function getOrderStatistics() {
  return get('/order/my/statistics');
}

// 获取待发货订单列表（发货端）
function getPendingShipOrders(params) {
  return get('/order/pending-ship/list', params);
}

// 获取已发货订单列表（发货端）
function getShippedOrders(params) {
  return get('/order/shipped/list', params);
}

// 发货（发货端）
function shipOrder(data) {
  return post('/order/ship', data);
}

module.exports = {
  createOrder,
  getOrders,
  getOrderDetail,
  getOrderDetailByOrderNo,
  cancelOrder,
  confirmReceipt,
  payOrder,
  getOrderStatistics,
  getPendingShipOrders,
  getShippedOrders,
  shipOrder
};
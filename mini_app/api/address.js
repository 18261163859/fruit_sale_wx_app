const { get, post, put, del } = require('../utils/request.js');

/**
 * 地址相关API
 */

// 获取地址列表
function getAddressList() {
  return get('/address/list');
}

// 获取地址详情
function getAddressDetail(id) {
  return get(`/address/${id}`);
}

// 添加地址
function addAddress(data) {
  return post('/address/add', data);
}

// 更新地址
function updateAddress(data) {
  return put('/address/update', data);
}

// 删除地址
function deleteAddress(id) {
  return del(`/address/delete/${id}`);
}

// 设置默认地址
function setDefaultAddress(id) {
  return put(`/address/default/${id}`);
}

module.exports = {
  getAddressList,
  getAddressDetail,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
};
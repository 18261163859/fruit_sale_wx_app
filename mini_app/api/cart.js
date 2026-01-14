const { get, post, put, del } = require('../utils/request.js');

/**
 * 购物车相关API
 */

// 获取购物车列表
function getCartList() {
  return get('/cart/list');
}

// 添加到购物车
function addToCart(data) {
  return post('/cart/add', data);
}

// 更新购物车商品数量
function updateCartItem(id, quantity) {
  return put(`/cart/update/${id}`, { quantity });
}

// 删除购物车商品
function removeCartItem(id) {
  return del(`/cart/remove/${id}`);
}

// 清空购物车
function clearCart() {
  return del('/cart/clear');
}

// 批量删除购物车商品
function batchRemoveCartItems(ids) {
  return post('/cart/batch-delete', { ids });
}

module.exports = {
  getCartList,
  addToCart,
  updateCartItem,
  removeCartItem,
  clearCart,
  batchRemoveCartItems
};
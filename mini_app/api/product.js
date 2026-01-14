const { get, post } = require('../utils/request.js');

/**
 * 商品相关API
 */

// 获取商品分类
function getCategories() {
  return get('/product/category/list');
}

// 获取商品列表
function getProducts(params) {
  return post('/product/page', params);
}

// 获取推荐商品
function getRecommendProducts(params) {
  return get('/product/recommend', params);
}

// 获取商品详情
function getProductDetail(id) {
  return get(`/product/${id}`);
}

// 搜索商品
function searchProducts(keyword, params = {}) {
  return post('/product/page', { productName: keyword, ...params });
}

// 获取轮播图
function getBanners() {
  return get('/banner/list');
}

// 获取商品规格列表
function getProductSpecs(productId) {
  return get(`/product/${productId}/specs`);
}

module.exports = {
  getCategories,
  getProducts,
  getRecommendProducts,
  getProductDetail,
  searchProducts,
  getBanners,
  getProductSpecs
};
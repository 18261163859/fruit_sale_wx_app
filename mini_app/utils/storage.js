/**
 * 存储工具类
 */

/**
 * 设置本地存储
 * @param {String} key 键
 * @param {*} value 值
 */
function setStorage(key, value) {
  try {
    wx.setStorageSync(key, value);
    return true;
  } catch (e) {
    console.error('setStorage error:', e);
    return false;
  }
}

/**
 * 获取本地存储
 * @param {String} key 键
 */
function getStorage(key) {
  try {
    return wx.getStorageSync(key);
  } catch (e) {
    console.error('getStorage error:', e);
    return null;
  }
}

/**
 * 移除本地存储
 * @param {String} key 键
 */
function removeStorage(key) {
  try {
    wx.removeStorageSync(key);
    return true;
  } catch (e) {
    console.error('removeStorage error:', e);
    return false;
  }
}

/**
 * 清空本地存储
 */
function clearStorage() {
  try {
    wx.clearStorageSync();
    return true;
  } catch (e) {
    console.error('clearStorage error:', e);
    return false;
  }
}

/**
 * 设置用户信息
 * @param {Object} userInfo 用户信息
 */
function setUserInfo(userInfo) {
  return setStorage('userInfo', userInfo);
}

/**
 * 获取用户信息
 */
function getUserInfo() {
  return getStorage('userInfo');
}

/**
 * 清除用户信息
 */
function clearUserInfo() {
  removeStorage('userInfo');
  removeStorage('token');
}

/**
 * 设置Token
 * @param {String} token Token
 */
function setToken(token) {
  return setStorage('token', token);
}

/**
 * 获取Token
 */
function getToken() {
  return getStorage('token');
}

/**
 * 判断是否已登录
 */
function isLogin() {
  return !!getToken();
}

module.exports = {
  setStorage,
  getStorage,
  removeStorage,
  clearStorage,
  setUserInfo,
  getUserInfo,
  clearUserInfo,
  setToken,
  getToken,
  isLogin
};
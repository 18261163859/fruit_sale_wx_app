/**
 * 通用工具函数
 */

/**
 * 格式化金额
 * @param {Number} amount 金额
 * @param {Number} decimals 小数位数
 */
function formatMoney(amount, decimals = 2) {
  if (amount === null || amount === undefined) return '0.00';
  return Number(amount).toFixed(decimals);
}

/**
 * 防抖
 * @param {Function} fn 函数
 * @param {Number} delay 延迟时间
 */
function debounce(fn, delay = 500) {
  let timer = null;
  return function (...args) {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
}

/**
 * 节流
 * @param {Function} fn 函数
 * @param {Number} delay 延迟时间
 */
function throttle(fn, delay = 500) {
  let timer = null;
  return function (...args) {
    if (timer) return;
    timer = setTimeout(() => {
      fn.apply(this, args);
      timer = null;
    }, delay);
  };
}

/**
 * 深拷贝
 * @param {*} obj 对象
 */
function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj;

  if (obj instanceof Date) {
    return new Date(obj.getTime());
  }

  if (obj instanceof Array) {
    return obj.map(item => deepClone(item));
  }

  if (obj instanceof Object) {
    const clonedObj = {};
    for (let key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key]);
      }
    }
    return clonedObj;
  }
}

/**
 * 获取图片完整URL
 * @param {String} url 图片路径
 */
function getImageUrl(url) {
  if (!url) return '';
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  return 'https://www.zhiyeji.com/files' + url;
}

/**
 * 隐藏手机号中间4位
 * @param {String} phone 手机号
 */
function hidePhone(phone) {
  if (!phone) return '';
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
}

/**
 * 验证手机号
 * @param {String} phone 手机号
 */
function validatePhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone);
}

/**
 * 验证邮箱
 * @param {String} email 邮箱
 */
function validateEmail(email) {
  return /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(email);
}

/**
 * 显示Toast
 * @param {String} title 提示文字
 * @param {String} icon 图标
 */
function showToast(title, icon = 'none') {
  wx.showToast({
    title,
    icon,
    duration: 2000
  });
}

/**
 * 显示Loading
 * @param {String} title 提示文字
 */
function showLoading(title = '加载中...') {
  wx.showLoading({
    title,
    mask: true
  });
}

/**
 * 隐藏Loading
 */
function hideLoading() {
  wx.hideLoading();
}

/**
 * 显示确认对话框
 * @param {String} content 内容
 * @param {String} title 标题
 */
function showConfirm(content, title = '提示') {
  return new Promise((resolve, reject) => {
    wx.showModal({
      title,
      content,
      success: (res) => {
        if (res.confirm) {
          resolve(true);
        } else {
          reject(false);
        }
      }
    });
  });
}

/**
 * 导航到页面
 * @param {String} url 页面路径
 */
function navigateTo(url) {
  wx.navigateTo({ url });
}

/**
 * 重定向到页面
 * @param {String} url 页面路径
 */
function redirectTo(url) {
  wx.redirectTo({ url });
}

/**
 * 切换Tab页面
 * @param {String} url 页面路径
 */
function switchTab(url) {
  wx.switchTab({ url });
}

/**
 * 返回上一页
 * @param {Number} delta 返回的页面数
 */
function navigateBack(delta = 1) {
  wx.navigateBack({ delta });
}

/**
 * 重新加载
 * @param {String} url 页面路径
 */
function reLaunch(url) {
  wx.reLaunch({ url });
}

/**
 * 选择图片
 * @param {Number} count 最多可以选择的图片张数
 */
function chooseImage(count = 1) {
  return new Promise((resolve, reject) => {
    wx.chooseImage({
      count,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        resolve(res.tempFilePaths);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

/**
 * 预览图片
 * @param {Array} urls 图片列表
 * @param {Number} current 当前显示图片的索引
 */
function previewImage(urls, current = 0) {
  wx.previewImage({
    urls,
    current: urls[current]
  });
}

/**
 * 复制到剪贴板
 * @param {String} data 数据
 */
function setClipboardData(data) {
  return new Promise((resolve, reject) => {
    wx.setClipboardData({
      data,
      success: () => {
        wx.showToast({
          title: '复制成功',
          icon: 'success'
        });
        resolve();
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

/**
 * 拨打电话
 * @param {String} phoneNumber 电话号码
 */
function makePhoneCall(phoneNumber) {
  wx.makePhoneCall({
    phoneNumber
  });
}

module.exports = {
  formatMoney,
  debounce,
  throttle,
  deepClone,
  getImageUrl,
  hidePhone,
  validatePhone,
  validateEmail,
  showToast,
  showLoading,
  hideLoading,
  showConfirm,
  navigateTo,
  redirectTo,
  switchTab,
  navigateBack,
  reLaunch,
  chooseImage,
  previewImage,
  setClipboardData,
  makePhoneCall
};
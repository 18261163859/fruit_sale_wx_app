// HTTP请求封装
// 从 app.js 的 globalData 中获取 baseUrl
function getBaseUrl() {
  const app = getApp();
  return app.globalData.baseUrl || 'http://127.0.0.1:8000/api';
}

// 请求拦截器
function request(options) {
  return new Promise((resolve, reject) => {
    // 显示加载提示
    if (!options.hideLoading) {
      wx.showLoading({
        title: '加载中...',
        mask: true
      });
    }

    // 获取token
    const token = wx.getStorageSync('token');

    wx.request({
      url: getBaseUrl() + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        wx.hideLoading();

        if (res.statusCode === 200) {
          if (res.data.code === 0 || res.data.code === 200 || res.data.success) {
            resolve(res.data);
          } else if (res.data.code === 401) {
            wx.showToast({
              title: '登录已过期',
              icon: 'none'
            });
            wx.removeStorageSync('token');
            wx.removeStorageSync('userInfo');

            const pages = getCurrentPages();
            if (pages.length > 0) {
              const currentPage = pages[pages.length - 1];
              const route = currentPage.route;
              const options = currentPage.options;
              const queryString = Object.keys(options).map(key => `${key}=${options[key]}`).join('&');
              const fullPath = queryString ? `/${route}?${queryString}` : `/${route}`;
              wx.setStorageSync('redirectUrl', fullPath);
            }

            setTimeout(() => {
              wx.reLaunch({
                url: '/pages/login/login'
              });
            }, 1500);
            reject(res.data);
          } else {
            wx.showToast({
              title: res.data.message || res.data.msg || '请求失败',
              icon: 'none',
              duration: 2000
            });
            reject(res.data);
          }
        } else if (res.statusCode === 401) {
          // token过期，跳转登录
          wx.showToast({
            title: '登录已过期',
            icon: 'none'
          });
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');

          const pages = getCurrentPages();
          if (pages.length > 0) {
            const currentPage = pages[pages.length - 1];
            const route = currentPage.route;
            const options = currentPage.options;
            const queryString = Object.keys(options).map(key => `${key}=${options[key]}`).join('&');
            const fullPath = queryString ? `/${route}?${queryString}` : `/${route}`;
            wx.setStorageSync('redirectUrl', fullPath);
          }

          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/login/login'
            });
          }, 1500);
          reject(res.data);
        } else {
          wx.showToast({
            title: '请求失败',
            icon: 'none'
          });
          reject(res.data);
        }
      },
      fail: (err) => {
        wx.hideLoading();
        wx.showToast({
          title: '网络异常',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
}

// GET请求
function get(url, data = {}, options = {}) {
  return request({
    url,
    method: 'GET',
    data,
    ...options
  });
}

// POST请求
function post(url, data = {}, options = {}) {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  });
}

// PUT请求
function put(url, data = {}, options = {}) {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  });
}

// DELETE请求
function del(url, data = {}, options = {}) {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  });
}

// 上传文件
function uploadFile(filePath, options = {}) {
  return new Promise((resolve, reject) => {
    wx.showLoading({
      title: '上传中...',
      mask: true
    });

    const token = wx.getStorageSync('token');

    wx.uploadFile({
      url: getBaseUrl() + (options.url || '/api/upload'),
      filePath,
      name: options.name || 'file',
      formData: options.formData || {},
      header: {
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        wx.hideLoading();
        const data = JSON.parse(res.data);
        // 支持 code === 0 或 code === 200 或 success === true
        if (data.code === 0 || data.code === 200 || data.success) {
          resolve(data);
        } else {
          wx.showToast({
            title: data.message || '上传失败',
            icon: 'none'
          });
          reject(data);
        }
      },
      fail: (err) => {
        wx.hideLoading();
        wx.showToast({
          title: '上传失败',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
}

module.exports = {
  request,
  get,
  post,
  put,
  del,
  uploadFile
};
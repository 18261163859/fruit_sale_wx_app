/**
 * 图片上传工具
 */
const { uploadFile } = require('./request.js');

/**
 * 上传图片
 * @param {String} filePath 图片临时路径
 * @param {String} type 图片类型 (product, avatar, shipping等)
 * @returns {Promise<String>} 返回图片URL
 */
function uploadImage(filePath, type = 'common') {
  return new Promise((resolve, reject) => {
    // 根据类型选择不同的上传接口
    let uploadUrl = '/upload/system/image'; // 默认使用系统图片接口

    if (type === 'product') {
      uploadUrl = '/upload/product/image';
    } else if (type === 'avatar') {
      uploadUrl = '/upload/user/avatar';
    } else if (type === 'shipping') {
      uploadUrl = '/upload/shipping/image';
    }

    uploadFile(filePath, {
      url: uploadUrl,
      name: 'file'
    })
      .then(res => {
        if (res.data && res.data.url) {
          resolve(res.data.url);
        } else if (res.url) {
          resolve(res.url);
        } else {
          reject(new Error('上传失败：未获取到图片URL'));
        }
      })
      .catch(err => {
        reject(err);
      });
  });
}

/**
 * 选择并上传图片
 * @param {Number} count 最多可以选择的图片张数
 * @param {String} type 图片类型
 * @returns {Promise<String|Array>} 返回图片URL或URL数组
 */
function chooseAndUploadImage(count = 1, type = 'common') {
  return new Promise((resolve, reject) => {
    wx.chooseImage({
      count,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        try {
          wx.showLoading({
            title: '上传中...',
            mask: true
          });

          const tempFilePaths = res.tempFilePaths;

          if (count === 1) {
            // 单张图片
            const url = await uploadImage(tempFilePaths[0], type);
            wx.hideLoading();
            wx.showToast({
              title: '上传成功',
              icon: 'success'
            });
            resolve(url);
          } else {
            // 多张图片
            const uploadPromises = tempFilePaths.map(path => uploadImage(path, type));
            const urls = await Promise.all(uploadPromises);
            wx.hideLoading();
            wx.showToast({
              title: '上传成功',
              icon: 'success'
            });
            resolve(urls);
          }
        } catch (err) {
          wx.hideLoading();
          wx.showToast({
            title: '上传失败',
            icon: 'none'
          });
          reject(err);
        }
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

module.exports = {
  uploadImage,
  chooseAndUploadImage
};

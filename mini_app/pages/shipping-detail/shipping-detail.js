// pages/shipping-detail/shipping-detail.js
const { getOrderDetail, shipOrder } = require('../../api/order.js');
const { uploadImage } = require('../../utils/upload.js');

Page({
  data: {
    orderId: null,
    order: null,
    expressCompany: '',
    expressNo: '',
    packageBeforeImage: '',
    packageAfterImage: '',
    shipRemark: '',
    loading: false
  },

  onLoad(options) {
    if (options.orderId) {
      this.setData({ orderId: options.orderId });
      this.loadOrderDetail();
    }
  },

  async loadOrderDetail() {
    try {
      const res = await getOrderDetail(this.data.orderId);
      if (res.code === 200 && res.data) {
        this.setData({ order: res.data });
      }
    } catch (err) {
      console.error('加载订单详情失败:', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  },

  // 输入快递公司
  onExpressCompanyInput(e) {
    this.setData({ expressCompany: e.detail.value });
  },

  // 输入快递单号
  onExpressNoInput(e) {
    this.setData({ expressNo: e.detail.value });
  },

  // 输入备注
  onRemarkInput(e) {
    this.setData({ shipRemark: e.detail.value });
  },

  // 选择打包前照片
  chooseBeforeImage() {
    wx.chooseImage({
      count: 1,
      success: (res) => {
        this.uploadImage(res.tempFilePaths[0], 'before');
      }
    });
  },

  // 选择打包后照片
  chooseAfterImage() {
    wx.chooseImage({
      count: 1,
      success: (res) => {
        this.uploadImage(res.tempFilePaths[0], 'after');
      }
    });
  },

  // 上传图片
  async uploadImage(filePath, type) {
    wx.showLoading({ title: '上传中...' });
    try {
      const url = await uploadImage(filePath, 'shipping');
      if (type === 'before') {
        this.setData({ packageBeforeImage: url });
      } else {
        this.setData({ packageAfterImage: url });
      }
      wx.hideLoading();
      wx.showToast({
        title: '上传成功',
        icon: 'success'
      });
    } catch (err) {
      wx.hideLoading();
      wx.showToast({
        title: '上传失败',
        icon: 'none'
      });
    }
  },

  // 提交发货
  async submitShip() {
    const { orderId, expressCompany, expressNo } = this.data;

    if (!expressCompany) {
      wx.showToast({
        title: '请输入快递公司',
        icon: 'none'
      });
      return;
    }

    if (!expressNo) {
      wx.showToast({
        title: '请输入快递单号',
        icon: 'none'
      });
      return;
    }

    wx.showModal({
      title: '确认发货',
      content: '确认已填写正确的快递信息？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '提交中...' });
            await shipOrder({
              orderId,
              expressCompany,
              expressNo,
              packageBeforeImage: this.data.packageBeforeImage,
              packageAfterImage: this.data.packageAfterImage,
              shipRemark: this.data.shipRemark
            });
            wx.hideLoading();
            wx.showToast({
              title: '发货成功',
              icon: 'success'
            });
            setTimeout(() => {
              wx.navigateBack();
            }, 1500);
          } catch (err) {
            wx.hideLoading();
            wx.showToast({
              title: err.message || '发货失败',
              icon: 'none'
            });
          }
        }
      }
    });
  }
});

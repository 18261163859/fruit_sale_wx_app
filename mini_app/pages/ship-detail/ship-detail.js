// pages/ship-detail/ship-detail.js
const { getOrderDetail } = require('../../api/order.js');
const { post } = require('../../utils/request.js');

Page({
  data: {
    orderId: null,
    order: null,

    // 物流公司列表
    logisticsList: ['顺丰速运', '中通快递', '圆通速递', '申通快递', '韵达快递', '百世快递', '邮政EMS', '京东物流', '德邦快递', '其他'],
    logisticsIndex: 0,
    logisticsCompany: '顺丰速运',

    // 物流单号
    trackingNumber: '',

    // 发货照片
    shipPhoto1: '',
    shipPhoto2: '',

    // 备注
    remark: '',

    loading: false
  },

  onLoad(options) {
    if (options.orderId) {
      this.setData({ orderId: options.orderId });
      this.loadOrderDetail();
    }
  },

  // 加载订单详情
  async loadOrderDetail() {
    try {
      const res = await getOrderDetail(this.data.orderId);

      if (res.code === 200) {
        this.setData({ order: res.data });
      } else {
        wx.showToast({
          title: res.message || '加载失败',
          icon: 'none'
        });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      }
    } catch (err) {
      console.error('加载订单详情失败:', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }
  },

  // 选择物流公司
  selectLogistics(e) {
    const index = e.detail.value;
    this.setData({
      logisticsIndex: index,
      logisticsCompany: this.data.logisticsList[index]
    });
  },

  // 输入物流单号
  onTrackingNumberInput(e) {
    this.setData({
      trackingNumber: e.detail.value
    });
  },

  // 输入备注
  onRemarkInput(e) {
    this.setData({
      remark: e.detail.value
    });
  },

  // 上传发货照片1
  uploadPhoto1() {
    this.uploadPhoto('shipPhoto1');
  },

  // 上传发货照片2
  uploadPhoto2() {
    this.uploadPhoto('shipPhoto2');
  },

  // 上传照片通用方法
  uploadPhoto(field) {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFilePaths[0];

        // 显示上传中
        wx.showLoading({ title: '上传中...' });

        // 上传到服务器
        wx.uploadFile({
          url: getApp().globalData.baseUrl + '/upload/image',
          filePath: tempFilePath,
          name: 'file',
          header: {
            'Authorization': 'Bearer ' + wx.getStorageSync('token')
          },
          success: (uploadRes) => {
            try {
              const data = JSON.parse(uploadRes.data);
              if (data.code === 200) {
                this.setData({
                  [field]: data.data
                });
                wx.showToast({
                  title: '上传成功',
                  icon: 'success'
                });
              } else {
                wx.showToast({
                  title: data.message || '上传失败',
                  icon: 'none'
                });
              }
            } catch (err) {
              console.error('解析上传结果失败:', err);
              wx.showToast({
                title: '上传失败',
                icon: 'none'
              });
            }
          },
          fail: (err) => {
            console.error('上传失败:', err);
            wx.showToast({
              title: '上传失败',
              icon: 'none'
            });
          },
          complete: () => {
            wx.hideLoading();
          }
        });
      }
    });
  },

  // 删除照片
  deletePhoto(e) {
    const { field } = e.currentTarget.dataset;
    wx.showModal({
      title: '提示',
      content: '确定要删除这张照片吗？',
      success: (res) => {
        if (res.confirm) {
          this.setData({
            [field]: ''
          });
        }
      }
    });
  },

  // 提交发货
  async submitShip() {
    const { orderId, logisticsCompany, trackingNumber, shipPhoto1, shipPhoto2, remark } = this.data;

    // 验证物流单号
    if (!trackingNumber || trackingNumber.trim() === '') {
      wx.showToast({
        title: '请输入物流单号',
        icon: 'none'
      });
      return;
    }

    try {
      this.setData({ loading: true });
      wx.showLoading({ title: '提交中...' });

      const shipData = {
        orderId,
        expressCompany: logisticsCompany,
        expressNo: trackingNumber.trim(),
        packageBeforeImage: shipPhoto1 || '',
        packageAfterImage: shipPhoto2 || '',
        shipRemark: remark || ''
      };

      const res = await post('/order/ship', shipData);

      if (res.code === 200) {
        wx.showToast({
          title: '发货成功',
          icon: 'success',
          duration: 1500
        });

        // 延迟返回上一页
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      } else {
        wx.showToast({
          title: res.message || '发货失败',
          icon: 'none'
        });
      }
    } catch (err) {
      console.error('提交发货失败:', err);
      wx.showToast({
        title: '发货失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
      wx.hideLoading();
    }
  }
});

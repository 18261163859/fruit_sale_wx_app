// pages/address-list/address-list.js
const { getAddressList, deleteAddress, setDefaultAddress } = require('../../api/address.js');

Page({
  data: {
    addressList: [],
    fromPage: '' // 来源页面,如checkout-结算页
  },

  onLoad(options) {
    if (options.from) {
      this.setData({ fromPage: options.from });
    }
    this.loadAddressList();
  },

  onShow() {
    // 从编辑页返回时刷新列表
    this.loadAddressList();
  },

  // 加载地址列表
  async loadAddressList() {
    try {
      wx.showLoading({ title: '加载中...' });
      const res = await getAddressList();
      if (res.code === 200) {
        this.setData({
          addressList: res.data || []
        });
      } else {
        wx.showToast({
          title: res.message || '加载失败',
          icon: 'none'
        });
      }
    } catch (err) {
      console.error('加载地址列表失败:', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
    }
  },

  // 选择地址(结算页返回用)
  selectAddress(e) {
    if (this.data.fromPage === 'checkout') {
      const { address } = e.currentTarget.dataset;
      // 使用Storage传递选中的地址ID
      wx.setStorageSync('selectedAddressId', address.id);
      wx.navigateBack();
    }
  },

  // 新增地址
  addAddress() {
    wx.navigateTo({
      url: '/pages/address-edit/address-edit'
    });
  },

  // 编辑地址
  editAddress(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/address-edit/address-edit?id=${id}`
    });
  },

  // 删除地址
  deleteAddress(e) {
    const { id } = e.currentTarget.dataset;
    wx.showModal({
      title: '提示',
      content: '确定要删除这个地址吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '删除中...' });
            const result = await deleteAddress(id);
            if (result.code === 200) {
              wx.showToast({
                title: '删除成功',
                icon: 'success'
              });
              this.loadAddressList();
            } else {
              wx.showToast({
                title: result.message || '删除失败',
                icon: 'none'
              });
            }
          } catch (err) {
            console.error('删除地址失败:', err);
            wx.showToast({
              title: '删除失败',
              icon: 'none'
            });
          } finally {
            wx.hideLoading();
          }
        }
      }
    });
  },

  // 设置默认地址
  async setDefaultAddress(e) {
    const { id } = e.currentTarget.dataset;
    try {
      wx.showLoading({ title: '设置中...' });
      const res = await setDefaultAddress(id);
      if (res.code === 200) {
        wx.showToast({
          title: '设置成功',
          icon: 'success'
        });
        this.loadAddressList();
      } else {
        wx.showToast({
          title: res.message || '设置失败',
          icon: 'none'
        });
      }
    } catch (err) {
      console.error('设置默认地址失败:', err);
      wx.showToast({
        title: '设置失败',
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
    }
  }
});

// pages/address-edit/address-edit.js
const { getAddressDetail, addAddress, updateAddress } = require('../../api/address.js');

Page({
  data: {
    addressId: null,
    formData: {
      receiverName: '',
      receiverPhone: '',
      province: '',
      city: '',
      district: '',
      detailAddress: '',
      addressTag: '',
      isDefault: 0
    },
    region: ['省份', '城市', '区县'], // 省市区选择器显示
    tagList: ['家', '公司', '学校', '其他']
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ addressId: options.id });
      this.loadAddressDetail(options.id);
    }
    wx.setNavigationBarTitle({
      title: options.id ? '编辑地址' : '新增地址'
    });
  },

  // 加载地址详情
  async loadAddressDetail(id) {
    try {
      wx.showLoading({ title: '加载中...' });
      const res = await getAddressDetail(id);
      if (res.code === 200 && res.data) {
        const address = res.data;
        this.setData({
          formData: {
            receiverName: address.receiverName || '',
            receiverPhone: address.receiverPhone || '',
            province: address.province || '',
            city: address.city || '',
            district: address.district || '',
            detailAddress: address.detailAddress || '',
            addressTag: address.addressTag || '',
            isDefault: address.isDefault || 0
          },
          region: [
            address.province || '省份',
            address.city || '城市',
            address.district || '区县'
          ]
        });
      }
    } catch (err) {
      console.error('加载地址详情失败:', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
    }
  },

  // 输入框变化
  onInputChange(e) {
    const { field } = e.currentTarget.dataset;
    this.setData({
      [`formData.${field}`]: e.detail.value
    });
  },

  // 地区选择
  onRegionChange(e) {
    const region = e.detail.value;
    this.setData({
      region: region,
      'formData.province': region[0],
      'formData.city': region[1],
      'formData.district': region[2]
    });
  },

  // 选择地址标签
  selectTag(e) {
    const { tag } = e.currentTarget.dataset;
    this.setData({
      'formData.addressTag': tag
    });
  },

  // 切换默认地址
  toggleDefault() {
    this.setData({
      'formData.isDefault': this.data.formData.isDefault === 1 ? 0 : 1
    });
  },

  // 表单验证
  validateForm() {
    const { formData } = this.data;
    
    if (!formData.receiverName) {
      wx.showToast({ title: '请输入收货人姓名', icon: 'none' });
      return false;
    }
    
    if (!formData.receiverPhone) {
      wx.showToast({ title: '请输入手机号码', icon: 'none' });
      return false;
    }
    
    // 验证手机号格式
    const phoneReg = /^1[3-9]\d{9}$/;
    if (!phoneReg.test(formData.receiverPhone)) {
      wx.showToast({ title: '手机号码格式不正确', icon: 'none' });
      return false;
    }
    
    if (!formData.province || formData.province === '省份') {
      wx.showToast({ title: '请选择所在地区', icon: 'none' });
      return false;
    }
    
    if (!formData.detailAddress) {
      wx.showToast({ title: '请输入详细地址', icon: 'none' });
      return false;
    }
    
    return true;
  },

  // 保存地址
  async saveAddress() {
    if (!this.validateForm()) {
      return;
    }

    try {
      wx.showLoading({ title: '保存中...' });
      
      const { addressId, formData } = this.data;
      let res;
      
      if (addressId) {
        // 编辑
        res = await updateAddress({
          id: addressId,
          formData
        });
      } else {
        // 新增
        res = await addAddress(formData);
      }
      
      if (res.code === 200) {
        wx.showToast({
          title: '保存成功',
          icon: 'success'
        });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      } else {
        wx.showToast({
          title: res.message || '保存失败',
          icon: 'none'
        });
      }
    } catch (err) {
      console.error('保存地址失败:', err);
      wx.showToast({
        title: '保存失败',
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
    }
  }
});

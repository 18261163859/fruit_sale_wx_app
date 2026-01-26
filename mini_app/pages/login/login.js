// pages/login/login.js
const { wxLogin } = require('../../api/user.js');
const { setToken, setUserInfo } = require('../../utils/storage.js');
const { showToast } = require('../../utils/common.js');

Page({
  data: {
    inviteCode: '',
    phone: '', // 手机号
    userInfo: null, // 存储用户头像和昵称
    redirectUrl: '' // 登录后跳转的页面
  },

  onLoad(options) {
    // 获取邀请码（优先使用URL参数，其次使用缓存）
    let inviteCode = options.inviteCode || wx.getStorageSync('pendingInviteCode') || '';
    console.log("123123123213"+inviteCode)

    if (inviteCode) {
      this.setData({ inviteCode });
      wx.removeStorageSync('pendingInviteCode');
    }

    const redirectUrl = wx.getStorageSync('redirectUrl');
    if (redirectUrl) {
      this.setData({ redirectUrl });
      wx.removeStorageSync('redirectUrl');
    } else if (options.productId) {
      this.setData({
        redirectUrl: `/pages/product-detail/product-detail?id=${options.productId}`
      });
    } else {
      const pendingProductId = wx.getStorageSync('pendingProductId');
      if (pendingProductId) {
        this.setData({
          redirectUrl: `/pages/product-detail/product-detail?id=${pendingProductId}`
        });
        wx.removeStorageSync('pendingProductId');
      }
    }
  },

  // 微信授权登录（带手机号）
  async onWxLoginWithPhone(e) {
    try {
      // 检查是否成功获取手机号授权
      if (e.detail.errMsg !== 'getPhoneNumber:ok') {
        showToast('需要授权手机号才能登录');
        return;
      }

      const phoneCode = e.detail.code;

      if (!phoneCode) {
        showToast('获取手机号失败');
        return;
      }

      // 先获取用户信息（头像、昵称）
      let userInfo = null;
      try {
        const profileRes = await wx.getUserProfile({
          desc: '用于完善会员资料'
        });
        userInfo = profileRes.userInfo;
        this.setData({ userInfo });
      } catch (profileErr) {
        console.log('用户取消授权或获取用户信息失败:', profileErr);
        // 继续登录流程，只是没有头像和昵称
      }

      // 获取微信授权code
      const loginRes = await wx.login();
      const code = loginRes.code;

      if (!code) {
        showToast('获取登录凭证失败');
        return;
      }

      // 调用登录接口，传入phoneCode让后端解密获取真实手机号
      const res = await wxLogin({
        code,
        phoneCode, // 传递手机号code给后端解密
        inviteCode: this.data.inviteCode,
        nickname: userInfo?.nickName || '微信用户',
        avatar: userInfo?.avatarUrl || ''
      });

      // 后端返回格式: { code: 200, data: { userId, token, nickname, ... } }
      if (res.code === 200 && res.data) {
        // 保存token和用户信息
        setToken(res.data.token);
        setUserInfo(res.data);

        showToast('登录成功', 'success');

        // 跳转到指定页面或首页
        setTimeout(() => {
          if (this.data.redirectUrl) {
            wx.redirectTo({
              url: this.data.redirectUrl,
              fail: () => {
                wx.switchTab({
                  url: '/pages/index/index'
                });
              }
            });
          } else {
            wx.switchTab({
              url: '/pages/index/index'
            });
          }
        }, 1500);
      } else {
        showToast(res.message || '登录失败');
      }
    } catch (err) {
      console.error('登录失败:', err);
      showToast('登录失败');
    }
  },

  // 输入邀请码
  onInviteCodeInput(e) {
    this.setData({
      inviteCode: e.detail.value
    });
  },

  // 输入手机号
  onPhoneInput(e) {
    this.setData({
      phone: e.detail.value
    });
  },

  // 手机号登录（测试用）
  async onPhoneLogin() {
    try {
      const phone = this.data.phone.trim();

      if (!phone) {
        showToast('请输入手机号');
        return;
      }

      if (!/^1[3-9]\d{9}$/.test(phone)) {
        showToast('请输入正确的手机号');
        return;
      }

      // 获取微信授权code
      const loginRes = await wx.login();
      const code = loginRes.code;

      if (!code) {
        showToast('获取登录凭证失败');
        return;
      }

      // 调用登录接口，直接传手机号
      const res = await wxLogin({
        code,
        phone: phone, // 直接使用手填的手机号
        inviteCode: this.data.inviteCode,
        nickname: `用户${phone.substr(-4)}`, // 使用手机号后4位生成昵称
        avatar: ''
      });

      // 后端返回格式: { code: 200, data: { userId, token, nickname, ... } }
      if (res.code === 200 && res.data) {
        // 保存token和用户信息
        setToken(res.data.token);
        setUserInfo(res.data);

        showToast('登录成功', 'success');

        // 跳转到指定页面或首页
        setTimeout(() => {
          if (this.data.redirectUrl) {
            wx.redirectTo({
              url: this.data.redirectUrl,
              fail: () => {
                wx.switchTab({
                  url: '/pages/index/index'
                });
              }
            });
          } else {
            wx.switchTab({
              url: '/pages/index/index'
            });
          }
        }, 1500);
      } else {
        showToast(res.message || '登录失败');
      }
    } catch (err) {
      console.error('手机号登录失败:', err);
      showToast('登录失败');
    }
  }
});
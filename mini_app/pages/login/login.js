// pages/login/login.js
const { wxLogin } = require('../../api/user.js');
const { setToken, setUserInfo } = require('../../utils/storage.js');
const { showToast } = require('../../utils/common.js');
const agentApi = require('../../api/agent.js');

Page({
  data: {
    inviteCode: '',
    agentInviteCode: '',
    commissionRate: '',
    phone: '', // mobile number
    userInfo: null, // store user avatar and nickname
    redirectUrl: '', // redirect after login
    showAgentConfirmModal: false
  },

  onLoad(options) {
    let inviteCode = options.inviteCode || wx.getStorageSync('pendingInviteCode') || '';
    console.log("123123123213"+inviteCode)

    if (inviteCode) {
      this.setData({ inviteCode });
      wx.removeStorageSync('pendingInviteCode');
    }

    let agentInviteCode = options.agentInviteCode || wx.getStorageSync('pendingAgentInviteCode') || '';
    let commissionRate = options.commissionRate || wx.getStorageSync('pendingCommissionRate') || '';

    if (agentInviteCode || commissionRate) {
      this.setData({
        agentInviteCode: agentInviteCode || '',
        commissionRate: commissionRate || ''
      });
      wx.setStorageSync('pendingAgentInviteCode', agentInviteCode || '');
      wx.setStorageSync('pendingCommissionRate', commissionRate || '');
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

    // 如果用户已登录且有代理邀请参数（从首页跳转过来），直接显示确认弹窗
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo && options.fromIndex === '1' && agentInviteCode && commissionRate) {
      this.setData({ userInfo });
      this.showAgentInvitationConfirmation();
    }
  },

  // WeChat authorization login (with phone number)
  async onWxLoginWithPhone(e) {
    try {
      // Check if successfully obtained phone number authorization
      if (e.detail.errMsg !== 'getPhoneNumber:ok') {
        showToast('需要授权手机号才能登录');
        return;
      }

      const phoneCode = e.detail.code;

      if (!phoneCode) {
        showToast('获取手机号失败');
        return;
      }

      // Get user info first (avatar, nickname)
      let userInfo = null;
      try {
        const profileRes = await wx.getUserProfile({
          desc: '用于完善会员资料'
        });
        userInfo = profileRes.userInfo;
        this.setData({ userInfo });
      } catch (profileErr) {
        console.log('用户取消授权或获取用户信息失败:', profileErr);
        // Continue login flow, just without avatar and nickname
      }

      // Get WeChat authorization code
      const loginRes = await wx.login();
      const code = loginRes.code;

      if (!code) {
        showToast('获取登录凭证失败');
        return;
      }

      // Call login API, pass phoneCode for backend to decrypt real phone number
      const res = await wxLogin({
        code,
        phoneCode, // pass mobile number code to backend for decryption
        inviteCode: this.data.inviteCode,
        agentInviteCode: this.data.agentInviteCode,
        commissionRate: this.data.commissionRate,
        nickname: userInfo?.nickName || '微信用户',
        avatar: userInfo?.avatarUrl || ''
      });

      // Backend return format: { code: 200, data: { userId, token, nickname, ... } }
      if (res.code === 200 && res.data) {
        // Save token and user info
        setToken(res.data.token);
        setUserInfo(res.data);

        showToast('登录成功', 'success');

        // Check if this is an agent invitation and show confirmation popup
        if (this.data.agentInviteCode && this.data.commissionRate) {
          setTimeout(() => {
            this.showAgentInvitationConfirmation();
          }, 500);
        } else {
          // Normal login flow
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
        }
      } else {
        showToast(res.message || '登录失败');
      }
    } catch (err) {
      console.error('登录失败:', err);
      showToast('登录失败');
    }
  },

  // Input invite code
  onInviteCodeInput(e) {
    this.setData({
      inviteCode: e.detail.value
    });
  },

  // Input phone number
  onPhoneInput(e) {
    this.setData({
      phone: e.detail.value
    });
  },

  // Show agent invitation confirmation popup
  showAgentInvitationConfirmation() {
    this.setData({ showAgentConfirmModal: true });
  },

  // Confirm agent invitation
  async confirmAgentInvitation() {
    const agentInviteCode = this.data.agentInviteCode;
    const commissionRate = this.data.commissionRate;

    if (!agentInviteCode || !commissionRate) {
      showToast('邀请参数无效');
      this.declineAgentInvitation();
      return;
    }

    wx.showLoading({ title: '提交申请中...' });

    try {
      // 调用新接口：被邀请者接受邀请
      const res = await agentApi.acceptAgentInvitation({
        agentInviteCode: agentInviteCode,
        commissionRate: commissionRate
      });

      wx.hideLoading();

      if (res.code === 0 || res.code === 200) {
        showToast('申请成功，等待审核', 'success');
        this.setData({ showAgentConfirmModal: false });

        // Clear pending invitation params
        wx.removeStorageSync('pendingAgentInviteCode');
        wx.removeStorageSync('pendingCommissionRate');
        this.setData({
          agentInviteCode: '',
          commissionRate: ''
        });

        // Redirect to home page
        setTimeout(() => {
          wx.switchTab({ url: '/pages/index/index' });
        }, 1000);
      } else {
        showToast(res.message || '申请失败');
        this.declineAgentInvitation();
      }
    } catch (err) {
      wx.hideLoading();
      console.error('申请失败:', err);
      showToast('申请失败');
      this.declineAgentInvitation();
    }
  },

  // Decline agent invitation
  declineAgentInvitation() {
    this.setData({ showAgentConfirmModal: false });

    // Clear pending invitation params
    wx.removeStorageSync('pendingAgentInviteCode');
    wx.removeStorageSync('pendingCommissionRate');
    this.setData({
      agentInviteCode: '',
      commissionRate: ''
    });

    // 如果用户已登录，直接跳转到首页
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo) {
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' });
      }, 300);
      return;
    }

    // 未登录用户，根据 redirectUrl 决定跳转
    setTimeout(() => {
      if (this.data.redirectUrl) {
        wx.redirectTo({
          url: this.data.redirectUrl,
          fail: () => {
            wx.switchTab({ url: '/pages/index/index' });
          }
        });
      } else {
        wx.switchTab({ url: '/pages/index/index' });
      }
    }, 500);
  }
});
// pages/profile/profile.js
const { getUserInfo } = require('../../api/user.js');
const { getOrderStatistics } = require('../../api/order.js');
const { getCommissionStats } = require('../../api/agent.js');
const { getCustomerServiceConfig, getVipConfig } = require('../../api/config.js');
const { applyTheme } = require('../../utils/theme.js');
const { getUserInfo: getStorageUserInfo, setUserInfo: setStorageUserInfo, clearUserInfo } = require('../../utils/storage.js');

Page({
  data: {
    userInfo: {},
    orderStats: {},
    agentStats: {},
    vipExpireDate: '',
    isVip: false,
    isAgent: false,
    theme: 'normal',
    themeStyle: {},
    themeConfig: {},
    currentTheme: 'normal',
    showInviteCodeDialog: false,
    inputInviteCode: '',
    customerServiceConfig: {
      servicePhone: '',
      serviceWechat: ''
    },
    vipDiscount: 9.5
  },

  onLoad() {
    this.initTheme();
    this.loadUserInfo();
    this.loadOrderStats();
    this.loadAgentStats();
    this.loadCustomerServiceConfig();
    this.loadVipConfig();
  },

  onShow() {
    this.initTheme();
    this.loadUserInfo();
    this.loadOrderStats();
    this.loadAgentStats();
    this.loadVipConfig();
  },

  // 初始化主题
  async initTheme() {
    const userInfo = getStorageUserInfo();
    await applyTheme(this, userInfo);
  },

  async loadUserInfo() {
    try {
      const res = await getUserInfo();
      console.log('=== 用户信息API响应 ===', res);
      if (res.code === 200 && res.data) {
        // 判断是否是VIP（基于 userType）
        const isVip = res.data.userType === 2;

        // 判断是否是代理（基于 agentLevel）
        const isAgent = res.data.agentLevel > 0;

        console.log('最终设置的isVip值:', isVip, '(vipExpireTime:', res.data.vipExpireTime, ')');
        console.log('最终设置的isAgent值:', isAgent);

        // 更新本地存储的用户信息
        setStorageUserInfo(res.data);

        this.setData({
          userInfo: res.data,
          vipExpireDate: '', // 不再显示过期时间
          isVip,
          isAgent
        });

        // 重新应用主题（因为VIP状态可能改变）
        await this.initTheme();
      }
    } catch (err) {
      console.error('加载用户信息失败:', err);
    }
  },

  async loadOrderStats() {
    try {
      const res = await getOrderStatistics();
      if (res.code === 200 && res.data) {
        this.setData({
          orderStats: res.data
        });
      }
    } catch (err) {
      console.error('加载订单统计失败:', err);
    }
  },

  async loadAgentStats() {
    try {
      const res = await getCommissionStats();
      if ((res.code === 0 || res.code === 200) && res.data) {
        const stats = res.data;
        this.setData({
          agentStats: {
            totalCommission: (stats.totalCommission || 0).toFixed(2),
            subAgentCount: stats.subAgentCount || 0,
            subMemberCount: stats.subMemberCount || 0
          }
        });
      }
    } catch (err) {
      console.error('加载代理统计失败:', err);
    }
  },

  async loadCustomerServiceConfig() {
    try {
      const res = await getCustomerServiceConfig();
      if ((res.code === 0 || res.code === 200) && res.data) {
        this.setData({
          customerServiceConfig: {
            servicePhone: res.data.servicePhone || '',
            serviceWechat: res.data.serviceWechat || ''
          }
        });
      }
    } catch (err) {
      console.error('加载客服配置失败:', err);
    }
  },

  async loadVipConfig() {
    try {
      const res = await getVipConfig();
      console.log("返回vip配置")
      console.log(res)
      if ((res.code === 200) && res.data) {
        this.setData({
          vipDiscount: res.data.vipDiscount || 9.5
        });
      }
    } catch (err) {
      console.error('加载VIP配置失败:', err);
    }
  },

  // 跳转到全部订单
  goToOrders() {
    wx.navigateTo({
      url: '/pages/order-list/order-list'
    });
  },

  // 跳转到指定状态的订单
  goToOrdersByStatus(e) {
    const { status } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/order-list/order-list?status=${status}`
    });
  },

  // 跳转到地址管理
  goToAddresses() {
    wx.navigateTo({
      url: '/pages/address-list/address-list'
    });
  },

  // 跳转到积分中心
  goToIntegral() {
    wx.showActionSheet({
      itemList: ['查看积分记录', '积分充值'],
      success: (res) => {
        if (res.tapIndex === 0) {
          // 跳转到积分记录
          wx.navigateTo({
            url: '/pages/integral-records/integral-records'
          });
        } else if (res.tapIndex === 1) {
          // 跳转到积分充值
          wx.navigateTo({
            url: '/pages/integral-recharge/integral-recharge'
          });
        }
      },
      fail: (err) => {
        console.log('用户取消选择:', err);
      }
    });
  },

  // 跳转到代理中心
  goToAgent() {
    wx.navigateTo({
      url: '/pages/my-agent/my-agent'
    });
  },

  // 跳转到开通VIP
  goToOpenVip() {
    // 检查用户是否已经是VIP会员
    if (this.data.isVip) {
      wx.showToast({
        title: '您已经是星享会员了',
        icon: 'none',
        duration: 2000
      });
      return;
    }

    wx.navigateTo({
      url: '/pages/open-vip/open-vip'
    });
  },

  // 联系客服
  goToCustomerService() {
    const { servicePhone, serviceWechat } = this.data.customerServiceConfig;

    let content = '';
    if (servicePhone) {
      content += `客服电话：${servicePhone}\n`;
    }
    if (serviceWechat) {
      content += `客服微信：${serviceWechat}\n`;
    }
    if (!servicePhone && !serviceWechat) {
      content = '暂未配置客服信息';
    }
    content += '\n工作时间：9:00-18:00';

    const buttons = {};
    if (servicePhone) {
      buttons.showCancel = true;
      buttons.confirmText = '拨打电话';
      buttons.cancelText = serviceWechat ? '复制微信' : '取消';
    } else if (serviceWechat) {
      buttons.showCancel = false;
      buttons.confirmText = '复制微信';
    } else {
      buttons.showCancel = false;
      buttons.confirmText = '确定';
    }

    wx.showModal({
      title: '联系客服',
      content: content,
      ...buttons,
      success: (res) => {
        if (res.confirm && servicePhone) {
          // 点击拨打电话
          wx.makePhoneCall({
            phoneNumber: servicePhone.replace(/-/g, '')
          });
        } else if (res.cancel && serviceWechat) {
          // 点击复制微信（当有电话时，取消按钮是复制微信）
          wx.setClipboardData({
            data: serviceWechat,
            success: () => {
              wx.showToast({
                title: '微信号已复制',
                icon: 'success'
              });
            }
          });
        } else if (res.confirm && !servicePhone && serviceWechat) {
          // 只有微信时，确定按钮是复制微信
          wx.setClipboardData({
            data: serviceWechat,
            success: () => {
              wx.showToast({
                title: '微信号已复制',
                icon: 'success'
              });
            }
          });
        }
      }
    });
  },

  // 跳转到发货管理（仅发货人员）
  goToShipping() {
    wx.navigateTo({
      url: '/pages/shipping-list/shipping-list'
    });
  },

  // 跳转到设置
  goToSettings() {
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    });
  },

  // 退出登录
  onLogout() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      confirmColor: '#667eea',
      success: (res) => {
        if (res.confirm) {
          // 清除本地存储的用户信息和token
          clearUserInfo();

          // 显示提示
          wx.showToast({
            title: '已退出登录',
            icon: 'success',
            duration: 1500
          });

          // 跳转到登录页
          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/login/login'
            });
          }, 1500);
        }
      }
    });
  },

  // 显示绑定邀请码弹窗
  showBindInviteCodeDialog() {
    this.setData({
      showInviteCodeDialog: true,
      inputInviteCode: ''
    });
  },

  // 复制我的邀请码
  copyMyInviteCode() {
    const inviteCode = this.data.userInfo.inviteCode;
    if (!inviteCode) {
      wx.showToast({
        title: '暂无邀请码',
        icon: 'none'
      });
      return;
    }

    wx.setClipboardData({
      data: inviteCode,
      success: () => {
        wx.showToast({
          title: '邀请码已复制',
          icon: 'success'
        });
      }
    });
  },

  // 隐藏绑定邀请码弹窗
  hideBindInviteCodeDialog() {
    this.setData({
      showInviteCodeDialog: false,
      inputInviteCode: ''
    });
  },

  // 输入邀请码
  onInviteCodeInput(e) {
    this.setData({
      inputInviteCode: e.detail.value
    });
  },

  // 阻止事件冒泡
  preventClose() {
    // 空函数，阻止事件冒泡到外层
  },

  // 确认绑定邀请码
  async confirmBindInviteCode() {
    const inviteCode = this.data.inputInviteCode.trim();

    if (!inviteCode) {
      wx.showToast({
        title: '请输入邀请码',
        icon: 'none'
      });
      return;
    }

    try {
      wx.showLoading({
        title: '绑定中...'
      });

      const { bindInviteCode } = require('../../api/user.js');
      const res = await bindInviteCode(inviteCode);

      wx.hideLoading();

      if (res.code === 200) {
        wx.showToast({
          title: '绑定成功',
          icon: 'success',
          duration: 2000
        });

        // 关闭弹窗
        this.setData({
          showInviteCodeDialog: false,
          inputInviteCode: ''
        });

        // 重新加载用户信息
        setTimeout(() => {
          this.loadUserInfo();
        }, 2000);
      } else {
        wx.showToast({
          title: res.message || '绑定失败',
          icon: 'none',
          duration: 2000
        });
      }
    } catch (err) {
      wx.hideLoading();
      console.error('绑定邀请码失败:', err);
      wx.showToast({
        title: err.message || '绑定失败，请重试',
        icon: 'none',
        duration: 2000
      });
    }
  }
});

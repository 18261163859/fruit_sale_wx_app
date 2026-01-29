// pages/my-agent/my-agent.js
const agentApi = require('../../api/agent.js');
const userApi = require('../../api/user.js');

Page({
  data: {
    userInfo: {},
    statistics: {},
    activeTab: 'members',  // 默认显示下级会员，后面会根据代理等级调整
    agentList: [],
    memberList: [],
    teamList: [],
    inviteApplications: [],
    showInviteModal: false,
    invitePhone: '',
    inviteCommissionRate: '80.00'
  },

  onLoad() {
    this.loadUserInfo();
    this.loadStatistics();
    this.loadTeamList();
    this.loadInviteApplications();
  },

  onShow() {
    // 每次显示页面时刷新数据
    this.loadStatistics();
  },

  /**
   * 加载用户信息
   */
  loadUserInfo() {
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo) {
      this.setData({ userInfo });
      this.setDefaultTab(userInfo);
    }

    // 从服务器获取最新信息
    userApi.getUserInfo().then(res => {
      if (res.code === 0 || res.code === 200) {
        this.setData({ userInfo: res.data });
        wx.setStorageSync('userInfo', res.data);
        this.setDefaultTab(res.data);
      }
    }).catch(err => {
      console.error('加载用户信息失败:', err);
    });
  },

  /**
   * 根据代理等级设置默认标签页
   */
  setDefaultTab(userInfo) {
    if (userInfo && userInfo.agentLevel === 1) {
      // 一级代理默认显示下级代理
      this.setData({ activeTab: 'agents' });
    } else {
      // 二级代理或其他情况默认显示下级会员
      this.setData({ activeTab: 'members' });
    }
  },

  /**
   * 加载统计信息
   */
  loadStatistics() {
    agentApi.getCommissionStats().then(res => {
      if (res.code === 0 || res.code === 200) {
        const stats = res.data || {};
        this.setData({
          statistics: {
            totalCommission: (stats.totalCommission || 0).toFixed(2),
            subAgentCount: stats.subAgentCount || 0,
            subMemberCount: stats.subMemberCount || 0
          }
        });
      }
    }).catch(err => {
      console.error('加载统计信息失败:', err);
    });
  },

  /**
   * 加载团队列表
   */
  loadTeamList() {
    agentApi.getSubordinates().then(res => {
      if (res.code === 0 || res.code === 200) {
        const teamList = res.data || [];

        // 分类为代理和会员
        const agentList = teamList.filter(item => item.agentLevel > 0);
        const memberList = teamList.filter(item => !item.agentLevel || item.agentLevel === 0);

        // 格式化数据
        const formattedAgents = agentList.map(item => {
          return Object.assign({}, item, {
            totalCommissionAmount: (item.totalCommissionAmount || 0).toFixed(2),
            createTime: this.formatDate(item.createTime)
          });
        });

        const formattedMembers = memberList.map(item => {
          return Object.assign({}, item, {
            totalConsumeAmount: (item.totalConsumeAmount || 0).toFixed(2),
            createTime: this.formatDate(item.createTime)
          });
        });

        this.setData({
          agentList: formattedAgents,
          memberList: formattedMembers,
          teamList
        });
      }
    }).catch(err => {
      console.error('加载团队列表失败:', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    });
  },

  /**
   * 加载邀请申请列表
   */
  loadInviteApplications() {
    agentApi.getMyInviteApplications().then(res => {
      if (res.code === 0 || res.code === 200) {
        const applications = (res.data || []).map(item => {
          return Object.assign({}, item, {
            createTime: this.formatDate(item.createTime),
            reviewTime: this.formatDate(item.reviewTime),
            statusText: item.status === 0 ? '待审核' : (item.status === 1 ? '已通过' : '已拒绝')
          });
        });
        this.setData({ inviteApplications: applications });
      }
    }).catch(err => {
      console.error('加载邀请申请列表失败:', err);
    });
  },

  /**
   * 切换Tab
   */
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
  },

  /**
   * 复制邀请码
   */
  copyInviteCode() {
    wx.setClipboardData({
      data: this.data.userInfo.inviteCode,
      success: () => {
        wx.showToast({
          title: '复制成功',
          icon: 'success'
        });
      }
    });
  },

  /**
   * 前往返现申请页面
   */
  goToCommissionApply() {
    wx.navigateTo({
      url: '/pages/commission-apply/commission-apply'
    });
  },

  /**
   * 分享邀请
   */
  shareInvite() {
    wx.showShareMenu({
      withShareTicket: true,
      menus: ['shareAppMessage', 'shareTimeline']
    });

    wx.showToast({
      title: '请点击右上角分享',
      icon: 'none'
    });
  },

  /**
   * 分享给好友
   */
  onShareAppMessage() {
    const { userInfo, inviteCommissionRate } = this.data;

    // 一级代理分享：邀请成为二级代理
    if (userInfo.agentLevel === 1) {
      const commissionRate = inviteCommissionRate || '80.00';
      return {
        title: '植野集 - 邀请你成为我的下级代理',
        path: `/pages/index/index?agentInviteCode=${userInfo.inviteCode}&commissionRate=${commissionRate}`,
        imageUrl: '/images/share-cover.png'
      };
    }

    // 二级代理分享：只绑定邀请码
    return {
      title: '植野集 - 邀请你一起购买优质水果',
      path: `/pages/index/index?inviteCode=${userInfo.inviteCode}`,
      imageUrl: '/images/share-cover.png'
    };
  },

  /**
   * 格式化日期
   */
  formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  },

  /**
   * 显示邀请弹窗
   */
  showInviteDialog() {
    // 检查是否为一级代理
    if (!this.data.userInfo.agentLevel || this.data.userInfo.agentLevel !== 1) {
      wx.showToast({
        title: '只有一级代理才能邀请二级代理',
        icon: 'none'
      });
      return;
    }

    this.setData({
      showInviteModal: true,
      invitePhone: '',
      inviteCommissionRate: '80.00'
    });
  },

  /**
   * 关闭邀请弹窗
   */
  hideInviteDialog() {
    this.setData({
      showInviteModal: false
    });
  },

  /**
   * 输入手机号
   */
  onPhoneInput(e) {
    this.setData({
      invitePhone: e.detail.value
    });
  },

  /**
   * 输入返现比例
   */
  onCommissionRateInput(e) {
    this.setData({
      inviteCommissionRate: e.detail.value
    });
  },

  /**
   * 确认邀请
   */
  confirmInvite() {
    const { invitePhone, inviteCommissionRate } = this.data;

    // 验证手机号
    if (!invitePhone) {
      wx.showToast({
        title: '请输入手机号',
        icon: 'none'
      });
      return;
    }

    const phoneReg = /^1[3-9]\d{9}$/;
    if (!phoneReg.test(invitePhone)) {
      wx.showToast({
        title: '请输入正确的手机号',
        icon: 'none'
      });
      return;
    }

    // 验证返现比例
    const rate = parseFloat(inviteCommissionRate);
    if (isNaN(rate) || rate <= 0 || rate > 100) {
      wx.showToast({
        title: '返现比例必须在0-100之间',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({
      title: '邀请中...'
    });

    agentApi.inviteSubAgent({
      phone: invitePhone,
      commissionRate: inviteCommissionRate
    }).then(res => {
      wx.hideLoading();

      if (res.code === 0 || res.code === 200) {
        wx.showToast({
          title: '邀请成功',
          icon: 'success'
        });

        // 关闭弹窗
        this.hideInviteDialog();

        // 刷新团队列表、统计和申请列表
        this.loadTeamList();
        this.loadStatistics();
        this.loadInviteApplications();
      } else {
        wx.showToast({
          title: res.message || '邀请失败',
          icon: 'none',
          duration: 2000
        });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error('邀请失败:', err);
      wx.showToast({
        title: err.message || '邀请失败，请重试',
        icon: 'none',
        duration: 2000
      });
    });
  }
});

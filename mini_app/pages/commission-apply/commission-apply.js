// pages/commission-apply/commission-apply.js
const agentApi = require('../../api/agent.js');

Page({
  data: {
    applications: [],
    showApplyModal: false,
    commissionAmount: '',
    bankName: '',
    bankAccount: '',
    accountName: '',
    remark: '',
    statistics: {
      totalCommission: 0,
      availableAmount: 0
    }
  },

  onLoad() {
    this.loadStatistics();
    this.loadApplications();
  },

  onShow() {
    this.loadStatistics();
    this.loadApplications();
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
            availableAmount: (stats.availableAmount || 0).toFixed(2)
          }
        });
      }
    }).catch(err => {
      console.error('加载统计信息失败:', err);
    });
  },

  /**
   * 加载返现申请列表
   */
  loadApplications() {
    wx.showLoading({
      title: '加载中...'
    });

    agentApi.getMyCommissionApplications().then(res => {
      wx.hideLoading();

      if (res.code === 0 || res.code === 200) {
        const applications = (res.data || []).map(item => {
          return Object.assign({}, item, {
            createTime: this.formatDate(item.createTime),
            reviewTime: this.formatDate(item.reviewTime),
            transferTime: this.formatDate(item.transferTime),
            statusText: item.status === 0 ? '待审核' : (item.status === 1 ? '已通过' : (item.status === 2 ? '已拒绝' : '已返现')),
            statusClass: item.status === 0 ? 'pending' : (item.status === 1 ? 'approved' : (item.status === 2 ? 'rejected' : 'transferred'))
          });
        });
        this.setData({ applications });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error('加载返现申请列表失败:', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    });
  },

  /**
   * 显示申请弹窗
   */
  showApplyDialog() {
    this.setData({
      showApplyModal: true,
      commissionAmount: '',
      bankName: '',
      bankAccount: '',
      accountName: '',
      remark: ''
    });
  },

  /**
   * 关闭申请弹窗
   */
  hideApplyDialog() {
    this.setData({
      showApplyModal: false
    });
  },

  /**
   * 输入申请金额
   */
  onAmountInput(e) {
    this.setData({
      commissionAmount: e.detail.value
    });
  },

  /**
   * 输入银行名称
   */
  onBankNameInput(e) {
    this.setData({
      bankName: e.detail.value
    });
  },

  /**
   * 输入银行账号
   */
  onBankAccountInput(e) {
    this.setData({
      bankAccount: e.detail.value
    });
  },

  /**
   * 输入账户名
   */
  onAccountNameInput(e) {
    this.setData({
      accountName: e.detail.value
    });
  },

  /**
   * 输入备注
   */
  onRemarkInput(e) {
    this.setData({
      remark: e.detail.value
    });
  },

  /**
   * 提交申请
   */
  submitApplication() {
    const { commissionAmount, bankName, bankAccount, accountName, remark } = this.data;

    // 验证金额
    if (!commissionAmount) {
      wx.showToast({
        title: '请输入申请金额',
        icon: 'none'
      });
      return;
    }

    const amount = parseFloat(commissionAmount);
    if (isNaN(amount) || amount <= 0) {
      wx.showToast({
        title: '请输入有效的申请金额',
        icon: 'none'
      });
      return;
    }

    // 验证银行信息
    if (!bankName) {
      wx.showToast({
        title: '请输入银行名称',
        icon: 'none'
      });
      return;
    }

    if (!bankAccount) {
      wx.showToast({
        title: '请输入银行账号',
        icon: 'none'
      });
      return;
    }

    if (!accountName) {
      wx.showToast({
        title: '请输入账户名',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({
      title: '提交中...'
    });

    agentApi.submitCommissionApplication({
      commissionAmount: amount,
      bankName,
      bankAccount,
      accountName,
      remark
    }).then(res => {
      wx.hideLoading();

      if (res.code === 0 || res.code === 200) {
        wx.showToast({
          title: '提交成功',
          icon: 'success'
        });

        // 关闭弹窗
        this.hideApplyDialog();

        // 刷新列表
        this.loadApplications();
      } else {
        wx.showToast({
          title: res.message || '提交失败',
          icon: 'none',
          duration: 2000
        });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error('提交返现申请失败:', err);
      wx.showToast({
        title: err.message || '提交失败，请重试',
        icon: 'none',
        duration: 2000
      });
    });
  },

  /**
   * 格式化日期
   */
  formatDate(timestamp) {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
});

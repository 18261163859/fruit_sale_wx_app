#!/bin/bash

# 微信小程序页面快速生成脚本
# 为所有页面生成基础代码

cd /Users/shengmingxing/fruit_sale/mini_app/pages

# 定义所有页面及其配置
declare -A pages=(
  ["category"]="分类"
  ["product-detail"]="商品详情"
  ["search"]="搜索"
  ["checkout"]="结算"
  ["profile"]="我的"
  ["order-list"]="订单列表"
  ["order-detail"]="订单详情"
  ["address-list"]="地址列表"
  ["address-edit"]="地址编辑"
  ["my-agent"]="我的代理"
  ["commission-list"]="返现记录"
  ["agent-apply"]="申请代理"
  ["integral-recharge"]="积分充值"
  ["integral-records"]="积分记录"
  ["open-vip"]="开通星享会员"
)

# 为每个页面生成基础文件
for page in "${!pages[@]}"; do
  title="${pages[$page]}"

  # 生成 .json 配置文件
  cat > "$page/$page.json" << EOF
{
  "navigationBarTitleText": "$title",
  "enablePullDownRefresh": true
}
EOF

  # 生成基础 .wxml 文件
  cat > "$page/$page.wxml" << EOF
<!--pages/$page/$page.wxml-->
<view class="container" style="background-color: {{themeStyle.backgroundColor}}">
  <view class="content">
    <text style="color: {{themeStyle.textColor}}">$title 页面</text>
  </view>
</view>
EOF

  # 生成基础 .wxss 文件
  cat > "$page/$page.wxss" << EOF
/* pages/$page/$page.wxss */
.container {
  min-height: 100vh;
  padding: 20rpx;
}

.content {
  padding: 40rpx;
  text-align: center;
}
EOF

  # 生成基础 .js 文件
  cat > "$page/$page.js" << EOF
// pages/$page/$page.js
const { applyTheme } = require('../../utils/theme.js');
const { getUserInfo } = require('../../utils/storage.js');

Page({
  data: {
    theme: 'normal',
    themeStyle: {}
  },

  onLoad(options) {
    this.initTheme();
  },

  onShow() {
    this.initTheme();
  },

  initTheme() {
    const userInfo = getUserInfo();
    applyTheme(this, userInfo);
  },

  async loadData() {
    // 加载数据
  },

  async onPullDownRefresh() {
    await this.loadData();
    wx.stopPullDownRefresh();
  }
});
EOF

  echo "Generated $page"
done

echo "All pages generated successfully!"
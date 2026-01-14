# 微信小程序页面开发模板

## 快速创建页面

每个页面需要创建4个文件,以下是标准模板:

## 1. page-name.js (页面逻辑)

```javascript
// pages/page-name/page-name.js
const { applyTheme } = require('../../utils/theme.js');
const { getUserInfo } = require('../../utils/storage.js');
const { showToast, navigateTo } = require('../../utils/common.js');

Page({
  data: {
    theme: 'normal',
    themeStyle: {},
    // 页面数据
  },

  onLoad(options) {
    this.initTheme();
    this.initData(options);
  },

  onShow() {
    this.initTheme();
    this.loadData();
  },

  // 初始化主题
  initTheme() {
    const userInfo = getUserInfo();
    applyTheme(this, userInfo);
  },

  // 初始化数据
  initData(options) {
    // 处理页面参数
  },

  // 加载数据
  async loadData() {
    try {
      // 调用API加载数据
    } catch (err) {
      console.error('加载数据失败:', err);
    }
  },

  // 下拉刷新
  async onPullDownRefresh() {
    await this.loadData();
    wx.stopPullDownRefresh();
  }
});
```

## 2. page-name.wxml (页面结构)

```xml
<!--pages/page-name/page-name.wxml-->
<view class="container" style="background-color: {{themeStyle.backgroundColor}}">
  <!-- 页面内容 -->
  <view class="content">
    <text style="color: {{themeStyle.textColor}}">页面内容</text>
  </view>
</view>
```

## 3. page-name.wxss (页面样式)

```css
/* pages/page-name/page-name.wxss */
.container {
  min-height: 100vh;
  padding: 20rpx;
}

.content {
  /* 内容样式 */
}
```

## 4. page-name.json (页面配置)

```json
{
  "navigationBarTitleText": "页面标题",
  "enablePullDownRefresh": true,
  "backgroundTextStyle": "dark"
}
```

## 常用组件示例

### 列表组件

```xml
<view class="list">
  <view
    class="list-item"
    wx:for="{{list}}"
    wx:key="id"
    bindtap="onItemTap"
    data-id="{{item.id}}"
    style="background-color: {{themeStyle.cardBackground}}"
  >
    <text style="color: {{themeStyle.textColor}}">{{item.name}}</text>
  </view>
</view>
```

### 按钮组件

```xml
<button
  class="btn-primary"
  bindtap="onSubmit"
  style="background-color: {{themeStyle.buttonColor}}; color: {{themeStyle.buttonTextColor}}"
>
  提交
</button>
```

### 空状态组件

```xml
<view class="empty-state" wx:if="{{list.length === 0}}">
  <image class="empty-image" src="/images/empty.png"></image>
  <text class="empty-text">暂无数据</text>
</view>
```

### 加载状态

```xml
<view class="loading" wx:if="{{loading}}">
  <text>加载中...</text>
</view>
```

## 常用样式

### 卡片样式

```css
.card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}
```

### 列表样式

```css
.list {
  padding: 20rpx;
}

.list-item {
  padding: 30rpx;
  margin-bottom: 20rpx;
  border-radius: 12rpx;
  background-color: #ffffff;
}

.list-item:last-child {
  margin-bottom: 0;
}
```

### 按钮样式

```css
.btn-primary {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: bold;
  border: none;
}

.btn-primary::after {
  border: none;
}
```

### 空状态样式

```css
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
}
```

## API调用示例

### GET请求

```javascript
const { getList } = require('../../api/xxx.js');

async loadData() {
  try {
    const res = await getList({ page: 1, pageSize: 20 });
    if (res.success) {
      this.setData({
        list: res.data.list || []
      });
    }
  } catch (err) {
    console.error('加载失败:', err);
  }
}
```

### POST请求

```javascript
const { submitData } = require('../../api/xxx.js');

async onSubmit() {
  const { formData } = this.data;

  // 表单验证
  if (!formData.name) {
    return showToast('请输入名称');
  }

  try {
    const res = await submitData(formData);
    if (res.success) {
      showToast('提交成功', 'success');
      wx.navigateBack();
    }
  } catch (err) {
    console.error('提交失败:', err);
  }
}
```

## 事件处理示例

### 列表项点击

```javascript
onItemTap(e) {
  const { id } = e.currentTarget.dataset;
  navigateTo(`/pages/detail/detail?id=${id}`);
}
```

### 表单输入

```javascript
onInputChange(e) {
  const { field } = e.currentTarget.dataset;
  this.setData({
    [`formData.${field}`]: e.detail.value
  });
}
```

### 图片选择

```javascript
async onChooseImage() {
  try {
    const res = await wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera']
    });

    this.setData({
      imageUrl: res.tempFilePaths[0]
    });
  } catch (err) {
    console.error('选择图片失败:', err);
  }
}
```

## 页面跳转示例

### 普通跳转

```javascript
navigateTo('/pages/detail/detail?id=123');
```

### Tab页跳转

```javascript
switchTab('/pages/index/index');
```

### 返回上一页

```javascript
wx.navigateBack({ delta: 1 });
```

### 重定向

```javascript
redirectTo('/pages/login/login');
```

## 主题样式使用

所有页面都应该使用主题样式:

```xml
<!-- 背景色 -->
<view style="background-color: {{themeStyle.backgroundColor}}">

<!-- 文字颜色 -->
<text style="color: {{themeStyle.textColor}}">文本</text>

<!-- 次要文字颜色 -->
<text style="color: {{themeStyle.subTextColor}}">次要文本</text>

<!-- 主色调 -->
<text style="color: {{themeStyle.primaryColor}}">强调文本</text>

<!-- 按钮 -->
<button style="background-color: {{themeStyle.buttonColor}}; color: {{themeStyle.buttonTextColor}}">
  按钮
</button>

<!-- 卡片背景 -->
<view style="background-color: {{themeStyle.cardBackground}}">卡片</view>
```

## 开发流程

1. 创建页面文件(4个文件)
2. 在 app.json 中注册页面路由
3. 实现页面逻辑(.js)
4. 编写页面结构(.wxml)
5. 添加页面样式(.wxss)
6. 配置页面属性(.json)
7. 测试功能和样式
8. 适配主题切换
9. 处理边界情况
10. 优化用户体验

## 注意事项

1. 所有页面必须支持主题切换
2. 统一使用封装的API进行请求
3. 统一使用工具函数处理通用逻辑
4. 统一的错误处理和提示
5. 适配不同屏幕尺寸
6. 处理网络异常情况
7. 添加加载状态和空状态
8. 做好表单验证
9. 优化用户体验细节
10. 保持代码风格一致

## 常见问题

### 1. 页面不显示
- 检查是否在 app.json 中注册
- 检查路径是否正确
- 检查文件名是否一致

### 2. 样式不生效
- 检查 wxss 文件是否创建
- 检查选择器是否正确
- 检查是否有样式冲突

### 3. 数据不更新
- 检查是否使用 setData
- 检查数据路径是否正确
- 检查是否在正确的生命周期调用

### 4. API调用失败
- 检查后端接口是否正常
- 检查请求参数是否正确
- 检查Token是否有效

### 5. 主题不生效
- 检查是否调用 initTheme
- 检查是否使用 themeStyle
- 检查主题配置是否正确